/*
 * ================================================================
 * FAJL: UserRole.java
 * SVRHA: Uloge korisnika admin panela.
 * GDE MENJATI: OWNER ima više prava od EMPLOYEE; promene uloga uskladi sa SecurityConfig.
 * ================================================================
 */
package rs.blabla.igraonica.model;
// OWNER=vlasnik sa punim podešavanjima, EMPLOYEE=zaposleni za operativni rad.
public enum UserRole { OWNER, EMPLOYEE }
