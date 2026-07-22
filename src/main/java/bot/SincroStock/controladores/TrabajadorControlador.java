package bot.SincroStock.controladores;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bot.SincroStock.modelos.Trabajador;
import bot.SincroStock.servicios.ServicioLogin;
import bot.SincroStock.servicios.ServicioPedidos;
import bot.SincroStock.servicios.ServicioRoles;
import bot.SincroStock.servicios.ServicioTrabajadores;


/**
 * @author Javier Martinez Sodric
 * Desde este controlador podremos crear, editar y listar borrar trabajadores. 
 * Obtener el mejor trabajador usando el servicio de pedidos.
 * Validacion usando el servicio de login
 * Todas los retornos de la api son un ResponseEntity.
 */
@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorControlador {

    @Autowired private ServicioTrabajadores trabajadorServicio;
    @Autowired private ServicioRoles rolServicio;
    @Autowired private ServicioLogin loginService;
    @Autowired private ServicioPedidos pedidosServicio;


     
    //LISTAR TRABAJADORES
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(trabajadorServicio.listarTodos());
    }
    
    //LISTAR ROLES
    @GetMapping("/roles-disponibles")
    public ResponseEntity<?> obtenerRoles() {
        return ResponseEntity.ok(rolServicio.listarTodos());
    }
    
    //MEJOR VENDEDOR
    @GetMapping("/mejor-vendedor")
    public ResponseEntity<Trabajador> getVendedorTop() {
        return ResponseEntity.ok(pedidosServicio.obtenerVendedorMasExitoso());
    }

    //EDITAR TRABAJADOR
    @PutMapping("/{username}")
    public ResponseEntity<?> actualizar(@PathVariable("username") String username, @RequestBody Trabajador datos) {
        return trabajadorServicio.actualizar(username, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    //GUARDAR TRABAJADOR
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Trabajador t) {
        return ResponseEntity.ok(trabajadorServicio.guardar(t));
    }
    
    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Trabajador datos)
    {
        Map<String, String> resultado = loginService.autenticar(datos);
        return resultado != null ? ResponseEntity.ok(resultado) : ResponseEntity.status(401).body("Error");
    }
    
    //BORRAR TRABAJADOR ( POR PK username )
    @DeleteMapping("/{username}")
    public ResponseEntity<?> borrar(@PathVariable("username") String username)
    {
        trabajadorServicio.borrar(username);
        return ResponseEntity.ok().build();
    }

    
}