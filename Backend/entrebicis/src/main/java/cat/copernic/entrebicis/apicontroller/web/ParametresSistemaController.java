/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.entities.ParametresSistema;
import cat.copernic.entrebicis.logic.ParametresSistemaLogic;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web encarregat de gestionar la visualització i actualització
 * dels paràmetres del sistema per a l'administració de l'aplicació Entrebicis.
 * 
 * <p>Permet mostrar els paràmetres actuals i actualitzar-los mitjançant un formulari.</p>
 * 
 * <p>Utilitza {@link ParametresSistemaLogic} per accedir i guardar els valors dels paràmetres.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * 
 * <p>Registra l'activitat d'accés i actualització mitjançant {@link Logger}.</p>
 * 
 * @author reyes
 */
@Controller
@RequestMapping("/admin/parametres")
public class ParametresSistemaController {
    
    private static final Logger logger = LoggerFactory.getLogger(ParametresSistemaController.class);
    
    @Autowired
    ParametresSistemaLogic logic;
    
    // Carrega i mostra la pàgina amb els paràmetres actuals del sistema.
    @GetMapping
    public String mostrarParametres(Model model){
        model.addAttribute("parametres", logic.obtenirParametres());
        logger.info("🛠️ Paràmetres del sistema carregats correctament.");
        return "parametres-sistema";
    }
    
    // Guarda els paràmetres modificats si no hi ha errors de validació i redirigeix 
    // a la vista de paràmetres amb un missatge de confirmació.
    @PostMapping
    public String guardarParametres(@Valid @ModelAttribute("parametres") ParametresSistema parametres,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttrs){
        if(result.hasErrors()){
            logger.warn("⚠️ Errors de validació en guardar paràmetres del sistema.");
            return "parametres-sistema";
        }
        logic.guardarParametres(parametres);
        logger.info("✅ Paràmetres del sistema guardats correctament.");
        redirectAttrs.addFlashAttribute("missatge", "Paràmetres guardats correctament");
        return "redirect:/admin/parametres";
    }
    
}
