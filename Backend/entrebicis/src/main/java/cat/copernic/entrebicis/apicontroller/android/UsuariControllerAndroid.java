/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.configuration.JwtUtil;
import cat.copernic.entrebicis.dto.CanviContrasenyaRequest;
import cat.copernic.entrebicis.dto.UsuariAndroidDto;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST encarregat de gestionar l'actualització de les dades
 * dels usuaris des de la part Android de l'aplicació Entrebicis.
 * 
 * <p>Permet als usuaris modificar el seu perfil, controlant errors de
 * duplicats, usuaris inexistents o altres incidències.</p>
 * 
 * <p>Utilitza {@link UsuariLogic} per aplicar els canvis i {@link JwtUtil}
 * per validar el token i identificar l'usuari.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * 
 * @author reyes
 */
@Controller
@RequestMapping("/api/usuari")
public class UsuariControllerAndroid {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariControllerAndroid.class);
    
    @Autowired
    private UsuariLogic usuariLogic;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Actualitza les dades de perfil de l'usuari autenticat a partir del token JWT.
    @PatchMapping("/actualitzar")
    public ResponseEntity<?> actualitzarUsuariAndroid(HttpServletRequest request,
            @RequestBody UsuariAndroidDto usuariDto
    ){
        try{
            String token = request.getHeader("Authorization").substring(7);
            String email = jwtUtil.extractUsername(token);

            usuariLogic.actualitzarUsuariAndroid(email, usuariDto);
            
            logger.info("✅ Usuari {} actualitzat correctament.", email);

            return ResponseEntity.ok("Usuari actualitzat correctament");
        }catch (NotFoundUsuariException e) {
            logger.error("❌ Error: usuari no trobat per actualitzar perfil.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
        }catch(DuplicateException e){
            logger.warn("⚠️ Error: duplicat detectat en actualitzar usuari: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            logger.error("❌ Error inesperat actualitzant usuari: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperat: " + e.getMessage());
        }
        
    }
    
    @PostMapping("/canvi-pass")
    public ResponseEntity<?> canviContrasenya(HttpServletRequest request, @RequestBody CanviContrasenyaRequest dto){
        String email = jwtUtil.extractUsername(request.getHeader("Authorization").substring(7));
        
        try{
            usuariLogic.canviarContrasenyaPerfil(email, dto.getActual(), dto.getNova(), dto.getRepetirNova());
            return ResponseEntity.ok("Contrasenya canviada correctament");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
