/*
 * ================================================================
 * FAJL: BookingRepository.java
 * SVRHA: Upiti nad rezervacijama.
 * GDE MENJATI: Dodaj novu metodu ovde kada ti treba novo filtriranje/sortiranje rezervacija.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.Booking;
import rs.blabla.igraonica.model.BookingStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA implementaciju pravi automatski na osnovu naziva metoda.
 * Zato ovde nema ručnog SQL-a za osnovne upite.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Hronološki pregled svih rezervacija za admin istoriju i kalendar.
    List<Booking> findAllByOrderByDateAscTimeSlotAsc();

    // Lista rezervacija jednog statusa, najnoviji zahtev prvi.
    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

    // Sve rezervacije određenog datuma osim jednog statusa.
    List<Booking> findByDateAndStatusNot(LocalDate date, BookingStatus status);

    // Rezervacije određenog datuma samo za prosleđene statuse (npr. PENDING + CONFIRMED).
    List<Booking> findByDateAndStatusIn(LocalDate date, Collection<BookingStatus> statuses);

    // Broj rezervacija po statusu za statistiku na dashboard-u.
    long countByStatus(BookingStatus status);

    // Brza provera da li isti datum+termin već postoji među statusima koji blokiraju termin.
    boolean existsByDateAndTimeSlotAndStatusIn(
            LocalDate date,
            String timeSlot,
            Collection<BookingStatus> statuses
    );
}
