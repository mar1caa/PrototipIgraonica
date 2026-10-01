/*
 * ================================================================
 * FAJL: EmailService.java
 * SVRHA: Sastavlja i šalje email obaveštenja roditelju.
 * GDE MENJATI: OVDE NAJLAKŠE MENJAŠ NASLOV I TEKST PORUKA. Slanje je bezbedno isključeno dok app.mail.enabled=false.
 * ================================================================
 */
package rs.blabla.igraonica.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import rs.blabla.igraonica.model.Booking;
import rs.blabla.igraonica.model.BookingStatus;
import rs.blabla.igraonica.model.PlayroomSettings;
import rs.blabla.igraonica.repository.PlayroomSettingsRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final PlayroomSettingsRepository settingsRepository;

    // Glavni prekidač. Dok je false, NIJEDAN email se stvarno ne šalje.
    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from:}")
    private String configuredFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * EMAIL #1 — roditelj je poslao zahtev.
     * MENJAJ SUBJECT i BODY ispod ako želiš drugačiji ton poruke.
     */
    public boolean sendRequestReceived(Booking booking) {
        String name = playroomName();
        String subject = "Primili smo vaš zahtev za rođendan – " + name;
        String body = "Zdravo " + booking.getParentName() + ",\n\n"
                + "primili smo vaš zahtev za rođendan i javićemo vam se radi konačne potvrde termina.\n\n"
                + bookingDetails(booking)
                + "\nVažno: slanje zahteva ne znači da je termin još konačno rezervisan.\n\n"
                + "Hvala,\n" + name;
        return send(booking.getEmail(), subject, body);
    }

    /**
     * EMAIL #2/#3/#4 — potvrda, odbijanje ili otkazivanje.
     * Svaki status ima poseban subject/body da ih lako možeš menjati.
     */
    public boolean sendStatusChanged(Booking booking) {
        String name = playroomName();
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            String subject = "Vaš rođendan je potvrđen – " + name;
            String body = "Zdravo " + booking.getParentName() + ",\n\n"
                    + "vaša rezervacija rođendana je potvrđena. 🎉\n\n"
                    + bookingDetails(booking)
                    + "\nRadujemo se proslavi!\n\n" + name;
            return send(booking.getEmail(), subject, body);
        }
        if (booking.getStatus() == BookingStatus.REJECTED) {
            String subject = "Informacija o zahtevu za rođendan – " + name;
            String body = "Zdravo " + booking.getParentName() + ",\n\n"
                    + "nažalost, poslati zahtev trenutno ne možemo da potvrdimo. Molimo vas da nas kontaktirate kako bismo proverili drugi termin.\n\n"
                    + bookingDetails(booking)
                    + "\n" + name;
            return send(booking.getEmail(), subject, body);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            String subject = "Rezervacija rođendana je otkazana – " + name;
            String body = "Zdravo " + booking.getParentName() + ",\n\n"
                    + "obaveštavamo vas da je rezervacija rođendana otkazana. Za novi termin možete nas kontaktirati ili poslati novi zahtev.\n\n"
                    + bookingDetails(booking)
                    + "\n" + name;
            return send(booking.getEmail(), subject, body);
        }
        return false;
    }

    // Zajednički blok detalja koji se dodaje u sve email poruke.
    private String bookingDetails(Booking booking) {
        String extras = booking.getExtras() == null || booking.getExtras().isEmpty()
                ? "Nema"
                : booking.getExtras().stream().map(e -> e.getName()).collect(Collectors.joining(", "));
        String menu = booking.getMenuOption() == null ? "—" : booking.getMenuOption().getName();
        String price = booking.getEstimatedPrice() == null ? "—" : booking.getEstimatedPrice().stripTrailingZeros().toPlainString() + " RSD";

        // Ako je prijavljeno manje od 12 dece, roditelju dodatno naglašavamo poslovno pravilo minimuma.
        String minimumChargeNote = booking.getNumberOfChildren() != null && booking.getNumberOfChildren() < 12
                ? "Napomena: minimalna naplata menija obračunava se za 12 dece.\n"
                : "";

        return "Datum: " + booking.getDisplayDate() + "\n"
                + "Termin: " + booking.getTimeSlot() + "\n"
                + "Dete: " + booking.getChildName() + "\n"
                + "Broj dece: " + booking.getNumberOfChildren() + "\n"
                + "Meni: " + menu + "\n"
                + "Dodatne usluge: " + extras + "\n"
                + "Okvirna cena: " + price + "\n"
                + minimumChargeNote
                + "Napomena: piće i druga potrošnja odraslih gostiju nisu uključeni u okvirnu cenu.\n";
    }

    // Ime se čita iz admin podešavanja; fallback je demo naziv.
    private String playroomName() {
        return settingsRepository.findById(1L)
                .map(PlayroomSettings::getName)
                .filter(n -> n != null && !n.isBlank())
                .orElse("BlaBla Igraonica");
    }

    /**
     * Jedino mesto koje fizički šalje poruku preko SMTP-a.
     * Ako je slanje isključeno ili SMTP nije podešen, aplikacija NE puca.
     */
    private boolean send(String to, String subject, String body) {
        if (!enabled || to == null || to.isBlank()) {
            return false;
        }
        String from = configuredFrom == null || configuredFrom.isBlank() ? mailUsername : configuredFrom;
        if (from == null || from.isBlank()) {
            log.warn("Email slanje je uključeno, ali MAIL_FROM/MAIL_USERNAME nije podešen.");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            log.warn("Email nije poslat na {}: {}", to, ex.getMessage());
            return false;
        }
    }
}
