/*
 * ================================================================
 * FAJL: GalleryImage.java
 * SVRHA: Jedna fotografija u galeriji.
 * GDE MENJATI: sortOrder određuje redosled; slika sa najmanjim redosledom je naslovna.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GalleryImage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Nasumično generisano ime fizičkog fajla u uploads folderu.
    @Column(nullable=false) private String fileName;
    // Opis slike za accessibility/SEO.
    private String altText;
    // Manji broj = ranije u galeriji; 0 je naslovna.
    private Integer sortOrder = 0;
}
