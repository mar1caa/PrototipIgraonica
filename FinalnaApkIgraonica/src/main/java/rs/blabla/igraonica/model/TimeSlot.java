/*
 * ================================================================
 * FAJL: TimeSlot.java
 * SVRHA: Model jednog termina za rođendan.
 * GDE MENJATI: Konstanta BIRTHDAY_DURATION_MINUTES kontroliše trajanje svih rođendana.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "time_slots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimeSlot {
    // GLAVNO MESTO za trajanje rođendana u minutima.
    private static final int BIRTHDAY_DURATION_MINUTES = 90;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    // Vreme početka termina.
    private LocalTime startTime;

    @Column(nullable = false)
    @Builder.Default
    // Neaktivan termin ostaje u adminu, ali se ne nudi za nove zahteve.
    private boolean active = true;

    @Transient
    public LocalTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(BIRTHDAY_DURATION_MINUTES);
    }

    @Transient
    public String getLabel() {
        if (startTime == null) return "";
        return startTime.format(TIME_FORMAT) + " - " + getEndTime().format(TIME_FORMAT);
    }

    @Transient
    public String getStartTimeDisplay() {
        return startTime == null ? "" : startTime.format(TIME_FORMAT);
    }
}
