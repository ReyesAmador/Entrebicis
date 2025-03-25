/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author reyes
 */
@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        HttpSession session,
                        Model model){
        
        if (error != null) {
            model.addAttribute("errorMessage", "Usuari o contrasenya incorrectes.");
        }
        
        Boolean denied = (Boolean) session.getAttribute("deniedMessage");
        
        if (denied != null && denied) {
            model.addAttribute("errorMessage", "No tens permisos per accedir.");
            session.removeAttribute("deniedMessage");
        }
        
        return "login";
    }
}
