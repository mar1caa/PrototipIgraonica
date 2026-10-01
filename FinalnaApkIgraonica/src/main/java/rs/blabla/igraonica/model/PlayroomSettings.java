/*
 * ================================================================
 * FAJL: PlayroomSettings.java
 * SVRHA: Podaci igraonice koji se uređuju iz admina i prikazuju javno.
 * GDE MENJATI: Ovde dodaj polje ako želiš novi globalni podatak kao adresu, društvenu mrežu ili SEO tekst.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayroomSettings {
    @Id private Long id;
    // Naziv igraonice.
    @Column(nullable=false) private String name;
    // Grad za javni prikaz/SEO.
    private String city;
    // Adresa lokala.
    private String address;
    // Kontakt telefon.
    private String phone;
    // Javni kontakt email igraonice.
    private String email;
    // Instagram profil/handle.
    private String instagram;
    // Opciono Facebook polje.
    private String facebook;
    // Kratak opis za javni sajt.
    @Column(length=2000) private String shortDescription;
    // Duži opis prostora/usluge.
    @Column(length=3000) private String aboutText;
    // Glavni naslov na vrhu početne strane.
    private String heroTitle;
    // Tekst ispod hero naslova.
    private String heroSubtitle;
    // HTML/SEO title.
    private String seoTitle;
    // Meta description.
    @Column(length=500) private String seoDescription;
}
