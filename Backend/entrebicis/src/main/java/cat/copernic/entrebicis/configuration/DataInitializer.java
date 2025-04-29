/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import cat.copernic.entrebicis.logic.UsuariLogic;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe encarregada d'inicialitzar dades bàsiques a l'arrencada de l'aplicació Entrebicis.
 * 
 * <p>Concretament, crea l'usuari administrador per defecte si no existeix.</p>
 * 
 * <p>Detectada automàticament per Spring Boot gràcies a {@link Configuration}.</p>
 * 
 * @author reyes
 */
@Configuration
public class DataInitializer {
    
    /**
     * Inicialitza l'usuari administrador a la base de dades si encara no existeix.
     * 
     * <p>S'executa automàticament en arrencar l'aplicació gràcies a {@link CommandLineRunner}.</p>
     *
     * @param usuariLogic instància de {@link UsuariLogic} utilitzada per crear l'administrador.
     * @return una instància de {@link CommandLineRunner} que inicialitza l'admin.
     */
    @Bean
    public CommandLineRunner initAdmin(UsuariLogic usuariLogic){
        return args -> usuariLogic.crearUsuariAdmin();
    }
}
