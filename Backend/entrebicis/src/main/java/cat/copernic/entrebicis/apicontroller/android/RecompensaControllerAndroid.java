/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.exceptions.NotFoundException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.exceptions.RecompensaReservadaException;
import cat.copernic.entrebicis.exceptions.SaldoInsuficientException;
import cat.copernic.entrebicis.logic.RecompensaLogic;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @PostMapping("/reservar/{id}")
    public ResponseEntity<?> reservarRecompensa(Principal principal, @PathVariable Long id){
        try{
            recoLogic.reservarRecompensa(principal.getName(), id);
            return ResponseEntity.ok("Recompensa reservada correctament");
        }catch(NotFoundUsuariException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuari no trobat");
        }catch(NotFoundException | SaldoInsuficientException | RecompensaReservadaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperat");
        }
    }
}
