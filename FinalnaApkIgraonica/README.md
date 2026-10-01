# BlaBla Igraonica — funkcionalni MVP

Spring Boot + Thymeleaf + MySQL aplikacija za prezentaciju igraonice, online zahtev za rođendan i admin upravljanje rezervacijama i ponudom.

## Šta radi

- javna početna strana sa informacijama o rođendanima, menijima, dodatnim uslugama, galerijom i kontaktom
- responzivna forma za rezervaciju na telefonu, tabletu i računaru
- provera zauzetih termina pre slanja zahteva
- automatski obračun okvirne cene: broj dece × cena menija + dodatne usluge
- admin login za vlasnika i radnika
- PENDING / CONFIRMED / REJECTED statusi rezervacije
- interna napomena uz rezervaciju
- posebno vidljive potvrđene rezervacije na dashboard-u
- vlasnik može da menja informacije o igraonici, menije, cene i dodatne usluge
- stavke se mogu deaktivirati i ponovo aktivirati; deaktivirana stavka ostaje u admin panelu, ali je roditelji ne vide
- upload galerije sa više fotografija, preview-em i izborom naslovne slike
- opciona automatska email obaveštenja roditeljima


## Pravila demo rođendana

- sve proslave traju 1,5h
- uključeni su igraonica, grickalice, sokići za decu, animatorke i muzika
- cena zavisi od broja dece i izabranog menija
- okvirna cena ne uključuje piće i drugu potrošnju odraslih gostiju u lokalu

## Demo cene menija

- Kiflice meni: 800 RSD po detetu
- Pizza meni: 850 RSD po detetu
- Mini burger meni: 950 RSD po detetu

Ako baza već sadrži stare demo cene 350 / 450 / 550, aplikacija ih pri prvom sledećem startu automatski menja na nove vrednosti. Kasnije ručne izmene vlasnika se ne prepisuju.

## Pokretanje

1. Instaliraj JDK 17 ili noviji kompatibilan JDK i MySQL 8.
2. U MySQL Workbench-u pokreni `database.sql`. On pravi samo praznu bazu.
3. U `src/main/resources/application.properties` zameni `CHANGE_ME` svojom lokalnom MySQL lozinkom ili postavi environment promenljivu `DB_PASSWORD`.
4. Otvori baš folder `blabla-functional` u IntelliJ IDEA.
5. Sačekaj Maven import / Reload Maven Project.
6. Pokreni `IgraonicaApplication.java`.
7. Otvori `http://localhost:8080`.

## Demo admin nalozi

Vlasnik:
- `owner@blabla.rs`
- `PromeniMe123!`

Radnik:
- `radnik@blabla.rs`
- `Radnik123!`

Pre produkcije obavezno promeni početne lozinke putem environment promenljivih.

## Email obaveštenja

Email je podrazumevano **isključen**, tako da aplikacija normalno radi i bez SMTP podešavanja. Kada želite pravo slanje poruka, preporuka je da podatke postavite kao environment promenljive, a ne da lozinku upisujete u kod:

- `MAIL_ENABLED=true`
- `MAIL_USERNAME=vas-email@gmail.com`
- `MAIL_PASSWORD=app-password`
- `MAIL_FROM=vas-email@gmail.com`

Za Gmail koristite App Password kada je nalog podešen za to. Aplikacija šalje roditelju potvrdu prijema zahteva i, kada admin promeni status, obaveštenje o potvrdi ili odbijanju. Ako slanje emaila ne uspe, rezervacija i promena statusa se ipak normalno čuvaju.

## Unos datuma

Roditelj i dalje vidi datum u formatu `dd/mm/yyyy`, ali sada može i da klikne na ikonicu kalendara i izabere datum bez ručnog kucanja. Backend i baza i dalje koriste standardan datum interno.

## Važna napomena za bazu

Struktura tabela se dobija iz Java JPA modela (`@Entity`) preko Hibernate-a. Nemoj ubacivati stare CREATE TABLE skripte iz ranijih verzija projekta.


### Minimalna naplata
Osnovni meni se obračunava za najmanje 12 dece. Ako je prijavljeno manje od 12 dece, okvirna i sačuvana procena koriste cenu za 12 dece, dok se stvarni broj dece i dalje čuva u rezervaciji.


## Gde je šta u kodu

Kod je namerno detaljno komentarisan na srpskom. Za brzi pregled otvori `KAKO-MENJATI-KOD.md`. Za email primer i kasnije SMTP uključivanje otvori `EMAIL-PRIMER.md`.
