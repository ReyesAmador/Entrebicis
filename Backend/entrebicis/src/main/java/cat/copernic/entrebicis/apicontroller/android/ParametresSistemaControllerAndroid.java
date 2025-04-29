/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.logic.ParametresSistemaLogic;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Controlador REST encarregat de proporcionar als clients Android
 * els paràmetres de sistema configurables de l'aplicació Entrebicis.
 * 
 * <p>Actualment, exposa el paràmetre del temps màxim d'aturada,
 * que indica el temps màxim d'inactivitat permès abans de finalitzar
 * automàticament una ruta.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link RestController}.</p>
 * 
 * <p>Utilitza {@link ParametresSistemaLogic} per accedir a la lògica de negoci.</p>
 * @author reyes
 */
@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametresSistemaControllerAndroid {
    
    private static final Logger logger = LoggerFactory.getLogger(ParametresSistemaControllerAndroid.class);
    
    @Autowired
    ParametresSistemaLogic parLogic;
    
    // Retorna el valor actual del paràmetre "temps màxim d'aturada" en minuts.
    @GetMapping("/temps-maxim-aturada")
    public ResponseEntity<Integer> obtenirTempsMaximAturada() {
        int temps = parLogic.obtenirTempsMaximAturat();
        logger.info("⏱️ Temps màxim d'aturada obtingut: {} minuts", temps);
        return ResponseEntity.ok(temps);
    }
}
