/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Classe utilitària encarregada de donar suport a la conversió i format
 * de dates i hores a l'aplicació Entrebicis.
 * 
 * <p>Proporciona un formatador estàndard per mostrar dates i hores en format "dd/MM/yyyy HH:mm:ss".</p>
 * 
 * <p>Detectada automàticament per Spring Boot gràcies a {@link Component},
 * registrada amb el nom "temporals" per a ús en plantilles Thymeleaf.</p>
 * 
 * @author reyes
 */
@Component("temporals")
public class Temporals {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Dona format a un objecte {@link LocalDateTime} seguint el patró "dd/MM/yyyy HH:mm:ss".
     * 
     * <p>Si la data proporcionada és {@code null}, retorna una cadena buida.</p>
     *
     * @param dateTime l'objecte {@link LocalDateTime} a formatar.
     * @return la representació en format de la data, o una cadena buida si és {@code null}.
     */
    public String format(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(formatter);
    }   
}
