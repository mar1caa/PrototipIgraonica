/*
 * ================================================================
 * FAJL: PlayroomSettingsRepository.java
 * SVRHA: Pristup jednom zapisu sa globalnim podacima igraonice.
 * GDE MENJATI: Aplikacija koristi zapis sa ID=1.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.PlayroomSettings;

// Nema dodatnih custom upita; standardni findById(1L)/save(...) su dovoljni.
public interface PlayroomSettingsRepository extends JpaRepository<PlayroomSettings, Long> {
}
