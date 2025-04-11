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
    public ResponseEntity<Void> finalitzarRuta(Principal principal){
        String email = principal.getName();
        rutaLogic.finalitzarRuta(email);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/detall")
    public ResponseEntity<RutaAmbPuntsGps> obtenirPuntsRuta(Principal principal){
        RutaAmbPuntsGps dto = rutaLogic.getDetallUltimaRutaFinalitzada(principal.getName());
        
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/finalitzades")
    public ResponseEntity<List<RutaSenseGps>> obtenirRutesFinalitades(Principal principal){
        String email = principal.getName();
        
        List<RutaSenseGps> rutes = rutaLogic.obtenirRutesUsuari(email);
        
        return ResponseEntity.ok(rutes);
    }
    
    @GetMapping("/detall/{id}")
    public ResponseEntity<RutaAmbPuntsGps> obtenirDetallRuta(@PathVariable Long id) {
        try {
            RutaAmbPuntsGps dto = rutaLogic.getDetallRutaAmbPunts(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}
