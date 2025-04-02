/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.dto.PuntGpsDTO;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.logic.RutaLogic;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author reyes
 */
@RestController
@RequestMapping("/api/ruta")
@RequiredArgsConstructor
public class RutaControllerAndroid {
    
    @Autowired
    RutaLogic rutaLogic;
    
    @PostMapping("/punt")
    public ResponseEntity<Void> afegirPunt(Principal principal, @RequestBody PuntGpsDTO dto) {
        String email = principal.getName();
        rutaLogic.afegirPuntGps(email, dto);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/finalitzar")
    public ResponseEntity<Void> finalitzarRuta(@AuthenticationPrincipal Usuari usuari){
        rutaLogic.finalitzarRuta(usuari.getEmail());
        return ResponseEntity.ok().build();
    }
    
}
