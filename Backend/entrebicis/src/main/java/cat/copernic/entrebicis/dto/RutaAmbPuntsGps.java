/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author reyes
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaAmbPuntsGps {
    private double distanciaTotal;
    private String tempsTotal; 
    private double velocitatMitjana;
    private double velocitatMaxima;
    private List<PuntGpsDTO> punts;
    
}
