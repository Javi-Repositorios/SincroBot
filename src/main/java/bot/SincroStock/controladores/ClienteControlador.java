package bot.SincroStock.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bot.SincroStock.modelos.Cliente;
import bot.SincroStock.servicios.ServicioClientes;



/**@author Javier Martinez Sodric
 * Controlador de la entidad Cliente. Contiene las operaciones CRUD.
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteControlador {

	@Autowired private ServicioClientes servicioClientes;



	//LISTAR
	@GetMapping
	public ResponseEntity<?> listar()
	{
		return ResponseEntity.ok(servicioClientes.listarTodos());
	}

	//NUEVO	
	@PostMapping
	public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {

		ResponseEntity<?> respuesta;

		try
		{
			Cliente guardado = servicioClientes.guardar(cliente);
			respuesta = ResponseEntity.status(201).body(guardado);
		} 
		catch (IllegalArgumentException e) 
		{
			respuesta = ResponseEntity.status(422).body("Datos inválidos.");
		} 
		catch (DataIntegrityViolationException e)
		{
			respuesta = ResponseEntity.status(409).body("El email o NIF ya están registrados.");
		} 
		catch (Exception e) 
		{
			respuesta = ResponseEntity.status(500).body("Error interno.");
		}

		return respuesta; 
	}

	//BORRAR POR ID 
	@DeleteMapping("/{nif}")
	public ResponseEntity<?> borrar(@PathVariable("nif") String nif_cliente)
	{
		servicioClientes.borrar(nif_cliente);
		return ResponseEntity.ok().build();
	}


	//ACTUALIZAR: APARTE DE PASAR EL ID POR VARIABLE EN LA RUTA FRONTEND;
	//				TIENE QUE PASAR EL OBJETO A GUARDAR EN EL BODY
	@PutMapping("/{nif}") 
	public ResponseEntity<?> actualizarCliente(@PathVariable("nif") String nif_cliente, @RequestBody Cliente cliente) {
		// Es importante asegurar que el ID de la URL se asigne al objeto
		cliente.setNif(nif_cliente); 

		return servicioClientes.actualizar(nif_cliente, cliente)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}

