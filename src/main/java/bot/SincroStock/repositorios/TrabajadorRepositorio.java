package bot.SincroStock.repositorios;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bot.SincroStock.modelos.Trabajador;


/**@author Javier Martinez Sodric
 * Repositorio del modelo Trabajador
 * Contiene la query personalizada para obtener mejor vendedor
 */
@Repository
public interface TrabajadorRepositorio extends JpaRepository<Trabajador, String> {
	
    Optional<Trabajador> findByUsername(String username);
        
    @Query("SELECT p.vendedor FROM Pedido p GROUP BY p.vendedor ORDER BY SUM(p.total) DESC")
    List<Trabajador> buscarVendedorConMasVentas(Pageable pageable);
}

