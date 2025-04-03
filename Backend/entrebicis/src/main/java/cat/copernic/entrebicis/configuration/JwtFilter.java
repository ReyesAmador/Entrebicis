/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author reyes
 */
@Component
public class JwtFilter extends OncePerRequestFilter{
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException{
        
        final String authHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        // Comprobar si el header Authorization contiene un token Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // elimina "Bearer "
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                System.out.println("Token invàlid o caducat: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        // Si hay usuario y no está autenticado aún
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try{
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validarToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }catch (Exception e) {
                System.out.println("❌ Error autenticant usuari des del token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 🔥 aquí bloqueamos el acceso
                return;
            }
            
        }
        
        filterChain.doFilter(request, response);
    }
    
}
