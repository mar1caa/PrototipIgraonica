/*
 * ================================================================
 * FAJL: SecurityConfig.java
 * SVRHA: Podešava prijavu, uloge OWNER/EMPLOYEE i zaštitu admin ruta.
 * GDE MENJATI: Ovde menjaj ko sme da pristupi kojoj admin stranici i način prijave.
 * ================================================================
 */
package rs.blabla.igraonica.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import rs.blabla.igraonica.repository.AppUserRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserRepository users;

    // BCrypt hashira admin lozinke pre čuvanja u bazi.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security koristi email kao username za prijavu.
    @Bean
    UserDetailsService userDetailsService() {
        return username -> users.findByEmail(username)
                .map(user -> User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .disabled(!user.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronađen"));
    }

    /**
     * PRAVA PRISTUPA:
     * - javni sajt, booking, CSS/JS i slike: svi
     * - menjanje termina: OWNER
     * - podaci igraonice, cenovnik i galerija: OWNER
     * - ostali /admin ekrani: OWNER ili EMPLOYEE
     *
     * Ako dodaješ novu admin rutu, ovde odluči kojoj grupi pripada.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/booking/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/robots.txt",
                                "/sitemap.xml",
                                "/login"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin/slots/**").hasRole("OWNER")
                        .requestMatchers("/admin/settings/**", "/admin/catalog/**", "/admin/gallery/**").hasRole("OWNER")
                        .requestMatchers("/admin/**").hasAnyRole("OWNER", "EMPLOYEE")
                        .anyRequest().permitAll())
                // Koristimo sopstvenu login stranicu umesto Spring default forme.
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll())
                // Posle logout-a vraćamo korisnika na javnu početnu stranu.
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
