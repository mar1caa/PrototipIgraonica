/*
 * ================================================================
 * FAJL: BookingController.java
 * SVRHA: Javna forma za zakazivanje, provera dostupnosti termina i kreiranje zahteva.
 * GDE MENJATI: Ovde menjaj validacije booking forme i tok nakon slanja zahteva.
 * ================================================================
 */
package rs.blabla.igraonica.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rs.blabla.igraonica.model.TimeSlot;
import rs.blabla.igraonica.repository.*;
import rs.blabla.igraonica.service.BookingService;
import rs.blabla.igraonica.service.EmailService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class BookingController {
    private final PlayroomSettingsRepository settings;
    private final MenuOptionRepository menus;
    private final ExtraServiceRepository extras;
    private final TimeSlotRepository timeSlots;
    private final BookingService bookingService;
    private final EmailService emailService;

    /** Učitava javnu booking formu i samo AKTIVNE menije/usluge/termine. */
    @GetMapping("/booking")
    public String form(Model model) {
        model.addAttribute("settings", settings.findById(1L).orElse(null));
        model.addAttribute("menus", menus.findByActiveTrueOrderByNameAsc());
        model.addAttribute("extras", extras.findByActiveTrueOrderByNameAsc());
        model.addAttribute("slots", timeSlots.findByActiveTrueOrderByStartTimeAsc());
        return "booking";
    }

    /** AJAX endpoint koji booking.js poziva kada roditelj izabere datum. */
    @GetMapping("/booking/availability")
    @ResponseBody
    public Map<String, Object> availability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<String> slotLabels = timeSlots.findByActiveTrueOrderByStartTimeAsc().stream()
                .map(TimeSlot::getLabel)
                .toList();
        List<String> unavailable = slotLabels.stream()
                .filter(slot -> bookingService.isSlotUnavailable(date, slot))
                .toList();
        return Map.of("date", date.toString(), "unavailable", unavailable);
    }

    /** Prima podatke iz forme, ponovo proverava termin na serveru i čuva zahtev. */
    @PostMapping("/booking")
    public String create(
            @RequestParam String parentName,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String childName,
            @RequestParam Integer childAge,
            @RequestParam Integer numberOfChildren,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
            @RequestParam String timeSlot,
            @RequestParam Long menuId,
            @RequestParam(required = false) List<Long> extraIds,
            @RequestParam(required = false, defaultValue = "") String parentNote,
            RedirectAttributes ra) {

        // Server-side validacija je obavezna čak i kada browser već proverava datum.
        if (date.isBefore(LocalDate.now())) {
            ra.addFlashAttribute("error", "Datum ne može biti u prošlosti.");
            return "redirect:/booking";
        }

        List<String> allowedSlots = timeSlots.findByActiveTrueOrderByStartTimeAsc().stream()
                .map(TimeSlot::getLabel)
                .toList();

        if (!allowedSlots.contains(timeSlot)) {
            ra.addFlashAttribute("error", "Izaberite važeći termin.");
            return "redirect:/booking";
        }

        if (bookingService.isSlotUnavailable(date, timeSlot)) {
            ra.addFlashAttribute("error", "Taj termin je u međuvremenu zauzet. Izaberite drugi termin.");
            return "redirect:/booking";
        }

        // Kreiranje rezervacije je u BookingService-u; controller samo upravlja HTTP tokom.
        var booking = bookingService.create(parentName, phone, email, childName, childAge, numberOfChildren,
                date, timeSlot, menuId, extraIds, parentNote);
        // Ako je email isključen, metoda samo vrati false i rezervacija ipak ostaje sačuvana.
        boolean emailSent = emailService.sendRequestReceived(booking);
        ra.addFlashAttribute("emailSent", emailSent);
        return "redirect:/booking/success";
    }

    @GetMapping("/booking/success")
    public String success(Model model) {
        model.addAttribute("settings", settings.findById(1L).orElse(null));
        return "booking-success";
    }
}
