/*
 * ================================================================
 * FAJL: ExtraServiceRepository.java
 * SVRHA: Upiti nad dodatnim uslugama.
 * GDE MENJATI: Aktivne usluge su one koje roditelj vidi na booking formi.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.ExtraService;

import java.util.List;

public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {
    // Javna forma prikazuje samo aktivne dodatke, sortirane po nazivu.
    List<ExtraService> findByActiveTrueOrderByNameAsc();
}
