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
 *
 * @author reyes
 */
@Service
public class ValidadorUsuaris implements UserDetailsService {
    
    @Autowired
    UsuariRepo usuariRepo;
    
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
