/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web encarregat de gestionar el tancament de sessió (logout)
 * per a la part d'administració de l'aplicació Entrebicis.
 * 
 * <p>Invalidar la sessió activa, esborra la cookie de sessió i redirigeix
 * l'usuari cap a la pàgina de login mostrant un missatge de tancament correcte.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * 
 * <p>Utilitza {@link Logger} per registrar els esdeveniments relacionats amb el tancament de sessió.</p>
 * 
 * @author reyes
 */
@Controller
public class LogoutController {
    
    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);
    
    // Gestiona la invalidació de la sessió, elimina la cookie de sessió i redirigeix a la pàgina de login amb un missatge d'èxit.
    @PostMapping("/custom-logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
        //invalidar sessió
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
            logger.info("✅ Sessió invalidada correctament.");
        }else{
            logger.warn("⚠️ S'ha intentat tancar sessió sense una sessió activa.");
        }
        
        //borrar cookies
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.info("🍪 Cookie de sessió eliminada.");
        
        //afegir missatge
        redirectAttributes.addFlashAttribute("logoutSuccess", "S'ha tancat la sessió correctament.");
        
        logger.info("🔵 Redirigint a la pàgina de login després del logout.");
        
        return "redirect:/login";
    }
}
