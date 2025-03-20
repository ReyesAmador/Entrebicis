/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis;

import cat.copernic.entrebicis.logic.UsuariLogic;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author reyes
 */
@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initAdmin(UsuariLogic usuariLogic){
        return args -> usuariLogic.crearUsuariAdmin();
    }
}
