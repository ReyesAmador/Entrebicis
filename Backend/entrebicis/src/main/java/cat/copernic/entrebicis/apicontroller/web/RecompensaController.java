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
 * Controlador web encarregat de gestionar la creació, visualització, assignació
 * i eliminació de recompenses a la part d'administració de l'aplicació Entrebicis.
 * 
 * <p>Permet llistar totes les recompenses, crear-ne de noves, eliminar-les,
 * assignar-les als usuaris i visualitzar el seu detall.</p>
 * 
 * <p>Utilitza {@link RecompensaLogic} per delegar la lògica de negoci
 * associada a recompenses.</p>
 * 
 * <p>És detectat automàticament per Spring Boot gràcies a {@link Controller}.</p>
 * 
 * <p>Registra totes les operacions mitjançant {@link Logger} per a control
 * i traçabilitat dels processos.</p>
 * 
 * @author reyes
 */
@Controller
@RequestMapping("/admin/recompenses")
public class RecompensaController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecompensaController.class);
    
    @Autowired
    RecompensaLogic recompensaLogic;
    
    // Carrega i mostra la llista de totes les recompenses.
    @GetMapping
    public String llistarRecompenses(Model model){
        model.addAttribute("llistaRecompenses", recompensaLogic.obtenirTotes());
        
        logger.info("📜 Llista de recompenses carregada correctament.");
        
        return "llista-recompenses";
    }
    
    // Mostra el formulari per crear una nova recompensa.
    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model){
        model.addAttribute("recompensa", new Recompensa());    
        logger.info("📝 Formulari per crear recompensa mostrat.");
        return "formulari-crear-recompensa";
    }
    
    // Gestiona la creació d'una nova recompensa si la validació és correcta.
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
    
    // Elimina una recompensa existent pel seu ID.
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
    
    // Assigna una recompensa reservada a l'usuari que la va reservar.
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
    // Mostra el detall d'una recompensa específica.
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
