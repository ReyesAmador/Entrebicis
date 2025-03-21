/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.exceptions.DuplicateException;
import cat.copernic.entrebicis.logic.UsuariLogic;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.net.http.HttpHeaders;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            BindingResult result, @RequestParam("imatgeFile") MultipartFile imatge,
            Model model, RedirectAttributes redirectAtt){
        
        //Si falla alguna validació automàtica
        if(result.hasErrors())
            return "formulari-crear-usuari";
        
        try{
            usuariLogic.crearUsuari(usuari,imatge);
        }catch(DuplicateException e){
            if(e.getMessage().toLowerCase().contains("correu"))
                result.rejectValue("email", "error.usuari", e.getMessage());
            if(e.getMessage().toLowerCase().contains("mòbil"))
                result.rejectValue("mobil", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(IllegalArgumentException e){
            if(e.getMessage().toLowerCase().contains("saldo"))
                result.rejectValue("saldo", "error.usuari", e.getMessage());
            return "formulari-crear-usuari";
        }catch(IOException e){
            result.rejectValue("imatge", "error.usuari", "Error en pujar la imatge.");
            return "formulari-crear-usuari";
        }
        
        //Missatge flash
        redirectAtt.addFlashAttribute("missatgeSuccess", "Usuari crear correctament");
        
        return "redirect:/admin/usuaris";
    }
    
    @GetMapping("/editar/{email}")
    public String mostrarFormulari(@PathVariable String email, Model model){
        Usuari usuari = usuariLogic.findByEmail(email).orElseThrow(
        ()-> new IllegalArgumentException ("Usuari no trobat amb email: " +email));
        
        model.addAttribute("usuari", usuari);
        
        return "formulari-modificar-usuari";
    }
    
    @PostMapping("/editar/{email}")
    public String processarModificarUsuari(@PathVariable String email,
            @ModelAttribute("usuari") Usuari usuariModificat,
            @RequestParam("imatgeFile") MultipartFile imatgeFile,
            RedirectAttributes redirectAtt){
        
        try{
            String error = usuariLogic.actualitzarUsuari(email, usuariModificat, imatgeFile);
        
            if (error != null) {
                redirectAtt.addFlashAttribute("error", error);
                return "redirect:/admin/usuaris/editar/" + email;
            }

            redirectAtt.addFlashAttribute("missatgeSuccess", "Usuari actualitzat correctament.");
            return "redirect:/admin/usuaris";
        }catch(IllegalArgumentException e){
            redirectAtt.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuaris";
        }
        
    }
    
    @GetMapping("/imatge/{email}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImatgeUsuari(@PathVariable String email) {
        Usuari usuari = usuariLogic.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuari no trobat: " + email));

        byte[] imatge = usuari.getImatge();
        if (imatge == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Detectar MIME
        MediaType mediaType;
        try {
            String mimeType = detectarMimeType(imatge);
            mediaType = (mimeType != null) ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (IOException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity
        .ok()
        .contentType(mediaType)
        .body(imatge);
    }
    
    private String detectarMimeType(byte[] imatge) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imatge)) {
            return URLConnection.guessContentTypeFromStream(bais);
        }
    }

}
