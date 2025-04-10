/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.repository.RecompensaRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author reyes
 */
@Service
public class RecompensaLogic {
    
    @Autowired
    RecompensaRepo repo;
    
    public List<Recompensa> obtenirTotes(){
        return repo.findAll();
    }
    
    public void crearRecompensa(Recompensa r){
        repo.save(r);
    }
}
