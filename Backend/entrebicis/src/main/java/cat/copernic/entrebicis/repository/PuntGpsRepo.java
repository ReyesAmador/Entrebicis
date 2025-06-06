/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.entrebicis.repository;

import cat.copernic.entrebicis.entities.PuntGps;
import cat.copernic.entrebicis.entities.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author reyes
 */
public interface PuntGpsRepo extends JpaRepository<PuntGps, Long>{
    
    List<PuntGps> findByRutaOrderByTempsAsc(Ruta ruta);
    
}
