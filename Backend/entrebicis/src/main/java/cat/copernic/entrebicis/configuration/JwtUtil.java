/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

/**
 * Classe utilitària encarregada de gestionar la creació i validació de tokens JWT
 * per a l'autenticació d'usuaris a l'aplicació Entrebicis.
 * 
 * <p>Proporciona funcionalitats per generar nous tokens, extreure informació
 * d'un token existent i validar la seva autenticitat.</p>
 * 
 * <p>Detectada automàticament per Spring Boot gràcies a {@link Component}.</p>
 * 
 * <p>Utilitza la clau secreta privada per signar i verificar els tokens.</p>
 * 
 * @author reyes
 */

@Component
public class JwtUtil {
    
    private final String SECRET_KEY = "entrebicis-secret-key-2025-strong-xx";
    
    /**
     * Genera un nou token JWT per a un usuari autenticat.
     * 
     * <p>El token inclou el nom d'usuari com a subjecte, la seva autoritat (rol),
     * la data d'emissió i una data d'expiració de 24 hores.</p>
     *
     * @param userDetails les dades de l'usuari per al qual es genera el token.
     * @return el token JWT generat com a {@link String}.
     */
    public String generateToken(UserDetails userDetails){
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("rol", userDetails.getAuthorities().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extreu el nom d'usuari (subject) d'un token JWT.
     *
     * @param token el token JWT del qual es vol extreure el nom d'usuari.
     * @return el nom d'usuari contingut dins del token.
     */
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    /**
     * Valida que el token JWT sigui vàlid i correspongui amb l'usuari especificat.
     * 
     * <p>Comprova que el nom d'usuari extret del token coincideixi amb el nom
     * d'usuari de les {@link UserDetails} proporcionades.</p>
     *
     * @param token el token JWT a validar.
     * @param userDetails les dades de l'usuari amb les quals validar el token.
     * @return {@code true} si el token és vàlid; {@code false} en cas contrari.
     */
    public boolean validarToken(String token, UserDetails userDetails){
        
        final String username = extractUsername(token);
        
        return username.equals(userDetails.getUsername());
    }
    
}
