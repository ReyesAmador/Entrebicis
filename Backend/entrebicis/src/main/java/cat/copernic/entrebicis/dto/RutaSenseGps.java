/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import cat.copernic.entrebicis.entities.Usuari;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author reyes
 */

@Data
public class RutaSenseGps {
    private Usuari usuari;
    private Long id;
    
    private LocalDateTime inici;
    private String temps_total;
    
    private boolean estat;
    private boolean validada;
    private double km_total;
    private double velocitat_mitjana;
    private double velocitat_max;
    private double saldo;
    
}
