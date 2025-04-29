/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuració de seguretat web per a la part d'administració de l'aplicació Entrebicis.
 * 
 * <p>Gestiona la seguretat de les rutes web (`/**`) aplicant autenticació basada en formularis,
 * permisos d'accés segons rols i gestió d'excepcions per accessos denegats.</p>
 * 
 * <p>Aquesta configuració té una prioritat {@link Order}(2), de manera que s'aplica
 * després de la configuració de la API Android.</p>
 * 
 * <p>Detectada automàticament per Spring Boot gràcies a {@link Configuration} i {@link EnableWebSecurity}.</p>
 * 
 * @author reyes
 */

@Configuration
@EnableWebSecurity
@Order(2)
public class WebSecurityConfig {
   
    /**
     * Defineix la cadena de filtres de seguretat per a totes les rutes web (`/**`).
     * 
     * <p>Permet accés públic a les rutes de login, imatges, fulls d'estil i scripts.
     * Restringeix l'accés a les rutes `/admin/**` únicament a usuaris amb rol ADMIN.
     * Configura també el comportament en login, error d'autenticació i accessos denegats.</p>
     *
     * @param http l'objecte {@link HttpSecurity} utilitzat per configurar la seguretat web.
     * @return la cadena de filtres {@link SecurityFilterChain}.
     * @throws Exception si es produeix un error de configuració.
     */
    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/images/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Página personalizada
                .defaultSuccessUrl("/admin/usuaris", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    request.getSession().setAttribute("deniedMessage", true);
                    response.sendRedirect("/login");
                })
            )
            .logout(logout -> logout.disable());

        return http.build();
    }
    
}
