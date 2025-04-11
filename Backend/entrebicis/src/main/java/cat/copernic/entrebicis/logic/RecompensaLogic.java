/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.enums.EstatRecompensa;
import cat.copernic.entrebicis.exceptions.NotFoundException;
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
        
        r.setDescripcio(r.getDescripcio() != null ? r.getDescripcio().trim() : null);
        r.setPuntRecollida(r.getPuntRecollida() != null ? r.getPuntRecollida().trim() : null);
        r.setDireccio(r.getDireccio() != null ? r.getDireccio().trim() : null);
        
        if(r.getObservacions() != null){
            r.setObservacions(r.getObservacions().trim());
        }
        
        r.setEstat(EstatRecompensa.DISPONIBLE);
        repo.save(r);
    }
    
    public void eliminarRecompensa(Long id){
        Recompensa recompensa = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Recompensa no trobada"));
        
        if(recompensa.getEstat() != EstatRecompensa.DISPONIBLE){
            throw new IllegalStateException("Només es poden eliminar recompenses amb estat " + EstatRecompensa.DISPONIBLE.toString());
        }
        
        repo.delete(recompensa);
    }
    
    public List<Recompensa> obtenirRecompensesPropies(String email){
        List<Recompensa> totes = repo.findAll();
        
        return totes.stream()
                .filter(r -> r.getEstat() == EstatRecompensa.DISPONIBLE ||
                        (r.getUsuari() != null && r.getUsuari().getEmail().equals(email)))
                .toList();
    }
}
