package bot.SincroStock.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Articulo;
import bot.SincroStock.repositorios.ArticuloRepositorio;

/**@author Javier Martinez Sodric
 * Logica de negocio del modelo Articulo. Puente entre controlador y repositorio.
 */
@Service
public class ServicioArticulos {

	@Autowired 
	private ArticuloRepositorio articuloRepositorio;	
	
	@Autowired
	private ServicioValidacion servicioValidacion;
	
	
	//LEER 
    public Optional<Articulo> buscarPorId(int id) {
        return articuloRepositorio.findById(id);
    }

    //LEER TODOS
    public List<Articulo> listarTodos()
    {
    	return articuloRepositorio.findAll(); 
    }
    	   
    //GUARDAR
    public Articulo guardar(Articulo articulo) 
    {
        // Si no da valido
        if (!servicioValidacion.esArticuloValido(articulo)) 
        {
            throw new IllegalArgumentException("DATOS_INVALIDOS");
        }

        return articuloRepositorio.save(articulo);
    }
    
    // BORRAR
    public void borrar(int id) 
    { 
    	articuloRepositorio.deleteById(id); 
    }

  //ACTUALIZAR
    public Optional<Articulo> actualizar(int id, Articulo nuevosDatos)
    {
        Optional<Articulo> articuloExistente = articuloRepositorio.findById(id);	// Optional para desmontar y sacar articulo
        Optional<Articulo> resultado = Optional.empty(); 							// Optional vacio para montar y devolver

        if (articuloExistente.isPresent()) {
            Articulo articulo = articuloExistente.get();
            
            // Actualizr
            articulo.setNombre(nuevosDatos.getNombre());
            articulo.setPrecio(nuevosDatos.getPrecio());
            articulo.setStock_disponible(nuevosDatos.getStock_disponible());

            
            //  Guardar en el Optional de resultado
            resultado = Optional.of(articuloRepositorio.save(articulo));
        }

        return resultado;
    }
}
