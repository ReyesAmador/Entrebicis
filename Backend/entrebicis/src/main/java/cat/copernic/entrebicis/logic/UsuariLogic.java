/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.enums.Rol;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.repository.UsuariRepo;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author reyes
 */
@Service
public class UsuariLogic {
    
    @Autowired
    UsuariRepo usuariRepo;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public void crearUsuariAdmin(){
        if(usuariRepo.findById("admin@email.com").isEmpty()){
            String passencrypt = passwordEncoder.encode("admin123");
            Usuari admin = new Usuari(
            "admin@email.com",
            passencrypt,
            "Admin",
            Rol.ADMIN,
            null,
            100.0,
            "Usuari de proves",
            "663587620",
            "Barcelona");
            
            usuariRepo.save(admin);
        }
    }
    
    public List<Usuari> obtenirTotsUsuaris(){
        return usuariRepo.findAll();
    }
    
    public Usuari crearUsuari(Usuari usuari, MultipartFile imatge) throws IOException{
        if(usuariRepo.existsByEmail(usuari.getEmail()))
            throw new DuplicateException("El correu ja està registrat.");
        
        if(usuariRepo.existsByMobil(usuari.getMobil()))
            throw new DuplicateException("El número del mòbil ja està en ús.");
        
        if (usuari.getSaldo() < 0)
            throw new IllegalArgumentException("El saldo no pot ser negatiu.");
        
        if(imatge != null && !imatge.isEmpty()){
            usuari.setImatge(imatge.getBytes()); //Guardar imatge en bytes[]
        }
        
        usuari.setParaula(passwordEncoder.encode(usuari.getParaula())); //Encriptar contrasenya
        
        usuari.setRol(Rol.CICLISTA);
        
        return usuariRepo.save(usuari);
    }
}
