# Izmene u ovoj verziji

- Uklonjeni javni paketi „Mini slavlje / BlaBla rođendan / Veliko slavlje“.
- Svaki rođendan sada traje 1,5h.
- Svaki rođendan uključuje igraonicu, grickalice, sokiće za decu, animatorke, muziku i meni po izboru.
- Cene menija: kiflice 800 RSD/dete, pizza 850 RSD/dete, mini burgeri 950 RSD/dete.
- Booking više ne traži paket; okvirna cena se računa: broj dece × cena menija + dodatne usluge.
- Jasno je naznačeno da piće i druga potrošnja odraslih gostiju nisu uključeni u okvirnu cenu.
- Termini su prilagođeni trajanju od 1,5h: 12:00–13:30, 15:00–16:30 i 18:00–19:30.
- Admin deo za „Cene i ponudu“ fokusiran je na menije i dodatne usluge; vlasnik može da menja cene, nazive, opise i aktivnost.
- Responsive prikaz ostaje prilagođen telefonu, tabletu i računaru.


## Admin kalendar
- Dodat mesečni kalendar na dashboard.
- Prethodni/sledeći mesec i povratak na današnji datum.
- Potvrđene rezervacije su zelene, zahtevi na čekanju žuti.
- Klik na događaj otvara detalje rezervacije.
- Mobilni prikaz koristi horizontalni scroll da kalendar ostane čitljiv.

## Format datuma i termini
- Svi datumi koje roditelj i admin vide prikazuju se u formatu `dd/mm/yyyy` (npr. `14/09/2026`).
- Booking forma više ne koristi američki prikaz datuma; unos je jasno označen kao `dd/mm/yyyy` i validira se pre slanja.
- Dodat je admin ekran **Termini**.
- Svaki termin ima samo vreme početka; završetak se automatski računa +90 minuta jer svi rođendani traju 1,5h.
- Vlasnik može da doda termin, promeni vreme početka, deaktivira ga i ponovo aktivira.
- Zaposleni mogu da vide termine, ali ne mogu da ih menjaju.
- Već napravljene rezervacije zadržavaju originalno vreme termina i nakon promene rasporeda.
- Provera dostupnosti detektuje i preklapanje termina, ne samo identičan naziv termina.

## V3 — minimalna naplata 12 dece
- Ako roditelj unese manje od 12 dece, booking forma prikazuje jasno obaveštenje o minimalnoj naplati.
- Okvirna cena menija tada se računa kao za 12 dece.
- Backend koristi isto pravilo, pa je sačuvana procena u rezervaciji usklađena sa prikazom.

## V4 — datum, galerija i email
- Polje za datum i dalje prikazuje `dd/mm/yyyy`, ali sada ima i dugme za otvaranje kalendara.
- Izabrani datum iz kalendara automatski se pretvara u `dd/mm/yyyy` i odmah proverava slobodne termine.
- Admin galerija podržava izbor više fotografija, preview pre slanja, JPG/PNG/WEBP validaciju i maksimalno 8 MB po slici.
- Opis fotografije može da se menja i nakon uploada.
- Bilo koja fotografija može da se postavi kao naslovna; ona se prikazuje prva na sajtu.
- Dodata su opciona email obaveštenja roditelju nakon slanja zahteva i nakon potvrde/odbijanja rezervacije.
- Email je podrazumevano isključen dok se ne podese SMTP environment promenljive, pa pogrešna ili nedostajuća mail konfiguracija ne blokira rezervacije.



## Komentarisana verzija / email primer
- Dodati komentari na srpskom kroz Java, HTML, JavaScript, CSS, properties, POM i SQL.
- Dodata mapa projekta `KAKO-MENJATI-KOD.md`.
- Dodat `EMAIL-PRIMER.md` sa primerima poruka i bezbednim SMTP placeholderima.
- Logika aplikacije nije menjana ovim korakom; cilj je lakše održavanje i kasnije izmene.

## V6 — sitna UI doterivanja
- Uklonjen je link **Admin prijava** sa javnog footera; admin je i dalje dostupan preko `/admin` ili `/login`.
- Polja **Datum** i **Termin** u booking formi sada su poravnata po gornjoj ivici, pa select više nije vizuelno spušten zbog pomoćnog teksta ispod datuma.

