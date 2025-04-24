/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import cat.copernic.entrebicis.entities.Recompensa;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author reyes
 */

@Data
@AllArgsConstructor
public class RecompensaDetallDTO {
    
    private Long id;
    private String descripcio;
    private String dataCreacio;
    private String dataReserva;
    private String dataAssignacio;
    private String dataRecollida;
    private String nomUsuari;
    private String nomPunt;
    private String direccio;
    private String estat;
    
    public static RecompensaDetallDTO from(Recompensa r){
        return new RecompensaDetallDTO(
        r.getId(),
        r.getDescripcio(),
        format(r.getDataCreacio()),
        format(r.getDataReserva(), "Reserva no feta"),
        format(r.getDataAssignacio(), "Assignació no feta"),
        format(r.getDataRecollida(), "No recollida"),
        r.getUsuari() != null ? r.getUsuari().getNom() : "",
        r.getPuntRecollida(),
        r.getDireccio(),
        r.getEstat().name()
        );
    }
    
    private static String format(LocalDate data) {
        return data != null ? data.toString() : "";
    }

    private static String format(LocalDate data, String textDefecte) {
        return data != null ? data.toString() : textDefecte;
    }
}
