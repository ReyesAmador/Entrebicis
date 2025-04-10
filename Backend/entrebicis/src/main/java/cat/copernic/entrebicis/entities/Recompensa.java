/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import cat.copernic.entrebicis.enums.EstatRecompensa;
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
    
    @Column(length = 2000)
    private String descripcio;
    
    @Column(length = 2000)
    private String observacions;
    
    private double valor;
    
    @Enumerated(EnumType.STRING)
    private EstatRecompensa estat;
    
    @Column(name = "punt_recollida")
    private String puntRecollida;
    
    private String direccio;
    
    @Column(name = "data_assignacio")
    private LocalDate dataAssignacio;
    
    @ManyToOne
    @JoinColumn(name = "usuari_email")
    private Usuari usuari;
    
}
