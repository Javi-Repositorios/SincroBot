package bot.SincroStock.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Cliente;
import bot.SincroStock.repositorios.ClienteRepositorio;

/**@author Javier Martinez Sodric
 * Servicio para acceder al repositorio de Clientes
 */
@Service
public class ServicioClientes {


	@Autowired 
	private ClienteRepositorio clienteRepositorio;	

	@Autowired
	private ServicioValidacion servicioValidacion;

	public List<Cliente> listarTodos()
	{
		return clienteRepositorio.findAll();
	}

	public Cliente guardar(Cliente cliente) 
	{
	    // 1. Lógica de negocio (NIF válido)
	    if (!servicioValidacion.esClienteValido(cliente)) 
	    {
	        throw new IllegalArgumentException("DATOS_INVALIDOS");
	    }

	    // 2. Comprobar si el @Id ya existe para evitar el UPDATE automático
	    if (clienteRepositorio.existsById(cliente.getNif())) 
	    {
	        // Lanzamos la excepción que el controlador captura como 409
	        throw new DataIntegrityViolationException("EL_CLIENTE_YA_EXISTE");
	    }

	    return clienteRepositorio.save(cliente);
	}

	public void borrar(String nif)
	{
		clienteRepositorio.deleteById(nif);
	}

	public Optional<Cliente> actualizar(String nif, Cliente clienteEditado) {
	   
		if (!servicioValidacion.esClienteValido(clienteEditado))
	    {
	        throw new IllegalArgumentException("DATOS_INVALIDOS");
	    }

	    Optional<Cliente> clienteExistente = clienteRepositorio.findById(nif);	//Se saca el cliente del optional y se usa 
	    Optional<Cliente> resultado = Optional.empty(); 						//Se mete en este y se devuelve.

	    if (clienteExistente.isPresent()) 
	    {
	        Cliente cliente = clienteExistente.get();
	        
	        // Modificar los datos
	        cliente.setNif(clienteEditado.getNif());
	        cliente.setEmail(clienteEditado.getEmail());
	        cliente.setNombre(clienteEditado.getNombre());
	        cliente.setTelefono(clienteEditado.getTelefono());
	        
	        // Guardarlo en el Optional de resultado
	        resultado = Optional.of(clienteRepositorio.save(cliente));
	    }

	    return resultado; 
	}

}
