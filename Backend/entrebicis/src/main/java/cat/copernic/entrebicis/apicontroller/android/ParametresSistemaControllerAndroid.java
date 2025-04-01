/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.logic.ParametresSistemaLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author reyes
 */
@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametresSistemaControllerAndroid {
    
    @Autowired
    ParametresSistemaLogic parLogic;
    
    @GetMapping("/temps-maxim-aturada")
    public ResponseEntity<Integer> obtenirTempsMaximAturada() {
        int temps = parLogic.obtenirTempsMaximAturat();
        return ResponseEntity.ok(temps);
    }
}
