/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.entities.Recompensa;
import cat.copernic.entrebicis.entities.Ruta;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.exceptions.CampBuitException;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.exceptions.NotFoundUsuariException;
import cat.copernic.entrebicis.logic.RecompensaLogic;
import cat.copernic.entrebicis.logic.RutaLogic;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author reyes
 */
@Controller
@RequestMapping("/admin/usuaris")
public class UsuariController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariController.class);
    
    @Autowired
    UsuariLogic usuariLogic;
    
    @Autowired
    RutaLogic rutaLogic;
    
    @Autowired
    RecompensaLogic recoLogic;
    
    @GetMapping
    public String llistarUsuaris(Model model){
        List<Usuari> usuaris = usuariLogic.obtenirTotsUsuaris();
        model.addAttribute("usuaris", usuaris);
        
        logger.info("📄 Llista d'usuaris carregada correctament. Total: {}", usuaris.size());
        
        return "usuaris";
    }
    
    @GetMapping("/formulari-crear")
    public String mostrarFormulari(Model model){
        model.addAttribute("usuari", new Usuari());
        
        logger.info("📝 Formulari de creació d'usuari mostrat.");
        
        return "formulari-crear-usuari";
    }
    
    @PostMapping("/crear")
    public String crearUsuari(@Valid @ModelAttribute("usuari") Usuari usuari, 
            BindingResult result, @RequestParam("imatgeFile") MultipartFile imatge,
            Model model, RedirectAttributes redirectAtt){
        
        //Si falla alguna validació automàtica
        if(result.hasErrors()){
            
            logger.warn("⚠️ Errors de validació en crear usuari.");
            return "formulari-crear-usuari";
        }
        
        try{
            usuariLogic.crearUsuari(usuari,imatge);
            logger.info("✅ Usuari {} creat correctament.", usuari.getEmail());
        }catch(DuplicateException e){
            logger.warn("⚠️ Duplicat detectat en crear usuari: {}", e.getMessage());
            if(e.getMessage().toLowerCase().contains("correu"))
                result.rejectValue("email", "error.usuari", e.getMessage());
            if(e.getMessage().toLowerCase().contains("mòbil"))
                result.rejectValue("mobil", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(CampBuitException e){
            logger.warn("⚠️ Error en camps obligatoris en crear usuari: {}", e.getMessage());
            if(e.getMessage().toLowerCase().contains("contrasenya"))
                result.rejectValue("paraula", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(IllegalArgumentException e){
            if(e.getMessage().toLowerCase().contains("saldo"))
                result.rejectValue("saldo", "error.usuari", e.getMessage());
            if(e.getMessage().toLowerCase().contains("contrasenya"))
                result.rejectValue("paraula", "error.usuari", e.getMessage());
            if(e.getMessage().toLowerCase().contains("imatge"))
                result.rejectValue("imatge", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(IOException e){
            logger.error("❌ Error al pujar la imatge de l'usuari.", e);
            result.rejectValue("imatge", "error.usuari", "Error en pujar la imatge.");
            return "formulari-crear-usuari";
        }
        
        //Missatge flash
        redirectAtt.addFlashAttribute("missatgeSuccess", "Usuari crear correctament");
        
        return "redirect:/admin/usuaris";
    }
    
    @GetMapping("/editar/{email}")
    public String mostrarFormulari(@PathVariable String email, Model model, RedirectAttributes redirectAtt){
        try{
            Usuari usuari = usuariLogic.findByEmail(email);
        
            model.addAttribute("usuari", usuari);
            model.addAttribute("lectura", false);
            logger.info("🔵 Formulari de modificació carregat per a usuari: {}", email);
        }catch(NotFoundUsuariException e){
            logger.error("❌ Usuari no trobat: {}", email);
            redirectAtt.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuaris";
        }
 
        return "formulari-modificar-usuari";
    }
    
    @GetMapping("visualitzar/{email}")
    public String visualitzarUsuari(@PathVariable String email, Model model, RedirectAttributes redirectAtt){
        try{
            Usuari usuari = usuariLogic.findByEmail(email);
        
            model.addAttribute("usuari", usuari);
            model.addAttribute("lectura", true);
            
            logger.info("🔍 Visualització de dades per a usuari: {}", email);
        }catch(NotFoundUsuariException e){
            logger.error("❌ Usuari no trobat: {}", email);
            redirectAtt.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuaris";
        }
 
        return "formulari-modificar-usuari";
    }
    
    @PostMapping("/editar/{email}")
    public String processarModificarUsuari(@PathVariable String email,
            @Valid @ModelAttribute("usuari") Usuari usuariModificat,
            BindingResult result,
            @RequestParam("imatgeFile") MultipartFile imatgeFile,
            Model model,
            RedirectAttributes redirectAtt){
        
        if(result.hasErrors()){
            logger.warn("⚠️ Errors de validació en modificar usuari.");
            model.addAttribute("lectura", false);
            return "formulari-modificar-usuari";
        }
        
        try{
            usuariLogic.actualitzarUsuari(email, usuariModificat, imatgeFile);
            logger.info("✅ Usuari {} actualitzat correctament.", email);
        }catch (DuplicateException e) {
            if (e.getMessage().toLowerCase().contains("mòbil"))
                result.rejectValue("mobil", "error.usuari", e.getMessage());
            model.addAttribute("lectura", false);
            return "formulari-modificar-usuari";
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Error en actualitzar usuari {}: {}", email, e.getMessage());
            if (e.getMessage().toLowerCase().contains("saldo"))
                result.rejectValue("saldo", "error.usuari", e.getMessage());
            if (e.getMessage().toLowerCase().contains("imatge"))
                result.rejectValue("imatge", "error.usuari", e.getMessage());
            model.addAttribute("lectura", false);
            return "formulari-modificar-usuari";
        } catch (IOException e) {
            logger.error("❌ Error al pujar imatge en modificar usuari.", e);
            result.rejectValue("imatge", "error.usuari", "Error en pujar la imatge.");
            model.addAttribute("lectura", false);
            return "formulari-modificar-usuari";
        }
        
        redirectAtt.addFlashAttribute("missatgeSuccess", "Usuari actualitzat correctament.");
        return "redirect:/admin/usuaris";
        
    }
    
    @GetMapping("/imatge/{email}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImatgeUsuari(@PathVariable String email) {
        try {
            Usuari usuari = usuariLogic.findByEmail(email);

            byte[] imatge = usuari.getImatge();
            if (imatge == null) {
                logger.warn("⚠️ L'usuari {} no té imatge.", email);
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType;
            try {
                String mimeType = detectarMimeType(imatge);
                mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
            } catch (IOException e) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            
            logger.info("🖼️ Imatge retornada per a usuari: {}", email);

            return ResponseEntity.ok().contentType(mediaType).body(imatge);

        } catch (NotFoundUsuariException e) {
            logger.error("❌ Usuari no trobat (imatge): {}", email);
            return ResponseEntity.notFound().build();
        }
    }
    
    private String detectarMimeType(byte[] imatge) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imatge)) {
            return URLConnection.guessContentTypeFromStream(bais);
        }
    }
    
    @GetMapping("/historial/rutes/{email}")
    public String historialRutes(@PathVariable String email, Model model) {
        List<Ruta> rutes = rutaLogic.obtenirRutaUsuari(email);
        model.addAttribute("rutes", rutes);
        logger.info("📜 Historial de rutes carregat per a usuari: {}", email);
        return "fragments/historial-rutes :: historialRutes";
    }

    @GetMapping("/historial/recompenses/{email}")
    public String historialRecompenses(@PathVariable String email, Model model) {
        List<Recompensa> recompenses = recoLogic.getRecompensesByUsuari(email);
        model.addAttribute("recompenses", recompenses);
        logger.info("🎁 Historial de recompenses carregat per a usuari: {}", email);
        return "fragments/historial-recompenses :: historialRecompenses";
    }

    
}
