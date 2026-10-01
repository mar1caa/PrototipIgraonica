# Mapa projekta — gde se šta menja

Ovaj fajl je brzi vodič kada se posle nekog vremena vratiš projektu i ne sećaš se gde je šta. Uz to, svi važni source fajlovi sada imaju komentare na srpskom.

## Ako menjaš poslovna pravila

- **Minimum naplate 12 dece:** `BookingService.java` i `booking.js`.
- **Trajanje rođendana 1,5h:** `TimeSlot.java` (`BIRTHDAY_DURATION_MINUTES = 90`).
- **Početni termini:** `DataInitializer.java`. Posle prvog pokretanja termine menjaš iz admina.
- **Početne cene menija i dodatnih usluga:** `DataInitializer.java`. Posle toga ih menja OWNER iz admina.

## Ako menjaš booking formu

- HTML/polja i tekst: `templates/booking.html`.
- Format datuma, live okvirna cena, provera dostupnosti: `static/js/booking.js`.
- Server-side validacija i POST: `BookingController.java`.
- Čuvanje i računanje: `BookingService.java`.
- Polja koja postoje u bazi: `model/Booking.java`.

## Ako menjaš admin

- Dashboard i status rezervacije: `AdminController.java` + `templates/admin/dashboard.html`.
- Meniji, dodatne usluge, termini, galerija, podaci igraonice: `AdminSettingsController.java`.
- Ko sme šta da otvori: `SecurityConfig.java`.

## Ako menjaš slike

- Admin ekran: `templates/admin/gallery.html`.
- Preview/drag&drop: `static/js/admin-gallery.js`.
- Upload, validacija i brisanje fajlova: `AdminSettingsController.java`.
- Folder za slike: `app.upload-dir` u `application.properties`.

## Ako menjaš email

- Tekst i subject: `EmailService.java`.
- SMTP podešavanja: `application.properties`.
- Primeri i koraci: `EMAIL-PRIMER.md`.

## Ako menjaš izgled

- Skoro sve boje, razmaci, kartice i responsive ponašanje: `static/css/app.css`.
- Najpre pogledaj `:root` na vrhu CSS-a; tu su glavne boje.

## Ako dodaješ novo polje

Primer: želiš polje „tema rođendana“. Najčešći redosled je:

1. dodaj `theme` u `Booking.java`,
2. dodaj input u `booking.html`,
3. primi ga u `BookingController`,
4. prosledi ga u `BookingService.create`,
5. prikaži ga u `admin/booking-detail.html`,
6. po potrebi dodaj ga i u `EmailService.bookingDetails`.

Hibernate će uz `ddl-auto=update` pokušati da doda novu kolonu pri sledećem pokretanju. Pre produkcije se inače koriste kontrolisane migracije, ali za ovaj studentski/MVP projekat trenutni pristup je jednostavniji.

## Pravilo za lozinke

Ne stavljaj prave MySQL, Gmail ili druge lozinke u Git/ZIP koji deliš. Koristi environment varijable (`DB_PASSWORD`, `MAIL_PASSWORD`, itd.).
