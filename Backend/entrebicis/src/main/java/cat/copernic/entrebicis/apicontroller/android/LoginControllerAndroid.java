/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.configuration.JwtUtil;
import cat.copernic.entrebicis.dto.ForgotPasswordRequest;
import cat.copernic.entrebicis.dto.LoginRequest;
import cat.copernic.entrebicis.dto.LoginResponse;
import cat.copernic.entrebicis.dto.ResetPasswordRequest;
import cat.copernic.entrebicis.dto.UsuariAndroidDto;
import cat.copernic.entrebicis.dto.ValidateCodeRequest;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encarregat de gestionar les operacions d'autenticació
 * i gestió d'usuaris per a la part Android de l'aplicació Entrebicis.
 * 
 * <p>Inclou funcionalitats com iniciar sessió, recuperar l'usuari a partir
 * del token JWT, iniciar el procés de recuperació de contrasenya, validar codis 
 * de recuperació i reiniciar contrasenyes.</p>
 * 
 * <p>Utilitza JWT per l'autenticació i proporciona respostes HTTP adequades
 * segons el resultat de cada operació.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link RestController}.</p>
 * 
 * @author reyes
 */
@RestController
@RequestMapping("/api")
public class LoginControllerAndroid {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginControllerAndroid.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UsuariLogic usuariLogic;
    
    //Autentica un usuari amb email i contrasenya, i retorna un token JWT si les credencials són correctes.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq){
        try{
            authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getParaula())
            );
            logger.info("✅ Login correcte per a: {}", loginReq.getEmail());
        }catch (AuthenticationException e) {
            logger.warn("⚠️ Error de login per a: {}", loginReq.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credencials incorrectes");
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginReq.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
    
    //Recupera les dades de l'usuari autenticat a partir del token JWT proporcionat.
    @GetMapping("/usuari")
    public ResponseEntity<?> getUsuari(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("⚠️ Accés denegat: token no proporcionat");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionat");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        logger.info("🔍 Email extret del token: {}", email);
        
        Usuari usuari = usuariLogic.findByEmail(email);
        
        if(usuari == null){
            logger.error("❌ Usuari no trobat: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
        }
        
        UsuariAndroidDto dto = usuariLogic.convertirAAndroidDTO(usuari);
        logger.info("✅ Usuari carregat: {} - Saldo: {}", dto.getNom(), dto.getSaldo());

        return ResponseEntity.ok(dto);
    }
    
    // Inicia el procés de recuperació de contrasenya enviant un codi de verificació al correu electrònic.
    @PostMapping("/forgot-pass")
    public ResponseEntity<?> passwordOblidada(@Valid @RequestBody ForgotPasswordRequest request){
        usuariLogic.iniciarRecuperacio(request.getEmail());
        logger.info("📧 Recuperació de contrasenya iniciada per a: {}", request.getEmail());
        return ResponseEntity.ok("Codi enviat");
    }
    
    //Valida un codi de recuperació enviat per correu electrònic.
    @PostMapping("/validate-code")
    public ResponseEntity<?> validarCodi(@Valid @RequestBody ValidateCodeRequest request){
        boolean valid = usuariLogic.validarCodi(request.getEmail(), request.getCodi());
        if (valid) {
            logger.info("✅ Codi de recuperació vàlid per a: {}", request.getEmail());
            return ResponseEntity.ok("Codi vàlid");
        } else {
            logger.warn("⚠️ Codi de recuperació invàlid o expirat per a: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Codi invàlid o expirat");
        }
    }
    
    //Permet reiniciar la contrasenya d'un usuari si el codi de recuperació és vàlid.
    @PostMapping("/reset-pass")
    public ResponseEntity<?> resetPass(@Valid @RequestBody ResetPasswordRequest request){
        usuariLogic.canviarContrasenya(request.getEmail(), request.getCodi(), request.getNovaContrasenya());
        logger.info("🔄 Contrasenya canviada correctament per a: {}", request.getEmail());
        return ResponseEntity.ok("Contrasenya canviada correctament");
    }
}
