/*
 * ================================================================
 * FAJL: AppUserRepository.java
 * SVRHA: Pristup users tabeli.
 * GDE MENJATI: Spring Data automatski generiše upit findByEmail iz naziva metode.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Login koristi email, pa ovim tražimo korisnika po jedinstvenoj email adresi.
    Optional<AppUser> findByEmail(String email);
}
