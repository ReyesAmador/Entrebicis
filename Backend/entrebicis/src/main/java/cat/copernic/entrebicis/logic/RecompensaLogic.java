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
 * Servei encarregat de gestionar tota la lògica relacionada amb les recompenses
 * a l'aplicació Entrebicis.
 * 
 * <p>Permet crear, eliminar, reservar, assignar, recollir recompenses i consultar
 * recompenses associades als usuaris.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Service}.</p>
 * 
 * <p>Utilitza {@link RecompensaRepo} per accedir a les recompenses i {@link UsuariRepo}
 * per accedir als usuaris.</p>
 * 
 * @author reyes
 */
@Service
public class RecompensaLogic {
    
    @Autowired
    RecompensaRepo repo;
    
    @Autowired
    UsuariRepo usuariRepo;
    
    /**
     * Obté la llista de totes les recompenses existents.
     *
     * @return una llista de {@link Recompensa}.
     */
    public List<Recompensa> obtenirTotes(){
        return repo.findAll();
    }
    
    /**
     * Obté una recompensa concreta pel seu identificador.
     *
     * @param id l'ID de la recompensa a consultar.
     * @return l'objecte {@link Recompensa} trobat.
     * @throws NotFoundException si no es troba la recompensa.
     */
    public Recompensa getRecompensa(Long id){
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Recompensa amb id " + id + " no trobada."));
    }
    
    /**
     * Crea una nova recompensa establint el seu estat inicial i la data de creació.
     *
     * @param r la recompensa a crear.
     */
    public void crearRecompensa(Recompensa r){
        
        r.setDescripcio(r.getDescripcio() != null ? r.getDescripcio().trim() : null);
        r.setPuntRecollida(r.getPuntRecollida() != null ? r.getPuntRecollida().trim() : null);
        r.setDireccio(r.getDireccio() != null ? r.getDireccio().trim() : null);
        
        if(r.getObservacions() != null){
            r.setObservacions(r.getObservacions().trim());
        }
        
        r.setEstat(EstatRecompensa.DISPONIBLE);
        r.setDataCreacio(LocalDate.now());
        repo.save(r);
    }
    
    /**
     * Elimina una recompensa si es troba en estat DISPONIBLE.
     *
     * @param id l'ID de la recompensa a eliminar.
     * @throws NotFoundException si no es troba la recompensa.
     * @throws IllegalStateException si la recompensa no està en estat DISPONIBLE.
     */
    public void eliminarRecompensa(Long id){
        Recompensa recompensa = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Recompensa no trobada"));
        
        if(recompensa.getEstat() != EstatRecompensa.DISPONIBLE){
            throw new IllegalStateException("Només es poden eliminar recompenses amb estat " + EstatRecompensa.DISPONIBLE.toString());
        }
        
        repo.delete(recompensa);
    }
    
    /**
     * Obté la llista de recompenses disponibles o associades a un usuari específic.
     *
     * @param email el correu electrònic de l'usuari.
     * @return una llista de {@link Recompensa}.
     */
    public List<Recompensa> obtenirRecompensesPropies(String email){
        List<Recompensa> totes = repo.findAll();
        
        return totes.stream()
                .filter(r -> r.getEstat() == EstatRecompensa.DISPONIBLE ||
                        (r.getUsuari() != null && r.getUsuari().getEmail().equals(email)))
                .toList();
    }
    
    /**
     * Reserva una recompensa per a un usuari si té saldo suficient i no té una altra recompensa reservada.
     *
     * @param email el correu de l'usuari.
     * @param idRecompensa l'ID de la recompensa a reservar.
     * @throws NotFoundUsuariException si l'usuari no es troba.
     * @throws NotFoundException si la recompensa no existeix.
     * @throws SaldoInsuficientException si l'usuari no té saldo suficient.
     * @throws RecompensaReservadaException si ja té una altra recompensa reservada.
     */
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
    
    /**
     * Assigna una recompensa reservada a un usuari si té saldo suficient.
     *
     * @param idRecompensa l'ID de la recompensa a assignar.
     * @throws NotFoundException si la recompensa no existeix.
     * @throws SaldoInsuficientException si l'usuari no té saldo suficient.
     * @throws RecompensaReservadaException si ja té una recompensa assignada.
     */
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
    
    /**
     * Marca una recompensa com a recollida per part de l'usuari propietari.
     *
     * @param email el correu electrònic de l'usuari.
     * @param idRecompensa l'ID de la recompensa a recollir.
     * @throws RuntimeException si la recompensa no pertany a l'usuari o no està en estat ASSIGNADA.
     */
    public void recollirRecompensa(String email, Long idRecompensa){
        Recompensa recompensa = repo.findById(idRecompensa)
                .orElseThrow(() -> new NotFoundException("Recompensa no trobada"));
        
        if (!recompensa.getUsuari().getEmail().equals(email)) {
            throw new RuntimeException("No tens permís per modificar aquesta recompensa");
        }
        
        if (recompensa.getEstat() != EstatRecompensa.ASSIGNADA) {
            throw new RuntimeException("La recompensa no està en estat ASSIGNADA");
        }
        
        recompensa.setEstat(EstatRecompensa.RECOLLIDA);
        repo.save(recompensa);
    }
    
    /**
     * Obté totes les recompenses associades a un usuari.
     *
     * @param email el correu electrònic de l'usuari.
     * @return una llista de recompenses.
     */
    public List<Recompensa> getRecompensesByUsuari(String email) {
        return repo.findByUsuariEmail(email);
    }
}
