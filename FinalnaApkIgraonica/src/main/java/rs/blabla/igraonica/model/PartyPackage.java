/*
 * ================================================================
 * FAJL: PartyPackage.java
 * SVRHA: Stariji/model paketa proslave ostavljen zbog kompatibilnosti postojećih podataka.
 * GDE MENJATI: Trenutni javni tok koristi meni po detetu; ovaj model ne moraš menjati za redovne izmene.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="party_packages") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartyPackage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Naziv starog/demo paketa.
    @Column(nullable=false) private String name;
    private String badge;
    // Stara osnovna cena paketa; novi tok koristi cenu menija po detetu.
    @Column(nullable=false, precision=10, scale=2) private BigDecimal basePrice;
    private Integer maxChildren;
    private Integer durationMinutes;
    @Column(length=1500) private String description;
    // Status starog paketa.
    @Column(nullable=false) private boolean active = true;
    private Integer sortOrder = 0;
}
