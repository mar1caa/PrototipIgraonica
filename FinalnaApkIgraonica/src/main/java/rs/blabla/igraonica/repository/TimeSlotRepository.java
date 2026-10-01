/*
 * ================================================================
 * FAJL: TimeSlotRepository.java
 * SVRHA: Upiti nad terminima.
 * GDE MENJATI: Metode exists... sprečavaju dupliranje vremena početka.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.TimeSlot;

import java.time.LocalTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    // Samo aktivni termini koje roditelj trenutno može da izabere.
    List<TimeSlot> findByActiveTrueOrderByStartTimeAsc();

    // Svi termini za admin ekran, uključujući neaktivne.
    List<TimeSlot> findAllByOrderByStartTimeAsc();

    // Provera duplikata pri dodavanju novog termina.
    boolean existsByStartTime(LocalTime startTime);

    // Provera duplikata pri izmeni, ali ignoriše termin koji trenutno editujemo.
    boolean existsByStartTimeAndIdNot(LocalTime startTime, Long id);
}
