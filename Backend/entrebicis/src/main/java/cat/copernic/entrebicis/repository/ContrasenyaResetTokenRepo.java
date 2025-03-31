/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.entrebicis.repository;

import cat.copernic.entrebicis.entities.ContrasenyaResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author reyes
 */
@Repository
public interface ContrasenyaResetTokenRepo extends JpaRepository<ContrasenyaResetToken,Long>{
    
    Optional<ContrasenyaResetToken> findByEmailAndCodi(String email, String codi);
    Optional<ContrasenyaResetToken> findByEmail(String email);
    void deleteByEmail(String email); // para evitar duplicados
}
