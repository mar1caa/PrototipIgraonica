# Email obaveštenja — primer i kasnije uključivanje

Email funkcionalnost je već pripremljena u `EmailService.java`, ali je **podrazumevano isključena**. To znači da možeš normalno da razvijaš i testiraš rezervacije bez pravog email naloga.

## Gde menjaš tekst poruka

Otvori:

`src/main/java/rs/blabla/igraonica/service/EmailService.java`

Tamo su odvojene poruke za: primljen zahtev, potvrđenu rezervaciju, odbijen zahtev i otkazanu rezervaciju. Traži komentare `EMAIL #1` i `EMAIL #2/#3/#4`.

## Primer: kada roditelj pošalje zahtev

**Naslov:** Primili smo vaš zahtev za rođendan – BlaBla Igraonica

**Tekst:**

Zdravo Ana,

Primili smo vaš zahtev za rođendan i javićemo vam se radi konačne potvrde termina.

Datum: 20/09/2026
Termin: 15:00 - 16:30
Dete: Mila
Broj dece: 10
Meni: Pizza meni
Dodatne usluge: Maskota
Okvirna cena: 14.700 RSD

Napomena: minimalna naplata menija je za 12 dece. Piće i druga potrošnja odraslih gostiju nisu uključeni u okvirnu cenu.

Važno: slanje zahteva ne znači da je termin još konačno rezervisan.

Hvala,
BlaBla Igraonica

## Primer: kada admin potvrdi rezervaciju

**Naslov:** Vaš rođendan je potvrđen – BlaBla Igraonica

**Tekst:**

Zdravo Ana,

Vaša rezervacija rođendana je potvrđena. 🎉

Datum: 20/09/2026
Termin: 15:00 - 16:30
Dete: Mila
Broj dece: 10
Meni: Pizza meni
Dodatne usluge: Maskota
Okvirna cena: 14.700 RSD

Radujemo se proslavi!

BlaBla Igraonica

## Kako se kasnije uključuje Gmail SMTP

Ne upisuj običnu Gmail lozinku u projekat. Za Gmail se koristi **App Password** na nalogu na kom je uključen 2-Step Verification. Kada budeš spremna, podesi environment varijable u IntelliJ Run Configuration:

```text
MAIL_ENABLED=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=primer.igraonica@gmail.com
MAIL_PASSWORD=OVDE_IDE_APP_PASSWORD
MAIL_FROM=primer.igraonica@gmail.com
```

`application.properties` već čita ove vrednosti. Dok `MAIL_ENABLED=false`, ništa se ne šalje.

## Ako koristiš drugi email provajder

Promeni `MAIL_HOST`, `MAIL_PORT` i po potrebi TLS/auth opcije prema dokumentaciji tog provajdera. Ostatak Java koda može da ostane isti.
