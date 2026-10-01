/*
 * ================================================================
 * FAJL: BookingStatus.java
 * SVRHA: Dozvoljeni statusi jedne rezervacije.
 * GDE MENJATI: Ako dodaš novi status, proveri i AdminController, email tekstove i prikaz statusa u HTML-u.
 * ================================================================
 */
package rs.blabla.igraonica.model;
// PENDING=nov zahtev, CONFIRMED=potvrđen, REJECTED=odbijen, CANCELLED=otkazan.
public enum BookingStatus { PENDING, CONFIRMED, REJECTED, CANCELLED }
