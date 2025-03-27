/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author reyes
 */

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
