/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/admin/usuaris")
public class UsuariController {
    
    @Autowired
    UsuariLogic usuariLogic;
    
    @GetMapping
    public String llistarUsuaris(Model model){
        List<Usuari> usuaris = usuariLogic.obtenirTotsUsuaris();
        model.addAttribute("usuaris", usuaris);
        
        return "usuaris";
    }
    
    @GetMapping("/formulari-crear")
    public String mostrarFormulari(Model model){
        model.addAttribute("usuari", new Usuari());
        
        return "formulari-crear-usuari";
    }
    
    @PostMapping("/crear")
    public String crearUsuari(@Valid @ModelAttribute("usuari") Usuari usuari, 
            BindingResult result, Model model, RedirectAttributes redirectAtt){
        
        //Si falla alguna validació automàtica
        if(result.hasErrors())
            return "formulari-crear-usuari";
        
        try{
            usuariLogic.crearUsuari(usuari);
        }catch(DuplicateException e){
            if(e.getMessage().contains("correu"))
                result.rejectValue("email", "error.usuari", e.getMessage());
            if(e.getMessage().contains("mobil"))
                result.rejectValue("mobil", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(IllegalArgumentException e){
            result.rejectValue("saldo", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }
        
        //Missatge flash
        redirectAtt.addFlashAttribute("missatgeSuccess", "Usuari crear correctament");
        
        return "redirect:/admin/usuaris";
    }
}
