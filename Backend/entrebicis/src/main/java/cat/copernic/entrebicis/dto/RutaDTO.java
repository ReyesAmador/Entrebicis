/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import cat.copernic.entrebicis.entities.Ruta;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author reyes
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {
    
    private Long id;
    private LocalDateTime inici;
    private boolean estat;
    private boolean validada;
    private String nomUsuari;
    
    public RutaDTO(Ruta ruta){
        this.id = ruta.getId();
        this.inici = ruta.getInici();
        this.estat = ruta.isEstat();
        this.validada = ruta.isValidada();
        this.nomUsuari = ruta.getUsuari().getNom();
    }
    
}
