/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import cat.copernic.entrebicis.enums.Rol;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
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
    @Column(nullable = false)
    private String paraula;
    private String nom;
    @Column(nullable = false)
    private Rol rol;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imatge;
    private double saldo;
    @Size(max = 2000, message = "Les observacions no poden superar els 2000 caràcters.")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String observacions;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "El número de mòbil ha de ser vàlid i contenir entre 9 y 15 digits")
    @Column(unique = true)
    private String mobil;
    private String poblacio;
    
    @OneToMany(mappedBy = "usuari")
    private List<Ruta> rutes = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuari", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Recompensa> recompenses = new ArrayList<>();
}
