/*
 * ================================================================
 * FAJL: IgraonicaApplication.java
 * SVRHA: Ulazna tačka Spring Boot aplikacije.
 * GDE MENJATI: Ovde se uglavnom ništa ne menja; pokreće celu aplikaciju.
 * ================================================================
 */
package rs.blabla.igraonica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IgraonicaApplication {

    // IntelliJ Run dugme na ovoj klasi pokreće web server, Spring konteks i aplikaciju.
    public static void main(String[] args) {
        SpringApplication.run(IgraonicaApplication.class, args);
    }
}
