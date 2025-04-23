/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.enums.EstatRecompensa;
import cat.copernic.entrebicis.exceptions.NotFoundException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.exceptions.RecompensaReservadaException;
import cat.copernic.entrebicis.exceptions.SaldoInsuficientException;
import cat.copernic.entrebicis.repository.RecompensaRepo;
import cat.copernic.entrebicis.repository.UsuariRepo;
import java.time.LocalDate;
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
    
    @Autowired
    UsuariRepo usuariRepo;
    
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
    
    public void reservarRecompensa(String email, Long idRecompensa){
        Usuari usuari = usuariRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundUsuariException(email));
        
        Recompensa recompensa = repo.findById(idRecompensa)
                .orElseThrow(() -> new NotFoundException("Recompensa no trobada"));
        
        //validació saldo
        if(usuari.getSaldo() < recompensa.getValor()){
            throw new SaldoInsuficientException("No tens saldo suficient");
        }
        
        //validació si ja té recompensa reservada
        boolean jaReservada = repo.existsByUsuariEmailAndEstat(email, EstatRecompensa.RESERVADA);
        if(jaReservada){
            throw new RecompensaReservadaException("Ja tens una recompensa reservada");
        }
        
        //aplicar canvis
        recompensa.setUsuari(usuari);
        recompensa.setEstat(EstatRecompensa.RESERVADA);
        
        //guardar canvis
        repo.save(recompensa);
    }
    
    public void assignarRecompensa (Long idRecompensa){
        Recompensa recompensa = repo.findById(idRecompensa)
                .orElseThrow(() -> new NotFoundException("Recompensa no trobada"));
        
        Usuari usuari = recompensa.getUsuari();
        
        //validació saldo
        if(usuari.getSaldo() < recompensa.getValor()){
            throw new SaldoInsuficientException("Saldo insuficient per assignar la recompensa");
        }
        
        //validació si ja té recompensa reservada
        boolean jaReservada = repo.existsByUsuariEmailAndEstat(usuari.getEmail(), EstatRecompensa.ASSIGNADA);
        if(jaReservada){
            throw new RecompensaReservadaException("L'usuari ja té una recompensa assignada.");
        }
        
        //aplicar canvis
        recompensa.setEstat(EstatRecompensa.ASSIGNADA);
        recompensa.setDataAssignacio(LocalDate.now());
        repo.save(recompensa);
        
        usuari.setSaldo(usuari.getSaldo() - recompensa.getValor());
        usuariRepo.save(usuari);
    }
}
