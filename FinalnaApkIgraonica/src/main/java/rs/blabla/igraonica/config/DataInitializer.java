/*
 * ================================================================
 * FAJL: DataInitializer.java
 * SVRHA: Ubaci početne/demo podatke u bazu ako ih nema i migrira stare demo cene.
 * GDE MENJATI: Ovde menjaj podrazumevane cene, termine, dodatne usluge i početne podatke igraonice.
 * ================================================================
 */
package rs.blabla.igraonica.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.blabla.igraonica.model.*;
import rs.blabla.igraonica.repository.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final PlayroomSettingsRepository settings;
    private final MenuOptionRepository menus;
    private final ExtraServiceRepository extras;
    private final TimeSlotRepository timeSlots;

    @Value("${app.owner.email}") private String ownerEmail;
    @Value("${app.owner.password}") private String ownerPassword;
    @Value("${app.employee.email}") private String employeeEmail;
    @Value("${app.employee.password}") private String employeePassword;

    // Ove vrednosti se čitaju iz application.properties / environment varijabli.
    // Ne upisuj prave lozinke direktno u Java kod.

    @Override
    public void run(String... args) {
        // Kreiraj OWNER nalog samo ako već ne postoji.
        users.findByEmail(ownerEmail).orElseGet(() -> users.save(AppUser.builder()
                .name("Vlasnik")
                .email(ownerEmail)
                .password(encoder.encode(ownerPassword))
                .role(UserRole.OWNER)
                .enabled(true)
                .build()));

        // Kreiraj EMPLOYEE nalog samo ako već ne postoji.
        users.findByEmail(employeeEmail).orElseGet(() -> users.save(AppUser.builder()
                .name("Radnik")
                .email(employeeEmail)
                .password(encoder.encode(employeePassword))
                .role(UserRole.EMPLOYEE)
                .enabled(true)
                .build()));

        // POČETNI PODACI IGRAONICE — menjaj tekstove/adresu za pravi lokal.
        if (settings.count() == 0) {
            settings.save(PlayroomSettings.builder()
                    .id(1L)
                    .name("BlaBla Igraonica")
                    .city("Niš")
                    .address("Bulevar BlaBla 12, Niš")
                    .phone("060 123 45 67")
                    .email("zdravo@blabla.rs")
                    .instagram("@blablaigraonica")
                    .heroTitle("Rođendan bez komplikacija, uz više vremena za slavlje.")
                    .heroSubtitle("Proslava traje 1,5h. Izaberite meni, proverite termin i pošaljite zahtev za rezervaciju za nekoliko minuta.")
                    .shortDescription("Moderna igraonica i prijatan kutak za roditelje u Nišu.")
                    .aboutText("Prostor je zamišljen tako da deca imaju bezbednu zonu za igru, dok roditelji mogu da se opuste u kafiću. Svaki rođendan uključuje igraonicu, animatorke, muziku, grickalice i sokiće za decu, a cena zavisi od izabranog menija i broja dece.")
                    .seoTitle("BlaBla Igraonica Niš | Rođendani i zakazivanje")
                    .seoDescription("BlaBla Igraonica u Nišu – dečji rođendani, meni po detetu, dodatne usluge i online zahtev za rezervaciju termina.")
                    .build());
        }

        // POČETNI MENIJI — ovde su demo cene po detetu.
        if (menus.count() == 0) {
            menus.save(MenuOption.builder()
                    .name("Kiflice meni")
                    .pricePerChild(new BigDecimal("800"))
                    .description("Kiflice za decu")
                    .active(true)
                    .build());
            menus.save(MenuOption.builder()
                    .name("Pizza meni")
                    .pricePerChild(new BigDecimal("850"))
                    .description("Pizza za decu")
                    .active(true)
                    .build());
            menus.save(MenuOption.builder()
                    .name("Mini burger meni")
                    .pricePerChild(new BigDecimal("950"))
                    .description("Mini burgeri za decu")
                    .active(true)
                    .build());
        } else {
            migrateLegacyDemoMenus();
        }


        // POČETNI TERMINI — svaki traje 90 min, vidi TimeSlot.java.
        if (timeSlots.count() == 0) {
            timeSlots.save(TimeSlot.builder().startTime(LocalTime.of(12, 0)).active(true).build());
            timeSlots.save(TimeSlot.builder().startTime(LocalTime.of(15, 0)).active(true).build());
            timeSlots.save(TimeSlot.builder().startTime(LocalTime.of(18, 0)).active(true).build());
        }

        // POČETNE DODATNE USLUGE — admin ih kasnije može menjati bez menjanja koda.
        if (extras.count() == 0) {
            extras.save(ExtraService.builder().name("Face painting").emoji("🎨").price(new BigDecimal("3000")).description("Oslikavanje lica dece tokom proslave.").active(true).build());
            extras.save(ExtraService.builder().name("Maskota").emoji("🦸").price(new BigDecimal("4500")).description("Dolazak maskote i druženje sa decom.").active(true).build());
            extras.save(ExtraService.builder().name("Fotograf").emoji("📸").price(new BigDecimal("6000")).description("Fotografisanje najlepših trenutaka sa rođendana.").active(true).build());
            extras.save(ExtraService.builder().name("Ketering za roditelje").emoji("🥪").price(new BigDecimal("8000")).description("Dodatni ketering paket za odrasle goste.").active(true).build());
        }
    }

    // Ova migracija samo stare demo cene 350/450/550 prebacuje na nove.
    // Ako je vlasnik već ručno promenio cenu, ova metoda je ne dira.
    private void migrateLegacyDemoMenus() {
        for (MenuOption menu : menus.findAll()) {
            boolean changed = false;
            if ("Kiflice meni".equalsIgnoreCase(menu.getName())) {
                if (new BigDecimal("350").compareTo(menu.getPricePerChild()) == 0) { menu.setPricePerChild(new BigDecimal("800")); changed = true; }
                if ("Kiflice + sok".equalsIgnoreCase(menu.getDescription())) { menu.setDescription("Kiflice za decu"); changed = true; }
            } else if ("Pizza meni".equalsIgnoreCase(menu.getName())) {
                if (new BigDecimal("450").compareTo(menu.getPricePerChild()) == 0) { menu.setPricePerChild(new BigDecimal("850")); changed = true; }
                if ("Pizza + sok".equalsIgnoreCase(menu.getDescription())) { menu.setDescription("Pizza za decu"); changed = true; }
            } else if ("Mini burger meni".equalsIgnoreCase(menu.getName())) {
                if (new BigDecimal("550").compareTo(menu.getPricePerChild()) == 0) { menu.setPricePerChild(new BigDecimal("950")); changed = true; }
                if ("Mini burgeri + sok".equalsIgnoreCase(menu.getDescription())) { menu.setDescription("Mini burgeri za decu"); changed = true; }
            }
            if (changed) menus.save(menu);
        }
    }
}
