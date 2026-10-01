/*
 * ================================================================
 * FAJL: Booking.java
 * SVRHA: Glavni entitet rezervacije rođendana.
 * GDE MENJATI: Ako dodaješ novi podatak u rezervaciju, obično prvo dodaješ polje ovde.
 * ================================================================
 */
package rs.blabla.igraonica.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    // Jedinstveni ID rezervacije.
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    // Kontakt podaci roditelja.
    @Column(nullable=false) private String parentName;
    @Column(nullable=false) private String phone;
    @Column(nullable=false) private String email;
    // Podaci o slavljeniku.
    @Column(nullable=false) private String childName;
    private Integer childAge;
    // Stvarno prijavljen broj dece; minimum naplate se NE upisuje ovde.
    private Integer numberOfChildren;
    // U bazi LocalDate; korisniku se prikazuje kao dd/MM/yyyy.
    @Column(nullable=false) private LocalDate date;
    // Tekst termina se čuva uz rezervaciju da stara rezervacija ostane istorijski tačna.
    @Column(nullable=false) private String timeSlot;
    @ManyToOne(fetch=FetchType.EAGER) private PartyPackage partyPackage;
    // Izabrani meni po detetu.
    @ManyToOne(fetch=FetchType.EAGER) private MenuOption menuOption;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="booking_extras", joinColumns=@JoinColumn(name="booking_id"), inverseJoinColumns=@JoinColumn(name="extra_id"))
    // Opciono izabrane dodatne usluge.
    @Builder.Default private List<ExtraService> extras = new ArrayList<>();
    // Napomena koju unosi roditelj.
    @Column(length=2000) private String parentNote;
    // Interna napomena zaposlenih; ne prikazuje se roditelju.
    @Column(length=3000) private String internalNote;
    // PENDING / CONFIRMED / REJECTED / CANCELLED.
    @Enumerated(EnumType.STRING) @Column(nullable=false) private BookingStatus status;
    // Okvirna cena u trenutku slanja zahteva.
    @Column(precision=12, scale=2) private BigDecimal estimatedPrice;
    // Vreme kreiranja zahteva.
    @Column(nullable=false) private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    public String getDisplayDate() {
        return date == null ? "" : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

