/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.dto.RecompensaDetallDTO;
import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.exceptions.NotFoundException;
import cat.copernic.entrebicis.exceptions.RecompensaReservadaException;
import cat.copernic.entrebicis.exceptions.SaldoInsuficientException;
import cat.copernic.entrebicis.logic.RecompensaLogic;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model){
        model.addAttribute("recompensa", new Recompensa());    
        return "formulari-crear-recompensa";
    }
    
    @PostMapping("/crear")
    public String crearRecompensa(@Valid @ModelAttribute("recompensa") Recompensa recompensa,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAtt){
        if(result.hasErrors()){
            return "formulari-crear-recompensa";
        }
        
        recompensaLogic.crearRecompensa(recompensa);
        
        //Missatge flash
        redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa creada correctament");
        
        return "redirect:/admin/recompenses";
    }
    
    @PostMapping("/delete/{id}")
    public String eliminarRecompensa(@PathVariable Long id, RedirectAttributes redirectAtt){
        try{
            recompensaLogic.eliminarRecompensa(id);
            redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa eliminada correctament");
        }catch(IllegalStateException e){
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }catch(NotFoundException e){
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/recompenses";
    }
    
    @PostMapping("/assignar/{id}")
    public String assignarRecompensa(@PathVariable Long id, RedirectAttributes redirectAtt){
        try{
            recompensaLogic.assignarRecompensa(id);
            redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa assignada correctament.");
        }catch(NotFoundException | SaldoInsuficientException | RecompensaReservadaException e){
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/recompenses";
    }
    
    @GetMapping("/{id}")
    public String mostrarRecompensa(@PathVariable Long id, Model model){
        RecompensaDetallDTO dto = RecompensaDetallDTO.from(recompensaLogic.getRecompensa(id));
        model.addAttribute("recompensa", dto);
        
        return "detall-recompensa";
    }
}
