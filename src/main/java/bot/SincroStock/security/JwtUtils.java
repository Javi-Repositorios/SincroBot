package bot.SincroStock.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import bot.SincroStock.modelos.Rol;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * @author Javier Martinez Sodric
 * <h1>Utilidad para JSON Web Token (JWT)</h1>
 * <p>
 * Esta clase es un componente gestionado por Spring que proporciona métodos 
 * para generar, parsear y validar tokens JWT.
 * Al ser un componente, permite la inyección de la clave secreta y el tiempo de expiración desde el archivo 
 * de propiedades de la aplicación.
 * </p>
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    public String generarToken(String username, Set<Rol> roles) {
        String rolesString = roles.stream()
                                  .map(Rol::getRol)
                                  .collect(Collectors.joining(","));

        // Nueva sintaxis: Metodos directos sin prefijo "set" y claims añadidos agrupados
        return Jwts.builder()
                .subject(username)
                .claim("roles", rolesString)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(obtenerKey())
                .compact();
    }

    private SecretKey obtenerKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        // Nueva sintaxis: parser() reemplaza a parserBuilder(), verifyWith() reemplaza a setSigningKey()
        // getPayload() reemplaza a getBody()
        return Jwts.parser()
                .verifyWith(obtenerKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String validarYObtenerRol(String token) {   	
        try {
            return Jwts.parser()
                    .verifyWith(obtenerKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("roles", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validarToken(String token) {
        try {
            // parseSignedClaims lanzara excepcion si el token esta expirado o alterado
            Jwts.parser()
                .verifyWith(obtenerKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}