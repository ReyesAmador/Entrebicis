/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.configuration.JwtUtil;
import cat.copernic.entrebicis.dto.UsuariAndroidDto;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/api/usuari")
public class UsuariControllerAndroid {
    
    @Autowired
    private UsuariLogic usuariLogic;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PatchMapping("/actualitzar")
    public ResponseEntity<?> actualitzarUsuariAndroid(HttpServletRequest request,
            @RequestBody UsuariAndroidDto usuariDto
    ){
        try{
            String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        
        usuariLogic.actualitzarUsuariAndroid(email, usuariDto);
        
        return ResponseEntity.ok("Usuari actualitzat correctament");
        }catch (NotFoundUsuariException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
        }catch(DuplicateException e){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperat: " + e.getMessage());
        }
        
    }
    
}
