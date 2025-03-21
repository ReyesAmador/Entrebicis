/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import cat.copernic.entrebicis.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author reyes
 */
@Entity
@Table(name = "usuari")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuari {
    
    @Id
    @Email(message = "El correu ha de ser vàlid")
    private String email;
    //aquesta es la password
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{4,}$",
        message = "La contrasenya ha de tenir almenys 4 caràcters, una minúscula, una majúscula i un símbol.")
    @NotBlank
    @Column(nullable = false)
    private String paraula;
    private String nom;
    @Column(nullable = false)
    private Rol rol;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private Byte[] imatge;
    private double saldo;
    private String observacions;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "El número de mòbil ha de ser vàlid i contenir entre 9 y 15 digits")
    @Column(unique = true)
    private String mobil;
    private String poblacio;
    
}
