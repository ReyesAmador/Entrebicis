/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.dto.PuntGpsDTO;
import cat.copernic.entrebicis.dto.RutaAmbPuntsGps;
import cat.copernic.entrebicis.dto.RutaSenseGps;
import cat.copernic.entrebicis.logic.RutaLogic;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encarregat de gestionar les operacions de rutes
 * per a la part Android de l'aplicació Entrebicis.
 * 
 * <p>Permet als usuaris afegir punts GPS durant una ruta, finalitzar rutes,
 * consultar la seva última ruta finalitzada, veure totes les rutes finalitzades
 * i obtenir el detall d'una ruta específica.</p>
 * 
 * <p>Utilitza {@link RutaLogic} per a la lògica de negoci relacionada amb rutes.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link RestController}.</p>
 * 
 * @author reyes
 */
@RestController
@RequestMapping("/api/ruta")
@RequiredArgsConstructor
public class RutaControllerAndroid {
    
    private static final Logger logger = LoggerFactory.getLogger(RutaControllerAndroid.class);
    
    @Autowired
    RutaLogic rutaLogic;
    
    // Afegeix un nou punt GPS a la ruta activa de l'usuari.
    @PostMapping("/punt")
    public ResponseEntity<Void> afegirPunt(Principal principal, @RequestBody PuntGpsDTO dto) {
        String email = principal.getName();
        rutaLogic.afegirPuntGps(email, dto);
        logger.info("📍 Punt GPS afegit per l'usuari {}", email);
        return ResponseEntity.ok().build();
    }
    
    // Finalitza la ruta activa de l'usuari.
    @PatchMapping("/finalitzar")
    public ResponseEntity<Void> finalitzarRuta(Principal principal){
        String email = principal.getName();
        rutaLogic.finalitzarRuta(email);
        logger.info("🏁 Ruta finalitzada per l'usuari {}", email);
        return ResponseEntity.ok().build();
    }
    
    // Retorna els punts GPS de l'última ruta finalitzada de l'usuari.
    @GetMapping("/detall")
    public ResponseEntity<RutaAmbPuntsGps> obtenirPuntsRuta(Principal principal){
        String email = principal.getName();
        RutaAmbPuntsGps dto = rutaLogic.getDetallUltimaRutaFinalitzada(principal.getName());
        
        if (dto != null) {
            logger.info("🔍 Ruta finalitzada trobada per l'usuari {}", email);
            return ResponseEntity.ok(dto);
        } else {
            logger.warn("⚠️ No s'ha trobat cap ruta finalitzada per l'usuari {}", email);
            return ResponseEntity.notFound().build();
        }
    }
    
    // Retorna la llista de totes les rutes finalitzades de l'usuari.
    @GetMapping("/finalitzades")
    public ResponseEntity<List<RutaSenseGps>> obtenirRutesFinalitades(Principal principal){
        String email = principal.getName();
        
        List<RutaSenseGps> rutes = rutaLogic.obtenirRutesUsuari(email);
        
        logger.info("📄 {} rutes finalitzades carregades per l'usuari {}", rutes.size(), email);
        
        return ResponseEntity.ok(rutes);
    }
    
    // Retorna el detall (amb punts GPS) d'una ruta concreta pel seu ID.
    @GetMapping("/detall/{id}")
    public ResponseEntity<RutaAmbPuntsGps> obtenirDetallRuta(@PathVariable Long id) {
        try {
            RutaAmbPuntsGps dto = rutaLogic.getDetallRutaAmbPunts(id);
            logger.info("🔍 Detall de la ruta ID {} carregat correctament.", id);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            logger.warn("⚠️ Error al obtenir detall de la ruta ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            logger.error("❌ Ruta ID {} no trobada.", id);
            return ResponseEntity.notFound().build();
        }
    }
    
}
