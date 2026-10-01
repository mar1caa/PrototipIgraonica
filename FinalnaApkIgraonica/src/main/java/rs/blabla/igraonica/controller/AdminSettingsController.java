/*
 * ================================================================
 * FAJL: AdminSettingsController.java
 * SVRHA: Admin podešavanja: podaci igraonice, meniji, dodatne usluge, termini i galerija.
 * GDE MENJATI: Ovo je glavno mesto za OWNER funkcije i administraciju sadržaja.
 * ================================================================
 */
package rs.blabla.igraonica.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rs.blabla.igraonica.model.*;
import rs.blabla.igraonica.repository.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSettingsController {
    private final PlayroomSettingsRepository settings;
    private final MenuOptionRepository menus;
    private final ExtraServiceRepository extras;
    private final GalleryImageRepository gallery;
    private final TimeSlotRepository timeSlots;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    // ===== PODACI IGRAONICE =====
    // Globalni naziv, kontakt, opis i SEO tekstovi.
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", settings.findById(1L).orElseThrow());
        model.addAttribute("mailEnabled", mailEnabled);
        model.addAttribute("mailSender", (mailFrom == null || mailFrom.isBlank()) ? mailUsername : mailFrom);
        return "admin/settings";
    }

    @PostMapping("/settings")
    public String saveSettings(@ModelAttribute PlayroomSettings form, RedirectAttributes ra) {
        form.setId(1L);
        settings.save(form);
        ra.addFlashAttribute("message", "Podaci igraonice su sačuvani.");
        return "redirect:/admin/settings";
    }

    // ===== CENOVNIK: MENIJI I DODATNE USLUGE =====
    @GetMapping("/catalog")
    public String catalog(Model model) {
        model.addAttribute("menus", menus.findAll().stream()
                .sorted(Comparator.comparing(MenuOption::getName, String.CASE_INSENSITIVE_ORDER))
                .toList());
        model.addAttribute("extras", extras.findAll().stream()
                .sorted(Comparator.comparing(ExtraService::getName, String.CASE_INSENSITIVE_ORDER))
                .toList());
        return "admin/catalog";
    }

    // Kreira novi meni ili čuva izmene postojećeg. Active stanje se pri editovanju ne menja.
    @PostMapping("/catalog/menu")
    public String saveMenu(@ModelAttribute MenuOption form, RedirectAttributes ra) {
        if (form.getId() == null) {
            form.setActive(true);
            menus.save(form);
            ra.addFlashAttribute("message", "Novi meni je dodat.");
        } else {
            MenuOption item = menus.findById(form.getId()).orElseThrow();
            item.setName(form.getName());
            item.setPricePerChild(form.getPricePerChild());
            item.setDescription(form.getDescription());
            menus.save(item);
            ra.addFlashAttribute("message", "Meni je sačuvan.");
        }
        return "redirect:/admin/catalog#meniji-admin";
    }

    // Kreira novu dodatnu uslugu ili čuva izmene postojeće.
    @PostMapping("/catalog/extra")
    public String saveExtra(@ModelAttribute ExtraService form, RedirectAttributes ra) {
        if (form.getId() == null) {
            form.setActive(true);
            extras.save(form);
            ra.addFlashAttribute("message", "Nova dodatna usluga je dodata.");
        } else {
            ExtraService item = extras.findById(form.getId()).orElseThrow();
            item.setEmoji(form.getEmoji());
            item.setName(form.getName());
            item.setPrice(form.getPrice());
            item.setDescription(form.getDescription());
            extras.save(item);
            ra.addFlashAttribute("message", "Dodatna usluga je sačuvana.");
        }
        return "redirect:/admin/catalog#dodaci-admin";
    }

    // Soft hide/show: zapis ostaje u adminu, ali neaktivan više nije ponuđen roditeljima.
    @PostMapping("/catalog/menu/{id}/toggle")
    public String toggleMenu(@PathVariable Long id, RedirectAttributes ra) {
        MenuOption item = menus.findById(id).orElseThrow();
        item.setActive(!item.isActive());
        menus.save(item);
        ra.addFlashAttribute("message", item.isActive() ? "Meni je ponovo aktiviran." : "Meni je deaktiviran i više se ne prikazuje roditeljima.");
        return "redirect:/admin/catalog#meniji-admin";
    }

    @PostMapping("/catalog/extra/{id}/toggle")
    public String toggleExtra(@PathVariable Long id, RedirectAttributes ra) {
        ExtraService item = extras.findById(id).orElseThrow();
        item.setActive(!item.isActive());
        extras.save(item);
        ra.addFlashAttribute("message", item.isActive() ? "Usluga je ponovo aktivirana." : "Usluga je deaktivirana i više se ne prikazuje roditeljima.");
        return "redirect:/admin/catalog#dodaci-admin";
    }

    // ===== TERMINI =====
    // OWNER može da menja; EMPLOYEE može da ih vidi.
    @GetMapping("/slots")
    public String slots(Model model, Authentication authentication) {
        model.addAttribute("slots", timeSlots.findAllByOrderByStartTimeAsc());
        boolean canEdit = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_OWNER".equals(a.getAuthority()));
        model.addAttribute("canEdit", canEdit);
        return "admin/slots";
    }

    // Dodavanje novog vremena početka. Kraj se automatski računa +90 min u TimeSlot modelu.
    @PostMapping("/slots")
    public String addSlot(
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            RedirectAttributes ra) {
        if (timeSlots.existsByStartTime(startTime)) {
            ra.addFlashAttribute("error", "Termin sa tim vremenom početka već postoji.");
            return "redirect:/admin/slots";
        }
        timeSlots.save(TimeSlot.builder().startTime(startTime).active(true).build());
        ra.addFlashAttribute("message", "Novi termin je dodat.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/slots/{id}")
    public String updateSlot(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            RedirectAttributes ra) {
        if (timeSlots.existsByStartTimeAndIdNot(startTime, id)) {
            ra.addFlashAttribute("error", "Drugi termin sa tim vremenom početka već postoji.");
            return "redirect:/admin/slots";
        }
        TimeSlot slot = timeSlots.findById(id).orElseThrow();
        slot.setStartTime(startTime);
        timeSlots.save(slot);
        ra.addFlashAttribute("message", "Termin je sačuvan. Trajanje rođendana ostaje 1,5h.");
        return "redirect:/admin/slots";
    }

    @PostMapping("/slots/{id}/toggle")
    public String toggleSlot(@PathVariable Long id, RedirectAttributes ra) {
        TimeSlot slot = timeSlots.findById(id).orElseThrow();
        slot.setActive(!slot.isActive());
        timeSlots.save(slot);
        ra.addFlashAttribute("message", slot.isActive()
                ? "Termin je ponovo dostupan roditeljima."
                : "Termin je deaktiviran i više se ne nudi za nove rezervacije.");
        return "redirect:/admin/slots";
    }

    // ===== GALERIJA =====
    // Redosled slika određuje prikaz; prva slika se koristi kao naslovna.
    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("images", gallery.findAllByOrderBySortOrderAsc());
        return "admin/gallery";
    }

    // Upload više slika; dozvoljeni su JPG/PNG/WEBP i max 8 MB po slici.
    @PostMapping("/gallery")
    public String upload(@RequestParam("images") MultipartFile[] images,
                         @RequestParam(required = false, defaultValue = "") String altText,
                         RedirectAttributes ra) throws IOException {
        List<MultipartFile> selected = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) selected.add(image);
            }
        }
        if (selected.isEmpty()) {
            ra.addFlashAttribute("error", "Izaberite bar jednu fotografiju.");
            return "redirect:/admin/gallery";
        }

        for (MultipartFile image : selected) {
            if (extensionFor(image.getContentType()) == null) {
                ra.addFlashAttribute("error", "Dozvoljene su JPG, PNG i WEBP fotografije.");
                return "redirect:/admin/gallery";
            }
            if (image.getSize() > 8L * 1024L * 1024L) {
                ra.addFlashAttribute("error", "Jedna fotografija može imati najviše 8 MB.");
                return "redirect:/admin/gallery";
            }
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        int nextOrder = gallery.findAllByOrderBySortOrderAsc().stream()
                .map(GalleryImage::getSortOrder)
                .filter(java.util.Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(-1) + 1;
        String defaultAlt = settings.findById(1L)
                .map(PlayroomSettings::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name + " - galerija")
                .orElse("BlaBla Igraonica - galerija");

        int uploaded = 0;
        for (MultipartFile image : selected) {
            String ext = extensionFor(image.getContentType());
            String filename = UUID.randomUUID() + ext;
            Files.copy(image.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            String imageAlt = altText == null || altText.isBlank() ? defaultAlt : altText.trim();
            if (selected.size() > 1) imageAlt += " " + (uploaded + 1);

            gallery.save(GalleryImage.builder()
                    .fileName(filename)
                    .altText(imageAlt)
                    .sortOrder(nextOrder + uploaded)
                    .build());
            uploaded++;
        }

        ra.addFlashAttribute("message", uploaded == 1
                ? "Fotografija je dodata u galeriju."
                : "Dodato je " + uploaded + " fotografija u galeriju.");
        return "redirect:/admin/gallery";
    }

    // ALT tekst je koristan za pristupačnost i SEO.
    @PostMapping("/gallery/{id}/alt")
    public String updateImageAlt(@PathVariable Long id,
                                 @RequestParam(required = false, defaultValue = "") String altText,
                                 RedirectAttributes ra) {
        GalleryImage image = gallery.findById(id).orElseThrow();
        image.setAltText(altText == null ? "" : altText.trim());
        gallery.save(image);
        ra.addFlashAttribute("message", "Opis fotografije je sačuvan.");
        return "redirect:/admin/gallery";
    }

    // Izabranu sliku pomera na sortOrder=0, pa postaje naslovna.
    @PostMapping("/gallery/{id}/cover")
    public String makeCover(@PathVariable Long id, RedirectAttributes ra) {
        GalleryImage selected = gallery.findById(id).orElseThrow();
        List<GalleryImage> ordered = new ArrayList<>(gallery.findAllByOrderBySortOrderAsc());
        ordered.removeIf(img -> img.getId().equals(id));
        selected.setSortOrder(0);
        gallery.save(selected);
        int order = 1;
        for (GalleryImage image : ordered) {
            image.setSortOrder(order++);
        }
        gallery.saveAll(ordered);
        ra.addFlashAttribute("message", "Fotografija je postavljena kao naslovna.");
        return "redirect:/admin/gallery";
    }

    // Briše i fizički fajl iz uploads foldera i zapis iz baze.
    @PostMapping("/gallery/{id}/delete")
    public String deleteImage(@PathVariable Long id, RedirectAttributes ra) throws IOException {
        GalleryImage img = gallery.findById(id).orElseThrow();
        Files.deleteIfExists(Paths.get(uploadDir).toAbsolutePath().normalize().resolve(img.getFileName()));
        gallery.delete(img);

        int order = 0;
        List<GalleryImage> remaining = gallery.findAllByOrderBySortOrderAsc();
        for (GalleryImage image : remaining) image.setSortOrder(order++);
        gallery.saveAll(remaining);

        ra.addFlashAttribute("message", "Fotografija je obrisana.");
        return "redirect:/admin/gallery";
    }

    // Bezbednosna kontrola: prihvatamo samo očekivane tipove slika.
    private String extensionFor(String contentType) {
        if (contentType == null) return null;
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> null;
        };
    }
}
