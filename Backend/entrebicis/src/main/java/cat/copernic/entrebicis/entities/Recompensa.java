/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import cat.copernic.entrebicis.enums.EstatRecompensa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author reyes
 */
@Entity
@Table(name = "recompensa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 50)
    @NotBlank(message = "Aquest camp no pot estar buit ni contenir només espais")
    private String descripcio;
    
    @Column(length = 2000)
    private String observacions;
    
    @Min(value = 0, message = "El valor no pot ser negatiu")
    private double valor;
    
    @Enumerated(EnumType.STRING)
    private EstatRecompensa estat;
    
    @Column(name = "punt_recollida", length = 50)
    @NotBlank(message = "Aquest camp no pot estar buit ni contenir només espais")
    private String puntRecollida;
    
    @Column(length = 50)
    @NotBlank(message = "Aquest camp no pot estar buit ni contenir només espais")
    private String direccio;
    
    @Column(name = "data_assignacio")
    private LocalDate dataAssignacio;
    
    @ManyToOne
    @JoinColumn(name = "usuari_email")
    @JsonIgnoreProperties("recompenses") // ← evita bucle infinito al serializar
    private Usuari usuari;
    
}
