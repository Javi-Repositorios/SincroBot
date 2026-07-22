package bot.SincroStock.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bot.SincroStock.modelos.Articulo;
import jakarta.persistence.LockModeType;


/**@author Javier Martinez Sodric
 * Interfaz DTO para acceder a db por el atributo marcado con @Id integer en el modelo y obtener Articulo
 */
@Repository
public interface ArticuloRepositorio extends JpaRepository<Articulo, Integer> {
	
	@Query("SELECT l.articulo FROM LineasPedido l GROUP BY l.articulo ORDER BY SUM(l.cantidad) DESC")
	List<Articulo> buscarProductoMasVendido(Pageable pageable);
	

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Articulo a WHERE a.id_articulo = :id")
	Optional<Articulo> findByIdForUpdate(@Param("id") int i);
	
}
