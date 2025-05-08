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
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servei encarregat de gestionar tota la lògica relacionada amb els usuaris
 * a l'aplicació Entrebicis.
 * 
 * <p>Inclou operacions de creació, actualització, recuperació de contrasenyes,
 * conversió a DTO per Android i gestió de la informació personal d'usuaris.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Service}.</p>
 * 
 * <p>Utilitza {@link UsuariRepo}, {@link ContrasenyaResetTokenRepo} i {@link JavaMailSender}.</p>
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
    
    /**
     * Crea un usuari administrador predeterminat si encara no existeix.
     */
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
    
    /**
     * Obté tots els usuaris existents.
     *
     * @return una llista de {@link Usuari}.
     */
    public List<Usuari> obtenirTotsUsuaris(){
        return usuariRepo.findAll();
    }
    
    /**
     * Crea un nou usuari amb validacions i pujada d'imatge.
     *
     * @param usuari l'objecte usuari a crear.
     * @param imatge el fitxer d'imatge de perfil.
     * @return l'usuari creat.
     * @throws IOException si hi ha un error en processar la imatge.
     */
    public Usuari crearUsuari(Usuari usuari, MultipartFile imatge) throws IOException{
        usuari.setEmail(usuari.getEmail().toLowerCase());
        
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
    
    /**
     * Cerca un usuari pel seu correu electrònic.
     *
     * @param email el correu de l'usuari.
     * @return l'usuari trobat.
     * @throws NotFoundUsuariException si no es troba l'usuari.
     */
    public Usuari findByEmail(String email) {
        return usuariRepo.findById(email).orElseThrow(
        () -> new NotFoundUsuariException("Usuari no trobat amb email: " +email));
    }
    
    /**
     * Actualitza les dades d'un usuari existent.
     *
     * @param email el correu de l'usuari a actualitzar.
     * @param usuariModificat les noves dades de l'usuari.
     * @param imatgeFile nova imatge de perfil.
     * @return l'usuari actualitzat.
     * @throws IOException si hi ha un error en processar la imatge.
     */
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
    
    /**
     * Inicia el procés de recuperació de contrasenya enviant un codi per correu electrònic.
     *
     * @param email el correu de l'usuari.
     */
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
    
    /**
     * Valida si un codi de recuperació de contrasenya és vàlid i no ha expirat.
     *
     * @param email el correu de l'usuari.
     * @param codi el codi de recuperació.
     * @return {@code true} si el codi és vàlid, {@code false} si no ho és.
     */
    public boolean validarCodi(String email, String codi){
        return tokenRepo.findByEmailAndCodi(email, codi)
                .filter(token -> token.getExpiracio().isAfter(LocalDateTime.now()))
                .isPresent();
    }
    
    /**
     * Canvia la contrasenya d'un usuari després de validar el codi.
     *
     * @param email el correu de l'usuari.
     * @param codi el codi de validació.
     * @param novaContrasenya la nova contrasenya.
     */
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
    
    /**
     * Converteix un objecte {@link Usuari} en un {@link UsuariAndroidDto} per a ús a Android.
     *
     * @param usuari l'usuari a convertir.
     * @return l'objecte {@link UsuariAndroidDto} corresponent.
     */
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
    
    /**
     * Actualitza les dades d'un usuari des de l'app Android.
     *
     * @param email el correu de l'usuari a actualitzar.
     * @param dto les noves dades de l'usuari en format {@link UsuariAndroidDto}.
     */
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
    
    public void canviarContrasenyaPerfil(String email, String actual, String nova, String repetirNova){
        Usuari usuari = usuariRepo.findByEmail(email)
        .orElseThrow(() -> new NotFoundUsuariException("Usuari no trobat"));
        
        // Validar contrasenya actual
        if (!passwordEncoder.matches(actual, usuari.getParaula())) {
            throw new IllegalArgumentException("La contrasenya actual no és correcta");
        }

        // Validar que les dues noves coincideixen
        if (!nova.equals(repetirNova)) {
            throw new IllegalArgumentException("La nova contrasenya no coincideix en ambdós camps");
        }

        // Validar que la nova contrasenya compleix requisits
        if (!nova.matches(regex)) {
            throw new IllegalArgumentException("La nova contrasenya ha de tenir almenys 4 caràcters, una minúscula, una majúscula i un símbol, sense espais");
        }

        // Guardar nova contrasenya encriptada
        usuari.setParaula(passwordEncoder.encode(nova));
        usuariRepo.save(usuari);
    }
}
