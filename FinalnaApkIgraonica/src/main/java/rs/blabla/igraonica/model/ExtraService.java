/*
 * ================================================================
 * FAJL: ExtraService.java
 * SVRHA: Dodatna usluga uz rođendan, npr. maskota ili fotograf.
 * GDE MENJATI: Cena je fiksna po usluzi; active određuje da li je roditelji vide.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExtraService {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Naziv usluge.
    @Column(nullable=false) private String name;
    // Fiksna doplata za uslugu.
    @Column(nullable=false, precision=10, scale=2) private BigDecimal price;
    // Mala ikonica prikazana roditelju.
    private String emoji;
    // Kratak opis.
    @Column(length=1000) private String description;
    // false = sakriveno roditeljima, ali ostaje vidljivo u adminu.
    @Column(nullable=false) private boolean active = true;
}
