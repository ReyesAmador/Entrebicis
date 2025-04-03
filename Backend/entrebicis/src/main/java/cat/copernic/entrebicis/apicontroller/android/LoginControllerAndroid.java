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
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
 *
 * @author reyes
 */
@RestController
@RequestMapping("/api")
public class LoginControllerAndroid {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UsuariLogic usuariLogic;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq){
        try{
            authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getParaula())
            );
        }catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credencials incorrectes");
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginReq.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
    
    @GetMapping("/usuari")
    public ResponseEntity<?> getUsuari(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionat");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        System.out.println("🔍 Email extret del token: " + email);
        
        Usuari usuari = usuariLogic.findByEmail(email);
        
        if(usuari == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
        }
        
        UsuariAndroidDto dto = usuariLogic.convertirAAndroidDTO(usuari);
        System.out.println(">>> Usuari retornat: " + dto.getNom() + " - " + dto.getSaldo());

        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/forgot-pass")
    public ResponseEntity<?> passwordOblidada(@Valid @RequestBody ForgotPasswordRequest request){
        usuariLogic.iniciarRecuperacio(request.getEmail());
        return ResponseEntity.ok("Codi enviat");
    }
    
    @PostMapping("/validate-code")
    public ResponseEntity<?> validarCodi(@Valid @RequestBody ValidateCodeRequest request){
        boolean valid = usuariLogic.validarCodi(request.getEmail(), request.getCodi());
        return valid ? ResponseEntity.ok("Codi vàlid") :
                       ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Codi invàlid o expirat");
    }
    
    @PostMapping("/reset-pass")
    public ResponseEntity<?> resetPass(@Valid @RequestBody ResetPasswordRequest request){
        usuariLogic.canviarContrasenya(request.getEmail(), request.getCodi(), request.getNovaContrasenya());
        return ResponseEntity.ok("Contrasenya canviada correctament");
    }
}
