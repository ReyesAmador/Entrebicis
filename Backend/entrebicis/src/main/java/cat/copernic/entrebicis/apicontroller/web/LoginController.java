/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador web encarregat de gestionar la pantalla d'inici de sessió (login)
 * per a la part d'administració de l'aplicació Entrebicis.
 * 
 * <p>Mostra el formulari de login i gestiona els missatges d'error
 * associats a intents fallits d'autenticació o accessos denegats.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * 
 * <p>Utilitza {@link Logger} per registrar intents fallits i accessos denegats.</p>
 * 
 * @author reyes
 */
@Controller
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    // Gestiona la visualització de la pàgina de login i mostra missatges d'error segons l'estat de l'autenticació.
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        HttpSession session,
                        Model model){
        
        if (error != null) {
            model.addAttribute("errorMessage", "Usuari o contrasenya incorrectes.");
            logger.warn("⚠️ Intent fallit d'inici de sessió (usuari o contrasenya incorrectes)");
        }
        
        Boolean denied = (Boolean) session.getAttribute("deniedMessage");
        
        if (denied != null && denied) {
            model.addAttribute("errorMessage", "No tens permisos per accedir.");
            session.removeAttribute("deniedMessage");
            logger.warn("⚠️ Accés denegat: usuari sense permisos");
        }
        
        logger.info("🔵 Mostrant pantalla de login");
        return "login";
    }
}
