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
 *
 * @author reyes
 */
@Controller
@RequestMapping("/admin/parametres")
public class ParametresSistemaController {
    
    private static final Logger logger = LoggerFactory.getLogger(ParametresSistemaController.class);
    
    @Autowired
    ParametresSistemaLogic logic;
    
    @GetMapping
    public String mostrarParametres(Model model){
        model.addAttribute("parametres", logic.obtenirParametres());
        logger.info("🛠️ Paràmetres del sistema carregats correctament.");
        return "parametres-sistema";
    }
    
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
