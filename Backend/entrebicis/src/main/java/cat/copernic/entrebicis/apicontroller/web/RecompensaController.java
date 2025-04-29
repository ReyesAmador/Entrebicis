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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(RecompensaController.class);
    
    @Autowired
    RecompensaLogic recompensaLogic;
    
    @GetMapping
    public String llistarRecompenses(Model model){
        model.addAttribute("llistaRecompenses", recompensaLogic.obtenirTotes());
        
        logger.info("📜 Llista de recompenses carregada correctament.");
        
        return "llista-recompenses";
    }
    
    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model){
        model.addAttribute("recompensa", new Recompensa());    
        logger.info("📝 Formulari per crear recompensa mostrat.");
        return "formulari-crear-recompensa";
    }
    
    @PostMapping("/crear")
    public String crearRecompensa(@Valid @ModelAttribute("recompensa") Recompensa recompensa,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAtt){
        if(result.hasErrors()){
            logger.warn("⚠️ Errors de validació en crear recompensa.");
            return "formulari-crear-recompensa";
        }
        
        recompensaLogic.crearRecompensa(recompensa);
        
        logger.info("✅ Recompensa '{}' creada correctament.", recompensa.getDescripcio());
        
        //Missatge flash
        redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa creada correctament");
        
        return "redirect:/admin/recompenses";
    }
    
    @PostMapping("/delete/{id}")
    public String eliminarRecompensa(@PathVariable Long id, RedirectAttributes redirectAtt){
        try{
            recompensaLogic.eliminarRecompensa(id);
            logger.info("🗑️ Recompensa ID {} eliminada correctament.", id);
            redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa eliminada correctament");
        }catch(IllegalStateException e){
            logger.warn("⚠️ Error en eliminar recompensa ID {}: {}", id, e.getMessage());
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }catch(NotFoundException e){
            logger.error("❌ Recompensa no trobada per eliminar ID {}.", id);
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/recompenses";
    }
    
    @PostMapping("/assignar/{id}")
    public String assignarRecompensa(@PathVariable Long id, RedirectAttributes redirectAtt){
        try{
            recompensaLogic.assignarRecompensa(id);
            logger.info("🎯 Recompensa ID {} assignada correctament.", id);
            redirectAtt.addFlashAttribute("missatgeSuccess", "Recompensa assignada correctament.");
        }catch(NotFoundException | SaldoInsuficientException | RecompensaReservadaException e){
            logger.warn("⚠️ Error en assignar recompensa ID {}: {}", id, e.getMessage());
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/recompenses";
    }
    
    @GetMapping("/{id}")
    public String mostrarRecompensa(@PathVariable Long id, Model model, RedirectAttributes redirectAtt){
        try{
            RecompensaDetallDTO dto = RecompensaDetallDTO.from(recompensaLogic.getRecompensa(id));
            model.addAttribute("recompensa", dto);
            
            logger.info("🔍 Detall de la recompensa ID {} carregat correctament.", id);

            return "detall-recompensa";
        }catch(NotFoundException e){
            logger.error("❌ Recompensa no trobada per mostrar ID {}.", id);
            redirectAtt.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/recompenses";
    }
}
