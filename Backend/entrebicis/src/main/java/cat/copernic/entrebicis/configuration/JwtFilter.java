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
 * Filtre de seguretat que intercepta totes les peticions HTTP per validar
 * el token JWT abans de permetre l'accés als recursos protegits de l'aplicació Entrebicis.
 * 
 * <p>Comprova la presència del token a l'encapçalament Authorization,
 * valida el token i, si és correcte, autentica l'usuari al {@link SecurityContextHolder}.</p>
 * 
 * <p>Detectat automàticament per Spring Boot gràcies a {@link Component}.</p>
 * 
 * <p>Extén {@link OncePerRequestFilter} per assegurar-se que només s'aplica un cop per petició.</p>
 * 
 * @author reyes
 */
@Component
public class JwtFilter extends OncePerRequestFilter{
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * Filtra totes les peticions entrants per validar el token JWT.
     * 
     * <p>Si el token és vàlid, configura l'autenticació de l'usuari en el context de seguretat;
     * en cas contrari, retorna un error 401 (Unauthorized).</p>
     *
     * @param request l'objecte {@link HttpServletRequest} de la petició HTTP.
     * @param response l'objecte {@link HttpServletResponse} de la resposta HTTP.
     * @param filterChain la cadena de filtres de Spring Security.
     * @throws ServletException si hi ha un error en el procés de filtratge.
     * @throws IOException si hi ha un error d'entrada/sortida durant el filtratge.
     */
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
