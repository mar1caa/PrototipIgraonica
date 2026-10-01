/*
 * ================================================================
 * FAJL: MenuOptionRepository.java
 * SVRHA: Upiti nad menijima.
 * GDE MENJATI: Aktivni meniji su oni koje roditelj može da izabere.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.MenuOption;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    // Javna forma prikazuje samo aktivne menije, sortirane po nazivu.
    List<MenuOption> findByActiveTrueOrderByNameAsc();
}
