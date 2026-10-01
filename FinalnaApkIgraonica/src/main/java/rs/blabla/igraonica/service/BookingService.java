/*
 * ================================================================
 * FAJL: BookingService.java
 * SVRHA: Poslovna logika rezervacije: kreiranje, cena, preklapanje termina i status.
 * GDE MENJATI: Ovde menjaj pravila obračuna, minimum dece ili pravila zauzetosti.
 * ================================================================
 */
package rs.blabla.igraonica.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.blabla.igraonica.model.Booking;
import rs.blabla.igraonica.model.BookingStatus;
import rs.blabla.igraonica.model.ExtraService;
import rs.blabla.igraonica.model.MenuOption;
import rs.blabla.igraonica.repository.BookingRepository;
import rs.blabla.igraonica.repository.ExtraServiceRepository;
import rs.blabla.igraonica.repository.MenuOptionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    // Ako se minimum ikada promeni, promeni i MINIMUM_BILLED_CHILDREN u booking.js logici.
    private static final int MINIMUM_BILLED_CHILDREN = 12;

    private final BookingRepository bookings;
    private final MenuOptionRepository menus;
    private final ExtraServiceRepository extras;

    /**
     * Kreira novi zahtev sa statusom PENDING.
     * Cena se računa u trenutku slanja i čuva uz rezervaciju kao istorijska procena.
     */
    @Transactional
    public Booking create(
            String parentName,
            String phone,
            String email,
            String childName,
            Integer childAge,
            Integer numberOfChildren,
            LocalDate date,
            String timeSlot,
            Long menuId,
            List<Long> extraIds,
            String parentNote
    ) {
        // Meni mora da postoji; ako ID nije validan, zahtev se ne čuva.
        MenuOption menu = menus.findById(menuId).orElseThrow();

        // Ako roditelj nije izabrao dodatke, koristimo praznu listu.
        List<ExtraService> selectedExtras = extraIds == null
                ? new ArrayList<>()
                : extras.findAllById(extraIds);

        BigDecimal total = calculate(menu, selectedExtras, numberOfChildren);

        Booking booking = Booking.builder()
                .parentName(parentName)
                .phone(phone)
                .email(email)
                .childName(childName)
                .childAge(childAge)
                .numberOfChildren(numberOfChildren)
                .date(date)
                .timeSlot(timeSlot)
                .menuOption(menu)
                .extras(selectedExtras)
                .parentNote(parentNote)
                .status(BookingStatus.PENDING)
                .estimatedPrice(total)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return bookings.save(booking);
    }

    /**
     * Proverava da li se kandidat termin preklapa sa PENDING ili CONFIRMED rezervacijom.
     * REJECTED i CANCELLED rezervacije ne blokiraju termin.
     */
    public boolean isSlotUnavailable(LocalDate date, String candidateSlot) {
        List<Booking> existing = bookings.findByDateAndStatusIn(
                date,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );

        LocalTime[] candidate = parseSlot(candidateSlot);

        for (Booking booking : existing) {
            if (booking.getTimeSlot() == null) {
                continue;
            }

            // Najjednostavniji slučaj: potpuno isti tekst termina.
            if (booking.getTimeSlot().equals(candidateSlot)) {
                return true;
            }

            // Dodatna zaštita: prepoznaje i delimično preklapanje vremena.
            LocalTime[] reserved = parseSlot(booking.getTimeSlot());
            if (candidate != null && reserved != null) {
                boolean overlaps = candidate[0].isBefore(reserved[1])
                        && reserved[0].isBefore(candidate[1]);
                if (overlaps) {
                    return true;
                }
            }
        }

        return false;
    }

    // Pretvara tekst "12:00 - 13:30" u početno i krajnje vreme radi provere preklapanja.
    private LocalTime[] parseSlot(String slot) {
        try {
            String[] parts = slot.split("\\s*-\\s*");
            if (parts.length != 2) {
                return null;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return new LocalTime[]{
                    LocalTime.parse(parts[0].trim(), formatter),
                    LocalTime.parse(parts[1].trim(), formatter)
            };
        } catch (Exception ignored) {
            // Stari ili ručno upisan termin možda nije u očekivanom formatu.
            // U tom slučaju ga ne parsiramo, već se oslanjamo na proveru identičnog teksta iznad.
            return null;
        }
    }

    /**
     * OKVIRNA CENA:
     * 1) meni po detetu × broj dece za naplatu,
     * 2) minimum naplate je 12 dece,
     * 3) zatim se dodaju fiksne cene izabranih dodatnih usluga.
     *
     * Piće odraslih gostiju se ovde namerno NE računa jer zavisi od stvarne potrošnje u lokalu.
     */
    public BigDecimal calculate(MenuOption menu, List<ExtraService> selectedExtras, Integer children) {
        int actualChildren = children == null ? 0 : children;
        int billedChildren = Math.max(MINIMUM_BILLED_CHILDREN, actualChildren);

        BigDecimal total = menu.getPricePerChild()
                .multiply(BigDecimal.valueOf(billedChildren));

        if (selectedExtras != null) {
            for (ExtraService extra : selectedExtras) {
                total = total.add(extra.getPrice());
            }
        }

        return total;
    }

    /** Menja status i internu napomenu rezervacije. */
    @Transactional
    public Booking updateStatus(Long id, BookingStatus status, String internalNote) {
        Booking booking = bookings.findById(id).orElseThrow();
        booking.setStatus(status);
        booking.setInternalNote(internalNote);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookings.save(booking);
    }
}
