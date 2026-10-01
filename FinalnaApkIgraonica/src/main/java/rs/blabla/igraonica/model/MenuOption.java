/*
 * ================================================================
 * FAJL: MenuOption.java
 * SVRHA: Meni hrane po detetu.
 * GDE MENJATI: pricePerChild je cena po detetu koja ulazi u okvirnu cenu rezervacije.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuOption {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Naziv menija, npr. Pizza meni.
    @Column(nullable=false) private String name;
    // Cena jednog menija po detetu.
    @Column(nullable=false, precision=10, scale=2) private BigDecimal pricePerChild;
    // Opis koji vidi roditelj.
    @Column(length=1000) private String description;
    // false = meni se ne nudi roditeljima, ali se ne briše iz admina.
    @Column(nullable=false) private boolean active = true;
}
