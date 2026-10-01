/*
 * ================================================================
 * FAJL: PartyPackageRepository.java
 * SVRHA: Upiti nad starijim modelom paketa proslave.
 * GDE MENJATI: Trenutni tok aplikacije se oslanja na MenuOption, ne na pakete.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.PartyPackage;

import java.util.List;

public interface PartyPackageRepository extends JpaRepository<PartyPackage, Long> {
    // Ostavljen zbog kompatibilnosti sa starijom verzijom projekta.
    List<PartyPackage> findByActiveTrueOrderBySortOrderAsc();
}
