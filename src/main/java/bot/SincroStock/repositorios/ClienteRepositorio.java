package bot.SincroStock.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bot.SincroStock.modelos.Cliente;

/**@author Javier Martinez Sodric
 * Esta interfaz hereda de JpaRepository y proporciona los metodos de acceso genericos, findbyid, deletebyid, findAll
 * aplicados a nuestra especificación Cliente, String, que equivale a la entidad y el tipo de su clave primaria nif= string
 */
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {

}
