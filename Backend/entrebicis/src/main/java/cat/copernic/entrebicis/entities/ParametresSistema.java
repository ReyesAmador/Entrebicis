/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author reyes
 */
@Entity
@Table(name = "parametres_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametresSistema {
    
    @Id
    private Long id = 1L; //tant sols hi haurà una única fila
    
    @Column(name = "velocitat_maxima", nullable = false)
    private int velocitatMaxima;
    @Column(name = "temps_max_aturat", nullable = false)
    private int tempsMaximAturat;
    @Column(nullable = false)
    private double conversio;
    @Column(name = "temps_max_recollida", nullable = false)
    private int tempsMaximRecollida;
    
}
