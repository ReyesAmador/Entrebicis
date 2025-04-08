/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.dto.RutaAmbPuntsGps;
import cat.copernic.entrebicis.logic.RutaLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/admin/rutes")
public class RutaController {
    
    @Autowired
    RutaLogic rutaLogic;
    
    @GetMapping
    public String llistarRutes(Model model){
        model.addAttribute("llistaRutes", rutaLogic.getAllRutes());
        return "llista-rutes";
    }
   
    @GetMapping("/{id}")
    public String veureDetallsRuta(@PathVariable Long id, Model model){
        RutaAmbPuntsGps detall = rutaLogic.getDetallRutaAmbPunts(id);
        model.addAttribute("detall", detall);
        
        return "detall-ruta";
    }
}
