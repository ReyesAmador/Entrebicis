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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String veureDetallsRuta(@PathVariable Long id,RedirectAttributes redirectAttrs, Model model){
        try{
            RutaAmbPuntsGps detall = rutaLogic.getDetallRutaAmbPunts(id);
            model.addAttribute("detall", detall);

            return "detall-ruta";
        }catch (IllegalStateException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Ruta no trobada.");
        }
        
        return "redirect:/admin/rutes";
    }
    
    @PostMapping("/validar/{id}")
    public String validarRuta(@PathVariable Long id, RedirectAttributes redirectAttrs){
        try{
            rutaLogic.validarRuta(id);
            redirectAttrs.addFlashAttribute("missatgeSuccess", "Ruta validada correctament!");
        }catch(RuntimeException e){
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No s'ha pogut validar la ruta: " + e.getMessage());
        }
        
        return "redirect:/admin/rutes";
    }
}
