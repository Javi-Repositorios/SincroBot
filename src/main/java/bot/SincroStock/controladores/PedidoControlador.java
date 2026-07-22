package bot.SincroStock.controladores;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bot.SincroStock.modelos.Pedido;
import bot.SincroStock.servicios.ServicioPedidos;


/**@author Javier Martinez Sodric
 * Controlador de pedidos. Operaciones: LISTAR Y CREAR.  FIltrar por vendedor.
 * Tamben se obtienen las lineas de pedido desde aqui. Ahorrando un controlador...
 * NO SE PUEDE BORRAR NI MODIFICAR EL PEDIDO
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin("*")
public class PedidoControlador {

	@Autowired
	private ServicioPedidos servicioPedidos;

	///LISTAR PEDIDOS
	@GetMapping
	public ResponseEntity<?> listarTodos() {
	    try 
	    {
	        List<Pedido> pedidos = servicioPedidos.listarPedidos();
	        return ResponseEntity.ok(pedidos);
	    } 
	    catch (Exception e) 
	    {
	        return ResponseEntity.status(500).body("Error al obtener estadísticas: " + e.getMessage());
	    }
	}
	
	//FILTRAR PEDIDOS POR VENDEDOR
	@GetMapping("/vendedor/{nombreVendedor}")
	public ResponseEntity<?> listarPorVendedor(@PathVariable String nombreVendedor) {
		
		//respuesta vacia
	    ResponseEntity<?> respuesta;
	    
	    try 
	    {	//Se intenta obtener la lista del pedidos del vendedor	
	        List<Pedido> pedidos = servicioPedidos.listarPorVendedor(nombreVendedor);
	        respuesta = ResponseEntity.ok(pedidos);   
	    }
	    catch (Exception e) 
	    {	
	        respuesta = ResponseEntity.status(500).body("Error al listar pedidos: " + e.getMessage());
	    }
	    
	    return respuesta;
	}

	///GUARDAR PEDIDO
	@PostMapping(value = "/guardar", consumes = "application/json")
	public ResponseEntity<?> guardarPedido(@RequestBody Pedido pedido) {

		ResponseEntity<?> respuesta;

		try 
		{
			Pedido resultado = servicioPedidos.guardarPedido(pedido);
			respuesta = ResponseEntity.ok(resultado);
		} 
		catch (IllegalArgumentException e) 
		{
			// Para errores de validación (400)
			respuesta = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} 
		catch (RuntimeException e) 
		{
			// Para errores de "No encontrado" o lógica de negocio (404 o 409 según prefieras, aquí 400 por simplificar)
			respuesta = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de negocio: " + e.getMessage());
		} 
		catch (Exception e) 
		{
			// Error genérico de servidor (500)
			respuesta = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
		}

		return respuesta;
	}

	///OBTENER LINEAS DE PEDIDO
	@GetMapping("/{id}/lineas")
	public ResponseEntity<?> obtenerLineasPedido(@PathVariable Long id) {

		ResponseEntity<?> respuesta;

		try 
		{
			respuesta= ResponseEntity.ok(servicioPedidos.obtenerLineasPorId(id));
		} 
		catch (Exception e) 
		{
			respuesta =  ResponseEntity.notFound().build();
		}

		return respuesta;
	}
}