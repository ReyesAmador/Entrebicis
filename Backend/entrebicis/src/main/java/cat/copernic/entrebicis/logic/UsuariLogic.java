/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.enums.Rol;
import cat.copernic.entrebicis.exceptions.CampBuitException;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.repository.UsuariRepo;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    
    private static final String regex= "^(?=\\S{4,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).*$";
    
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
        
        if(usuari.getParaula() == null || usuari.getParaula().isBlank())
            throw new CampBuitException("La contrasenya no pot estar buida.");
        
        if(!usuari.getParaula().matches(regex))
            throw new IllegalArgumentException("La contrasenya ha de tenir almenys 4 caràcters (sense espais), una minúscula, una majúscula i un símbol.");
        
        usuari.setParaula(passwordEncoder.encode(usuari.getParaula())); //Encriptar contrasenya
        
        usuari.setRol(Rol.CICLISTA);
        
        return usuariRepo.save(usuari);
    }
    
    public Usuari findByEmail(String email) {
        return usuariRepo.findById(email).orElseThrow(
        () -> new NotFoundUsuariException("Usuari no trobat amb email: " +email));
    }
    
    public Usuari actualitzarUsuari(String email, Usuari usuariModificat, MultipartFile imatgeFile) throws IOException{
        Usuari usuariActual = usuariRepo.findById(email)
        .orElseThrow(() -> new NotFoundUsuariException("Usuari no trobat: " + email));
        
        if (usuariModificat.getSaldo() < 0) {
            throw new IllegalArgumentException("El saldo no pot ser negatiu.");
        }

        if (usuariModificat.getMobil() != null &&
            !usuariModificat.getMobil().equals(usuariActual.getMobil()) &&
            usuariRepo.existsByMobil(usuariModificat.getMobil())) {
            throw new DuplicateException("El número del mòbil ja està en ús.");
        }
        

            if (imatgeFile != null && !imatgeFile.isEmpty()) {
                usuariActual.setImatge(imatgeFile.getBytes());
            }
         
        // Actualitzar camps
        usuariActual.setNom(usuariModificat.getNom());
        usuariActual.setSaldo(usuariModificat.getSaldo());
        usuariActual.setObservacions(usuariModificat.getObservacions());
        usuariActual.setPoblacio(usuariModificat.getPoblacio());
        usuariActual.setMobil(usuariModificat.getMobil());

        return usuariRepo.save(usuariActual);
    }
}
