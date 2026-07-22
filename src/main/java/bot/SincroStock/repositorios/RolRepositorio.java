package bot.SincroStock.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bot.SincroStock.modelos.Rol;


/**@author Javier Martinez Sodric
 * Interfaz DTO para el modelo Rol.
 */
@Repository
public interface RolRepositorio extends JpaRepository<Rol, String> {
	
	Optional<Rol> findByRol(String rol);
}