/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.logic.RecompensaLogic;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/api/recompenses")
public class RecompensaControllerAndroid {
    
    @Autowired
    RecompensaLogic recoLogic;
    
    @GetMapping
    public ResponseEntity<List<Recompensa>> llistarRecompensesPropies(Principal principal){
        String email = principal.getName();
        List<Recompensa> recompenses = recoLogic.obtenirRecompensesPropies(email);
        
        return ResponseEntity.ok(recompenses);
    }
}
