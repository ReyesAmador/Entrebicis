/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.dto.PuntGpsDTO;
import cat.copernic.entrebicis.entities.PuntGps;
import cat.copernic.entrebicis.entities.Ruta;
import cat.copernic.entrebicis.repository.PuntGpsRepo;
import cat.copernic.entrebicis.repository.RutaRepo;
import java.time.Instant;
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
    
    public void afegirPuntGps(Long idRuta, PuntGpsDTO dto){
        
        Ruta ruta = rutaRepo.findById(idRuta)
                .orElseThrow(() -> new RuntimeException("Ruta no trobada"));
        
        PuntGps punt = new PuntGps();
        punt.setTemps(Instant.ofEpochMilli(dto.getTemps()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        punt.setLatitud(dto.getLatitud());
        punt.setLongitud(dto.getLongitud());
        punt.setRuta(ruta);
        
        puntRepo.save(punt);
    }
}
