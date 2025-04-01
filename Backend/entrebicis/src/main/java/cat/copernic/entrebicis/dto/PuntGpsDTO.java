/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import lombok.Data;

/**
 *
 * @author reyes
 */

@Data
public class PuntGpsDTO {
    
    private double latitud;
    private double longitud;
    private long temps;
}
