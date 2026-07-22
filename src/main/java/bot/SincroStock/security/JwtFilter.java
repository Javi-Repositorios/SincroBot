package bot.SincroStock.security;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Javier Martinez Sodric
 * Al extender de OncePerRequestFilter se ejecutará en cada nueva petición, y obliga a definir el metodo
 * donde se gestiona la validez del token, para incluirlo en el SecurityContextHolder.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Omite la ejecución del filtro JWT para el endpoint de login
        String path = request.getRequestURI();
        return path.equals("/api/trabajadores/login");
    }
    
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        
        System.out.println(" APLICASTE FILTRO INTERNO:" + header);
        
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtUtils.validarToken(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);
                    String rolesString = jwtUtils.validarYObtenerRol(token);

                    if (rolesString != null && !rolesString.isEmpty()) {
                        // IMPORTANTE: Añadimos ROLE_ a cada rol extraído del token
                        List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(","))
                                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.trim()))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                        
                        SecurityContextHolder.getContext().setAuthentication(auth);

                    }
                }
            } 
            catch (Exception e) 
            {
                System.out.println("DEBUG FILTRO: Error en validación: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}