package bot.SincroStock.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import bot.SincroStock.security.JwtFilter;

/**
 * @author Javier Martinez Sodric
 * La clase SecurityConfig almacena el Bean de la cadena de filtros, donde se especifican los parámetros de csrf, cors, tipo de sesiones, y 
 * la clasificación de rutas de la API con autorizaciones, además, incrusta el Filtro JWTFilter en la cadena de filtros.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

   
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
    	// Configuración de CORS
        var corsConfig = new org.springframework.web.cors.CorsConfiguration();
        corsConfig.setAllowedOrigins(java.util.List.of("*"));
        corsConfig.setAllowedMethods(java.util.List.of("*"));
        corsConfig.setAllowedHeaders(java.util.List.of("*"));

        // Spring Security
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(cors -> cors.configurationSource(request -> corsConfig)) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        System.out.println(" APLICASTE FILTRO");
    
        return http.build();
    }
}