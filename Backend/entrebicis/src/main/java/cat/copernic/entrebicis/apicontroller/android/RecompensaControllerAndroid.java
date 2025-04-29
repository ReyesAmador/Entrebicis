/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.dto.RecompensaDetallDTO;
import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.exceptions.NotFoundException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.exceptions.RecompensaReservadaException;
import cat.copernic.entrebicis.exceptions.SaldoInsuficientException;
import cat.copernic.entrebicis.logic.RecompensaLogic;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Controlador REST encarregat de gestionar les operacions relacionades
 * amb les recompenses dels usuaris a la part Android de l'aplicació Entrebicis.
 * 
 * <p>Permet llistar recompenses pròpies, reservar recompenses disponibles,
 * visualitzar el detall d'una recompensa i confirmar-ne la recollida.</p>
 * 
 * <p>Utilitza {@link RecompensaLogic} per delegar la lògica de negoci
 * i gestiona tant casos d'error com excepcions específiques de recompenses.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * @author reyes
 */
@Controller
@RequestMapping("/api/recompenses")
public class RecompensaControllerAndroid {
    
    private static final Logger logger = LoggerFactory.getLogger(RecompensaControllerAndroid.class);
    
    @Autowired
    RecompensaLogic recoLogic;
    
    // Retorna la llista de recompenses associades a l'usuari autenticat.
    @GetMapping
    public ResponseEntity<List<Recompensa>> llistarRecompensesPropies(Principal principal){
        String email = principal.getName();
        List<Recompensa> recompenses = recoLogic.obtenirRecompensesPropies(email);
        
        logger.info("🎁 Llista de recompenses pròpies carregada per a: {}", email);
        
        return ResponseEntity.ok(recompenses);
    }
    
    // Permet a l'usuari reservar una recompensa si compleix les condicions.
    @PostMapping("/reservar/{id}")
    public ResponseEntity<?> reservarRecompensa(Principal principal, @PathVariable Long id){
        String email = principal.getName();
        try{
            recoLogic.reservarRecompensa(principal.getName(), id);
            logger.info("✅ Recompensa ID {} reservada per l'usuari {}", id, email);
            return ResponseEntity.ok("Recompensa reservada correctament");
        }catch(NotFoundUsuariException e){
            logger.warn("⚠️ Usuari no trobat reservant recompensa: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuari no trobat");
        }catch(NotFoundException | SaldoInsuficientException | RecompensaReservadaException e){
            logger.warn("⚠️ Error reservant recompensa ID {} per {}: {}", id, email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            logger.error("❌ Error inesperat reservant recompensa ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperat");
        }
    }
    
    // Mostra el detall d'una recompensa específica, validant permisos d'accés.
    @GetMapping("/{id}")
    public ResponseEntity<?> mostrarRecompensa(@PathVariable Long id, Principal principal){
        try{
            String email = principal.getName();
            Recompensa recompensa = recoLogic.getRecompensa(id);
            
            // ⚠️ Validar si la recompensa tiene usuario asignado y no es el logueado
            if (recompensa.getUsuari() != null && !recompensa.getUsuari().getEmail().equals(email)) {
                logger.warn("⚠️ Accés denegat per veure recompensa ID {} per {}", id, email);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tens permís per veure aquesta recompensa");
            }
            
            logger.info("🔍 Detall de recompensa ID {} mostrat a {}", id, email);
            
            return ResponseEntity.ok(RecompensaDetallDTO.from(recompensa));
        }catch(NotFoundException e){
            logger.error("❌ Recompensa ID {} no trobada.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // Marca una recompensa com a recollida per part de l'usuari.
    @PatchMapping("/recollir/{id}")
    public ResponseEntity<?> recollirRecompensa(@PathVariable Long id, Principal principal){
        String email = principal.getName();
        try{
            recoLogic.recollirRecompensa(email, id);
            logger.info("🎯 Recompensa ID {} recollida per l'usuari {}", id, email);
            return ResponseEntity.ok().build();
        }catch(RuntimeException e){
            logger.warn("⚠️ Error recollint recompensa ID {} per {}: {}", id, email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
