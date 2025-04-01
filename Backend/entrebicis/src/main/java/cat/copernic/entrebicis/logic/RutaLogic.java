/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.dto.PuntGpsDTO;
import cat.copernic.entrebicis.entities.PuntGps;
import cat.copernic.entrebicis.entities.Ruta;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.repository.PuntGpsRepo;
import cat.copernic.entrebicis.repository.RutaRepo;
import cat.copernic.entrebicis.repository.UsuariRepo;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author reyes
 */
@Service
public class RutaLogic {
    
    @Autowired
    RutaRepo rutaRepo;
    
    @Autowired
    PuntGpsRepo puntRepo;
    
    @Autowired
    UsuariRepo usuariRepo;
    
    public void afegirPuntGps(String email, PuntGpsDTO dto){
        
        Usuari usuari = usuariRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));
        
        Ruta ruta = rutaRepo.findByUsuariAndEstatTrue(usuari)
                .orElseGet(() -> {
                    Ruta nova = new Ruta();
                    nova.setUsuari(usuari);
                    nova.setEstat(true);
                    nova.setInici(LocalDateTime.now());
                    return rutaRepo.save(nova);
                });
        
        PuntGps punt = new PuntGps();
        punt.setTemps(Instant.ofEpochMilli(dto.getTemps()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        punt.setLatitud(dto.getLatitud());
        punt.setLongitud(dto.getLongitud());
        punt.setRuta(ruta);
        
        puntRepo.save(punt);
    }
}
