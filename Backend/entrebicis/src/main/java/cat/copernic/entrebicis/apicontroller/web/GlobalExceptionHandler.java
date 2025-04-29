/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Classe que gestiona de manera global les excepcions que es produeixen
 * a la part web (admin) de l'aplicació Entrebicis.
 * 
 * <p>Actualment captura errors de peticions amb mètodes HTTP no suportats
 * i redirigeix l'usuari a una pàgina adequada mostrant un missatge d'error.</p>
 * 
 * <p>És detectada automàticament per Spring Boot gràcies a {@link ControllerAdvice}.</p>
 * 
 * <p>Utilitza {@link Logger} per registrar advertències sobre intents incorrectes d'accés.</p>
 * 
 * @author reyes
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // Gestiona errors quan un mètode HTTP no és suportat, redirigint a una pàgina segura i mostrant un missatge d'error.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupported(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        
        logger.warn("⚠️ Mètode HTTP no suportat en la URL: {}", request.getRequestURI());
        
        
        if (referer != null && referer.contains("/assignar/")) {
            return "redirect:/admin/recompenses";
        }
        redirectAttributes.addFlashAttribute("error", "No es pot accedir a aquesta acció directament per URL.");
        return "redirect:" + (referer != null ? referer : "/admin/recompenses");
    }
}
