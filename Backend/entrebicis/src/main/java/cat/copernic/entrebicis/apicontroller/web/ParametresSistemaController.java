/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.entities.ParametresSistema;
import cat.copernic.entrebicis.logic.ParametresSistemaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    
    @Autowired
    ParametresSistemaLogic logic;
    
    @GetMapping
    public String mostrarParametres(Model model){
        model.addAttribute("parametres", logic.obtenirParametres());
        return "parametres-sistema";
    }
    
    @PostMapping
    public String guardarParametres(@ModelAttribute ParametresSistema parametres,
                                    RedirectAttributes redirectAttrs){
        logic.guardarParametres(parametres);
        redirectAttrs.addFlashAttribute("missatge", "Paràmetres guardats correctament");
        return "redirect:/admin/parametres";
    }
    
}
