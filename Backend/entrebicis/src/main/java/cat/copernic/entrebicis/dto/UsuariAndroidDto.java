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
public class UsuariAndroidDto {
    private String email;
    private String nom;
    private String rol;
    private double saldo;
    private String observacions;
    private String mobil;
    private String poblacio;
    private String imatgeBase64;
}
