/*
 * ================================================================
 * FAJL: AppUser.java
 * SVRHA: Entitet korisničkog naloga za admin panel.
 * GDE MENJATI: Polja predstavljaju vlasnika/radnika, email za login, šifru i ulogu.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="users") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    // Primarni ključ korisnika.
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Ime koje možemo prikazati u adminu.
    @Column(nullable=false) private String name;
    // Jedinstveni email; koristi se i kao username.
    @Column(nullable=false, unique=true) private String email;
    // BCrypt hash lozinke, nikad plain-text lozinka.
    @Column(nullable=false) private String password;
    // OWNER ili EMPLOYEE.
    @Enumerated(EnumType.STRING) @Column(nullable=false) private UserRole role;
    // false = nalog postoji ali mu je login onemogućen.
    @Column(nullable=false) private boolean enabled = true;
}
