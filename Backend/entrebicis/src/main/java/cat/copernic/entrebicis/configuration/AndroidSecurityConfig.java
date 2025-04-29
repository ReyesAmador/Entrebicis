/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuració de seguretat específica per a la API REST (/api/**) de l'aplicació Entrebicis.
 * 
 * <p>Aquesta configuració gestiona l'autenticació mitjançant JWT, estableix una política de sessió
 * sense estat (stateless) i configura l'ús d'un {@link DaoAuthenticationProvider} amb contrasenyes encriptades.</p>
 * 
 * <p>Es prioritza amb {@link Order}(1) per assegurar que aquesta configuració
 * s'apliqui abans que altres cadenes de seguretat si n'hi ha més definides.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Configuration}.</p>
 * 
 * @author reyes
 */
@Configuration
@Order(1)
@RequiredArgsConstructor
public class AndroidSecurityConfig {
    
    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    
    /**
     * Defineix la cadena de filtres de seguretat per protegir totes les rutes que comencen amb /api/.
     * 
     * <p>Permet accés públic als endpoints de login i recuperació de contrasenya,
     * mentre que la resta de peticions requereixen autenticació amb token JWT.</p>
     *
     * @param http l'objecte {@link HttpSecurity} que configura la seguretat HTTP.
     * @return la cadena de filtres {@link SecurityFilterChain}.
     * @throws Exception si hi ha errors en la configuració de seguretat.
     */
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login", "/api/forgot-pass", "/api/validate-code", "/api/reset-pass").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * Crea i configura un {@link AuthenticationProvider} que utilitza
     * un {@link UserDetailsService} i un {@link PasswordEncoder} per autenticar usuaris.
     *
     * @return el {@link AuthenticationProvider} configurat.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    /**
     * Proporciona un {@link AuthenticationManager} basat en la configuració d'autenticació actual.
     *
     * @param config l'objecte {@link AuthenticationConfiguration}.
     * @return l'objecte {@link AuthenticationManager} configurat.
     * @throws Exception si hi ha problemes obtenint l'AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Defineix un {@link PasswordEncoder} que utilitza l'algoritme BCrypt per encriptar contrasenyes.
     *
     * @return una instància de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
