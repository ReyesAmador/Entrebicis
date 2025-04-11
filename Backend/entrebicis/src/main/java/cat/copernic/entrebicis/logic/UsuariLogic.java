/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.dto.UsuariAndroidDto;
import cat.copernic.entrebicis.entities.ContrasenyaResetToken;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.enums.Rol;
import cat.copernic.entrebicis.exceptions.CampBuitException;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.repository.ContrasenyaResetTokenRepo;
import cat.copernic.entrebicis.repository.UsuariRepo;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    
    @Autowired
    ContrasenyaResetTokenRepo tokenRepo;
    
    @Autowired
    JavaMailSender mailSender;
    
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
            "Barcelona",
            new ArrayList<>(),
            new ArrayList<>());
            
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
            String contentType = imatge.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El fitxer ha de ser una imatge vàlida.");
        }
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
                String contentType = imatgeFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("El fitxer ha de ser una imatge vàlida.");
                }
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
    
    @Transactional
    public void iniciarRecuperacio(String email){
        
        usuariRepo.findByEmail(email).orElseThrow(() -> 
        new NotFoundUsuariException("Usuari no trobat: " + email));
        
        String codi = String.format("%06d", new Random().nextInt(999999));
        tokenRepo.deleteByEmail(email); //neteja tokens anteriors
        tokenRepo.save(new ContrasenyaResetToken(null, email, codi, LocalDateTime.now().plusMinutes(10)));
        
        //Enviar correu
        SimpleMailMessage missatge = new SimpleMailMessage();
        missatge.setTo(email);
        missatge.setSubject("Codi de recuperació");
        missatge.setText("El teu codi és: " + codi);
        
        try{
            mailSender.send(missatge);
            System.out.println("MISSATGE ENVIAR AMB CODI: " + codi);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR ENVIANT CORREU: " + e.getMessage());
        }
        
        System.out.println("CODI DE RECUPERACIÓ ENVIAT: " + codi);
        
    }
    
    public boolean validarCodi(String email, String codi){
        return tokenRepo.findByEmailAndCodi(email, codi)
                .filter(token -> token.getExpiracio().isAfter(LocalDateTime.now()))
                .isPresent();
    }
    
    @Transactional
    public void canviarContrasenya(String email, String codi, String novaContrasenya){
        if(!validarCodi(email,codi)) throw new IllegalArgumentException("Codi invàlid o expirat");
        
        if (!novaContrasenya.matches(regex)) {
        throw new IllegalArgumentException("La contrasenya ha de tenir almenys 4 caràcters (sense espais), una minúscula, una majúscula i un símbol.");
    }
        
        Usuari usuari = usuariRepo.findByEmail(email).orElseThrow(() -> 
        new NotFoundUsuariException("Usuari no trobat: " + email));
        usuari.setParaula(passwordEncoder.encode(novaContrasenya));
        usuariRepo.save(usuari);
        tokenRepo.deleteByEmail(email); //elimina el token
    }
    
    public UsuariAndroidDto convertirAAndroidDTO(Usuari usuari) {
        if (usuari == null) return null;

        UsuariAndroidDto dto = new UsuariAndroidDto();
        dto.setEmail(usuari.getEmail());
        dto.setNom(usuari.getNom());
        dto.setRol(usuari.getRol().name());
        dto.setSaldo(usuari.getSaldo());
        dto.setObservacions(usuari.getObservacions());
        dto.setMobil(usuari.getMobil());
        dto.setPoblacio(usuari.getPoblacio());

        if (usuari.getImatge() != null) {
            String base64 = Base64.getEncoder().encodeToString(usuari.getImatge());
            dto.setImatgeBase64(base64);
        }

        return dto;
    }
    
    public void actualitzarUsuariAndroid(String email, UsuariAndroidDto dto){
        Usuari existent = usuariRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundUsuariException("Usuari no trobat"));
        
        if(!existent.getMobil().equals(dto.getMobil()) && usuariRepo.existsByMobil(dto.getMobil())){
            throw new DuplicateException("El número de mòbil ja està registrat");
        }
        
        existent.setNom(dto.getNom());
        existent.setMobil(dto.getMobil());
        existent.setPoblacio(dto.getPoblacio());
        
        if(dto.getImatgeBase64() != null && !dto.getImatgeBase64().isEmpty()){
            byte[] imatge = Base64.getDecoder().decode(dto.getImatgeBase64());
            existent.setImatge(imatge);
        }
        
        usuariRepo.save(existent);
    }
}
