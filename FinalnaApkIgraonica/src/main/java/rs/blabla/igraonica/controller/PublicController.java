/*
 * ================================================================
 * FAJL: PublicController.java
 * SVRHA: Javne stranice: početna, login, robots.txt i sitemap.
 * GDE MENJATI: Ovde dodaj podatke koji treba da se prikažu na javnim stranicama.
 * ================================================================
 */
package rs.blabla.igraonica.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rs.blabla.igraonica.repository.*;

@Controller
@RequiredArgsConstructor
public class PublicController {
    private final PlayroomSettingsRepository settings;
    private final MenuOptionRepository menus;
    private final ExtraServiceRepository extras;
    private final GalleryImageRepository gallery;

    /** Početna stranica: sadržaj dolazi iz baze da OWNER može da ga menja iz admina. */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("settings", settings.findById(1L).orElse(null));
        model.addAttribute("menus", menus.findByActiveTrueOrderByNameAsc());
        model.addAttribute("extras", extras.findByActiveTrueOrderByNameAsc());
        model.addAttribute("gallery", gallery.findAllByOrderBySortOrderAsc());
        return "index";
    }

    /** Prikazuje custom login stranicu za zaposlene. */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Osnovni SEO endpoint. Za pravi domen kasnije ovde može da ide puna sitemap adresa.
    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ResponseBody
    public String robots() {
        return "User-agent: *\nAllow: /\nSitemap: /sitemap.xml\n";
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url><loc>/</loc></url><url><loc>/booking</loc></url></urlset>";
    }
}
