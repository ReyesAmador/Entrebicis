/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.repository.UsuariRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei {@link UserDetailsService} que proporciona
 * la càrrega dels detalls d'un usuari basat en el seu correu electrònic
 * per a la gestió d'autenticació de l'aplicació Entrebicis.
 * 
 * <p>Recupera la informació de l'usuari des del repositori {@link UsuariRepo}
 * i construeix un objecte {@link UserDetails} necessari per a Spring Security.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Service}.</p>
 * 
 * @author reyes
 */
@Service
public class ValidadorUsuaris implements UserDetailsService {
    
    @Autowired
    UsuariRepo usuariRepo;
    
    /**
     * Carrega els detalls d'un usuari basant-se en el seu correu electrònic.
     * 
     * <p>Si l'usuari no es troba, llança una {@link UsernameNotFoundException}.</p>
     *
     * @param email el correu electrònic de l'usuari que es vol autenticar.
     * @return l'objecte {@link UserDetails} amb la informació de l'usuari.
     * @throws UsernameNotFoundException si no es troba cap usuari amb el correu proporcionat.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuari usuari = usuariRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari no trobat: " + email));
        
        return User.builder()
                .username(usuari.getEmail())
                .password(usuari.getParaula())
                .roles(usuari.getRol().name())
                .build();
    }
    
}
