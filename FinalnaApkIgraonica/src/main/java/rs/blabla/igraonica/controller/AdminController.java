/*
 * ================================================================
 * FAJL: AdminController.java
 * SVRHA: Admin dashboard, detalj rezervacije i promena statusa rezervacije.
 * GDE MENJATI: Ovde dodaj logiku vezanu za potvrdu/odbijanje/otkazivanje rezervacija i obaveštenja.
 * ================================================================
 */
package rs.blabla.igraonica.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rs.blabla.igraonica.model.*;
import rs.blabla.igraonica.repository.BookingRepository;
import rs.blabla.igraonica.service.BookingService;
import rs.blabla.igraonica.service.EmailService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final BookingRepository bookings;
    private final BookingService bookingService;
    private final EmailService emailService;

    /** Priprema statistiku i liste rezervacija za glavni admin ekran. */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pendingCount", bookings.countByStatus(BookingStatus.PENDING));
        model.addAttribute("confirmedCount", bookings.countByStatus(BookingStatus.CONFIRMED));
        model.addAttribute("rejectedCount", bookings.countByStatus(BookingStatus.REJECTED));
        model.addAttribute("pending", bookings.findByStatusOrderByCreatedAtDesc(BookingStatus.PENDING));
        model.addAttribute("confirmed", bookings.findByStatusOrderByCreatedAtDesc(BookingStatus.CONFIRMED));
        model.addAttribute("allBookings", bookings.findAllByOrderByDateAscTimeSlotAsc());
        return "admin/dashboard";
    }

    /** Otvara jednu rezervaciju po ID-u. */
    @GetMapping("/bookings/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookings.findById(id).orElseThrow());
        return "admin/booking-detail";
    }

    /**
     * Menja status rezervacije i, ako je email uključen, šalje roditelju obaveštenje.
     * Tekst poruke se menja u EmailService.java.
     */
    @PostMapping("/bookings/{id}/status")
    public String status(@PathVariable Long id,
                         @RequestParam BookingStatus status,
                         @RequestParam(required = false, defaultValue = "") String internalNote,
                         RedirectAttributes ra) {
        Booking before = bookings.findById(id).orElseThrow();
        BookingStatus previousStatus = before.getStatus();
        Booking updated = bookingService.updateStatus(id, status, internalNote);

        // Email šaljemo samo kada se status ZAISTA promenio, da izbegnemo duple poruke.
        if (previousStatus != status && (status == BookingStatus.CONFIRMED || status == BookingStatus.REJECTED || status == BookingStatus.CANCELLED)) {
            if (emailService.isEnabled()) {
                boolean sent = emailService.sendStatusChanged(updated);
                if (sent) {
                    ra.addFlashAttribute("mailMessage", "Email obaveštenje je poslato roditelju.");
                } else {
                    ra.addFlashAttribute("mailError", "Status je sačuvan, ali email nije mogao da bude poslat.");
                }
            }
        }

        // Poruka koju admin vidi nakon promene statusa.
        String message = switch (status) {
            case CONFIRMED -> "Rezervacija je uspešno potvrđena.";
            case REJECTED -> "Rezervacija je označena kao odbijena.";
            case PENDING -> "Rezervacija je vraćena na čekanje.";
            case CANCELLED -> "Rezervacija je otkazana.";
        };
        ra.addFlashAttribute("message", message);
        return "redirect:/admin/bookings/" + id;
    }
}
