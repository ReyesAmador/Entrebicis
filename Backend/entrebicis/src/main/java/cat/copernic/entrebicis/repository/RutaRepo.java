/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.entrebicis.repository;

import cat.copernic.entrebicis.entities.Ruta;
import cat.copernic.entrebicis.entities.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author reyes
 */
public interface RutaRepo extends JpaRepository<Ruta, Long>{
    
    Optional<Ruta> findByUsuariAndEstatTrue(Usuari usuari);
    
}
