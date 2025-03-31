/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @author reyes
 */
@Data
public class ResetPasswordRequest {
    
    @Email(message = "El correu ha de ser vàlid")
    @NotBlank(message = "El correu no pot estar buit")
    private String email;

    @NotBlank(message = "El codi no pot estar buit")
    private String codi;

    @NotBlank(message = "La nova contrasenya no pot estar buida")
    private String novaContrasenya;
}
