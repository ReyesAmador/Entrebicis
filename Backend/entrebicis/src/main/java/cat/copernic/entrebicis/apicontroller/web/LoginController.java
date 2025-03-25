/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.web;

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
                        @RequestParam(value = "denied", required = false) String denied,
                        Model model){
        
        if (error != null) {
            model.addAttribute("errorMessage", "Usuari o contrasenya incorrectes.");
        }
        if (denied != null) {
            model.addAttribute("errorMessage", "No tens permisos per accedir.");
        }
        
        return "login";
    }
}
