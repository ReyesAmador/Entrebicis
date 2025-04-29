/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.entities.ParametresSistema;
import cat.copernic.entrebicis.repository.ParametresSistemaRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei encarregat de gestionar la lògica relacionada amb els paràmetres
 * configurables del sistema a l'aplicació Entrebicis.
 * 
 * <p>Proporciona mètodes per inicialitzar, obtenir i actualitzar els paràmetres
 * com la velocitat màxima permesa, el temps màxim d'aturada, i la conversió de km a punts.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Service}.</p>
 * 
 * @author reyes
 */
@Service
public class ParametresSistemaLogic {
    
    @Autowired
    ParametresSistemaRepo repo;
    
    /**
     * Inicialitza els paràmetres del sistema si encara no existeixen a la base de dades.
     * 
     * <p>Assigna valors per defecte com la velocitat màxima, temps màxim d'aturada,
     * conversió km -> punts i temps màxim de recollida.</p>
     */
    @PostConstruct
    public void inicialitzarParametres(){
        if(repo.findById(1L).isEmpty()){
            ParametresSistema p = new ParametresSistema();
            p.setVelocitatMaxima(60);
            p.setTempsMaximAturat(5);
            p.setConversio(1);
            p.setTempsMaximRecollida(72);
            repo.save(p);
        };
    }
    
    /**
     * Obté l'objecte complet dels paràmetres del sistema.
     *
     * @return l'objecte {@link ParametresSistema} actual.
     * @throws RuntimeException si no es troben els paràmetres.
     */
    public ParametresSistema obtenirParametres() {
        return repo.findById(1L).orElseThrow();
    }
    
    /**
     * Desa (actualitza) els paràmetres del sistema a la base de dades.
     *
     * @param par els nous paràmetres a guardar.
     */
    public void guardarParametres(ParametresSistema par){
        par.setId(1L);
        repo.save(par);
    }
    
    /**
     * Obté el valor del temps màxim permès d'aturada (en minuts).
     *
     * @return el temps màxim d'aturada.
     * @throws RuntimeException si no es troben els paràmetres del sistema.
     */
    public int obtenirTempsMaximAturat() {
        ParametresSistema parametres = repo.findById(1L)
            .orElseThrow(() -> new RuntimeException("Paràmetres del sistema no trobats"));
        return parametres.getTempsMaximAturat();
    }
    
    /**
     * Obté el valor de la velocitat màxima permesa (en km/h).
     *
     * @return la velocitat màxima.
     * @throws RuntimeException si no es troben els paràmetres del sistema.
     */
    public int obtenirVelocitatMaxima() {
        ParametresSistema parametres = repo.findById(1L)
            .orElseThrow(() -> new RuntimeException("Paràmetres del sistema no trobats"));
        return parametres.getVelocitatMaxima();
    }
    
    /**
     * Obté el factor de conversió de quilòmetres a punts.
     *
     * @return el valor de conversió de km a punts.
     * @throws RuntimeException si no es troben els paràmetres del sistema.
     */
    public double obtenirConversioKmPunts(){
        ParametresSistema parametres = repo.findById(1L)
                .orElseThrow(() -> new RuntimeException("Paràmetres del sistema no trobats"));
        return parametres.getConversio();
    }
    
}
