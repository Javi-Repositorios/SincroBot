package bot.SincroStock.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Articulo;
import bot.SincroStock.modelos.Cliente;
import bot.SincroStock.modelos.LineasPedido;
import bot.SincroStock.modelos.Pedido;
import bot.SincroStock.repositorios.ArticuloRepositorio;
import bot.SincroStock.repositorios.ClienteRepositorio;
import bot.SincroStock.repositorios.TrabajadorRepositorio;


/**@author Javier Martinez Sodric
 * Servicio de validacion, lo usan el resto de servicios antes de insertar en la database.
 * Necesita los repositorios para insertar.
 */
@Service
public class ServicioValidacion {

	@Autowired
	TrabajadorRepositorio repoTrabajador;
	@Autowired
	ClienteRepositorio repoCliente;
	@Autowired
	ArticuloRepositorio repoArticulo;

	public boolean esArticuloValido(Articulo articulo)
	{
		boolean esValido = false;

		if (articulo.getPrecio() > 0 && articulo.getStock_disponible() > 0) {

			esValido = true;
		}

		return esValido;
	}


	public boolean esClienteValido(Cliente cliente) 
	{
		if (cliente == null) return false;

		boolean nifValido = cliente.getNif() != null && cliente.getNif().matches("^[0-9]{8}[A-Z]$");
		boolean nombreValido = cliente.getNombre() != null && !cliente.getNombre().trim().isEmpty() && cliente.getNombre().length() <= 100;
		boolean emailValido = cliente.getEmail() != null && cliente.getEmail().contains("@") && cliente.getEmail().length() <= 100;
		boolean telValido = cliente.getTelefono() != null && !cliente.getTelefono().trim().isEmpty() && cliente.getTelefono().length() <= 15;

		return nifValido && nombreValido && emailValido && telValido;
	}
	
	public boolean esPedidoValido(Pedido pedido) {

		boolean vendedorOk = false;
		boolean clienteOk = false;
		boolean tieneLineas = false;
		boolean stockOk = true;
		boolean totalResultado = false;

		try 
		{			// 1. Validar Vendedor
			if (pedido.getVendedor() != null && repoTrabajador.findByUsername(pedido.getVendedor().getUsername()).isPresent()) 
			{
				vendedorOk = true;
			} 				// 2. Validar Cliente
			if (pedido.getCliente() != null && repoCliente.existsById(pedido.getCliente().getNif())) 
			{
				clienteOk = true;
			} 				// 3. Validar Líneas
			if (pedido.getLineas() != null && !pedido.getLineas().isEmpty()) 
			{
				tieneLineas = true;
			}			// 4. Validar Stock
			if (tieneLineas) 
			{
				for (LineasPedido linea : pedido.getLineas())
				{	
					Articulo art = repoArticulo.findById(linea.getArticulo().getId_articulo()).orElse(null);
					//si el articulo es nulo o no hay cantidad
					if (art == null || art.getStock_disponible() < linea.getCantidad())
					{
						System.out.println("DEBUG: Fallo en artículo o stock insuficiente");
						stockOk = false;
						break;
					}
				}
			}
			// Resultado final (Lógica AND)
			totalResultado = (vendedorOk && clienteOk && tieneLineas && stockOk);
		} 
		catch (Exception e)
		{
			e.getMessage();
			e.printStackTrace(); 
		}
		return totalResultado; 
	}
}
