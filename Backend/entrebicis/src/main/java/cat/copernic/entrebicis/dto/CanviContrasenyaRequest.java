/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

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
public class CanviContrasenyaRequest {
    
    private String actual;
    private String nova;
    private String repetirNova;
    
}
