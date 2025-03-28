/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.apicontroller.android;

import cat.copernic.entrebicis.configuration.JwtUtil;
import cat.copernic.entrebicis.entities.LoginRequest;
import cat.copernic.entrebicis.entities.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author reyes
 */
@RestController
@RequestMapping("/api")
public class LoginControllerAndroid {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq){
        try{
            authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getParaula())
            );
        }catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credencials incorrectes");
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginReq.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
    
}
