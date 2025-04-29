/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.dto.RutaAmbPuntsGps;
import cat.copernic.entrebicis.logic.ParametresSistemaLogic;
import cat.copernic.entrebicis.logic.RutaLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(RutaController.class);
    
    @Autowired
    RutaLogic rutaLogic;
    
    @Autowired
    ParametresSistemaLogic parametresLogic;
    
    @GetMapping
    public String llistarRutes(Model model){
        model.addAttribute("llistaRutes", rutaLogic.getAllRutes());
        model.addAttribute("velocitatMaxima", parametresLogic.obtenirVelocitatMaxima());
        logger.info("📄 Llista de rutes carregada correctament.");
        return "llista-rutes";
    }
   
    @GetMapping("/{id}")
    public String veureDetallsRuta(@PathVariable Long id,RedirectAttributes redirectAttrs, Model model){
        try{
            RutaAmbPuntsGps detall = rutaLogic.getDetallRutaAmbPunts(id);
            model.addAttribute("detall", detall);
            
            logger.info("🔍 Detalls de la ruta carregats per a ruta ID: {}", id);

            return "detall-ruta";
        }catch (IllegalStateException e) {
            logger.warn("⚠️ Error en obtenir detalls de la ruta ID {}: {}", id, e.getMessage());
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            logger.error("❌ Ruta no trobada o error inesperat per ruta ID: {}", id);
            redirectAttrs.addFlashAttribute("error", "Ruta no trobada.");
        }
        
        return "redirect:/admin/rutes";
    }
    
    @PostMapping("/validar/{id}")
    public String validarRuta(@PathVariable Long id, RedirectAttributes redirectAttrs){
        try{
            rutaLogic.validarRuta(id);
            logger.info("✅ Ruta ID {} validada correctament.", id);
            redirectAttrs.addFlashAttribute("missatgeSuccess", "Ruta validada correctament!");
        }catch(RuntimeException e){
            logger.warn("⚠️ Error al validar la ruta ID {}: {}", id, e.getMessage());
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }catch (Exception e) {
            logger.error("❌ Error inesperat en validar la ruta ID {}: {}", id, e.getMessage());
            redirectAttrs.addFlashAttribute("error", "No s'ha pogut validar la ruta: " + e.getMessage());
        }
        
        return "redirect:/admin/rutes";
    }
    
    @PostMapping("/invalidar/{id}")
    public String invalidarRuta(@PathVariable Long id, RedirectAttributes redirectAttrs){
        try{
            rutaLogic.invalidarRuta(id);
            logger.info("🚫 Ruta ID {} invalidada correctament.", id);
            redirectAttrs.addFlashAttribute("missatgeSuccess", "Ruta invalidada correctament!");
        }catch(RuntimeException e){
            logger.warn("⚠️ Error al invalidar la ruta ID {}: {}", id, e.getMessage());
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }catch (Exception e) {
            logger.error("❌ Error inesperat en invalidar la ruta ID {}: {}", id, e.getMessage());
            redirectAttrs.addFlashAttribute("error", "No s'ha pogut validar la ruta: " + e.getMessage());
        }
        
        return "redirect:/admin/rutes";
    }
}
