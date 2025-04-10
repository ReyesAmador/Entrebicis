/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.logic.RecompensaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/admin/recompenses")
public class RecompensaController {
    
    @Autowired
    RecompensaLogic recompensaLogic;
    
    @GetMapping
    public String llistarRecompenses(Model model){
        model.addAttribute("llistaRecompenses", recompensaLogic.obtenirTotes());
        
        return "llista-recompenses";
    }
}
