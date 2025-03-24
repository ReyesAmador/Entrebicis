/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.ParametresSistema;
import cat.copernic.entrebicis.repository.ParametresSistemaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author reyes
 */
@Service
public class ParametresSistemaLogic {
    
    @Autowired
    ParametresSistemaRepo repo;
    
    public ParametresSistema obtenirParametres(){
        return repo.findById(1L).orElseGet(() -> {
            ParametresSistema p = new ParametresSistema();
            p.setVelocitatMaxima(60);
            p.setTempsMaximAturat(5);
            p.setConversio(1);
            p.setTempsMaximRecollida(72);
            return repo.save(p);
        });
    }
    
    public void guardarParametres(ParametresSistema par){
        par.setId(1L);
        repo.save(par);
    }
    
}
