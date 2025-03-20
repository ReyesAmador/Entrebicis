/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.entrebicis.repository;

import cat.copernic.entrebicis.entities.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author reyes
 */
@Repository
public interface UsuariRepo extends JpaRepository<Usuari, String> {
    
}
