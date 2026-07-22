package bot.SincroStock.servicios;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Rol;
import bot.SincroStock.modelos.Trabajador;
import bot.SincroStock.repositorios.TrabajadorRepositorio;
import bot.SincroStock.security.JwtUtils;



/**
 * @author Javier Martinez Sodric
 * El servicio de login se encarga de autenticar los datos de registro, extrayendo el nombre del trabajador mandando por parametro, y bucandolo en la base de datos,
 * Después utiliza el encoder para cotejar las contraseñas y si son correctas, crea y devuelve un Map del token, username y roles.
 */
@Service
public class ServicioLogin {
    @Autowired
    private TrabajadorRepositorio repositorio;

    @Autowired
    private BCryptPasswordEncoder encoder; // El encriptador
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public Map<String, String> autenticar(Trabajador datos) {
        System.out.println(">>> 1. AUTENTICANDO");
        System.out.println(">>> Username recibido: [" + datos.getUsername() + "]");
        System.out.println(">>> Password recibida: [" + datos.getPassword() + "]");

        Map<String, String> respuesta = null;

        Optional<Trabajador> trabajadorEncontrado = repositorio.findById(datos.getUsername());

        System.out.println(">>> 2. ¿Usuario existe en BBDD?: " + trabajadorEncontrado.isPresent());

        if (trabajadorEncontrado.isPresent()) {
            Trabajador trabajador = trabajadorEncontrado.get();
            System.out.println(">>> Hash en BBDD: [" + trabajador.getPassword() + "]");

            boolean coincidencia = encoder.matches(datos.getPassword(), trabajador.getPassword());
            System.out.println(">>> 3. ¿Coincide el password?: " + coincidencia);

            if (coincidencia) {
                String rolesString = trabajador.getRoles().stream()
                                               .map(Rol::getRol)
                                               .collect(Collectors.joining(","));

                respuesta = new HashMap<>();
                
                System.out.println(">>> 4. Generando token...");
                respuesta.put("token", jwtUtils.generarToken(trabajador.getUsername(), trabajador.getRoles()));
                respuesta.put("roles", rolesString);
                respuesta.put("username", trabajador.getUsername());
                System.out.println(">>> 5. Login correcto y mapa generado.");
            } else {
                System.out.println(">>> FALLO: El password no coincide.");
            }
        } else {
            System.out.println(">>> FALLO: No se encontró el usuario en la BBDD.");
        }

        return respuesta;
    }
    
    
    public String encriptar(String clave)
    {
    	return encoder.encode(clave);
    }

    // --- REGISTRO ---
    public Trabajador guardarEmpleado(Trabajador nuevoTrabajador)
    {
        // Encriptar la clave simple del form
        String claveEncriptada = encoder.encode(nuevoTrabajador.getPassword());
        
        // Sustituir la simple por la encriptada
        nuevoTrabajador.setPassword(claveEncriptada);
        
        // Save
        return repositorio.save(nuevoTrabajador);
    }
}