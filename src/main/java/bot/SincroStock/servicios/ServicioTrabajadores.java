package bot.SincroStock.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Rol;
import bot.SincroStock.modelos.Trabajador;
import bot.SincroStock.repositorios.RolRepositorio;
import bot.SincroStock.repositorios.TrabajadorRepositorio;


/**@author Javier Martinez Sodric
 * Servicio para la gestión  de trabajadores.
 */


@Service
public class ServicioTrabajadores {
	
	
		@Autowired private RolRepositorio rolRepositorio;	
	    @Autowired private TrabajadorRepositorio repositorio;
	    @Autowired private BCryptPasswordEncoder encoder;
	    
	    
	    //LEER 
	    public Optional<Trabajador> buscarPorUsername(String username) 
	    {
	        return repositorio.findById(username);
	    }

	    //LEER TODOS
	    public List<Trabajador> listarTodos()
	    {
	    	return repositorio.findAll(); 
	    }
	    	   
	    //GUARDAR
	    public Trabajador guardar(Trabajador trabajador) 
	    {
	        // 1. Contraseña
	        if (trabajador.getPassword() != null && !trabajador.getPassword().isEmpty())
	        {
	            trabajador.setPassword(encoder.encode(trabajador.getPassword()));
	        }

	        // 2. PROCESAR ROLES
	        if (trabajador.getRoles() != null && !trabajador.getRoles().isEmpty()) 
	        {
	            // Creamos una lista temporal con los nombres que vienen del JS
	            java.util.List<String> nombresRoles = trabajador.getRoles().stream()
	                                                   .map(Rol::getRol)
	                                                   .collect(java.util.stream.Collectors.toList());
	            
	            // Vaciamos los roles "sucios" que venían del JSON
	            trabajador.getRoles().clear(); 
	            
	            // Buscamos cada uno en la BD y lo añadimos al trabajador
	            for (String nombre : nombresRoles) 
	            {
	                Rol rolBD = rolRepositorio.findByRol(nombre).orElseThrow(() -> new RuntimeException("No existe el rol: " + nombre));
	                trabajador.getRoles().add(rolBD); // Aquí añadimos el objeto ROL a la lista de roles del trabajador
	            }
	        }

	        return repositorio.save(trabajador);
	    }
	    
	    // BORRAR
	    public void borrar(String username) 
	    { 
	    	repositorio.deleteById(username); 
	    }

	    //ACTUALIZAR
	    public Optional<Trabajador> actualizar(String username, Trabajador nuevosDatos) {
	        // 1. Creamos la variable que contendrá el resultado final
	        Optional<Trabajador> resultado = Optional.empty();

	        // 2. Buscamos al trabajador
	        Optional<Trabajador> existente = repositorio.findById(username);

	        if (existente.isPresent()) {
	            Trabajador trabajador = existente.get();

	            // Actualizamos datos básicos
	            trabajador.setNombre(nuevosDatos.getNombre());
	            trabajador.setApellidos(nuevosDatos.getApellidos());

	            // Password
	            if (nuevosDatos.getPassword() != null && !nuevosDatos.getPassword().isEmpty()) 
	            {
	                trabajador.setPassword(encoder.encode(nuevosDatos.getPassword()));
	            }

	            // Roles
	            if (nuevosDatos.getRoles() != null) 
	            {
	                trabajador.getRoles().clear();
	                for (Rol r : nuevosDatos.getRoles()) 
	                {
	                    Rol rolBD = rolRepositorio.findByRol(r.getRol())
	                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
	                    trabajador.getRoles().add(rolBD);
	                }
	            }
	            // Guardamos y envolvemos el resultado en el Optional
	            resultado = Optional.of(repositorio.save(trabajador));
	        }	        
	        return resultado;
	    }
	    
}
