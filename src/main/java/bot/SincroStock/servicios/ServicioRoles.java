package bot.SincroStock.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bot.SincroStock.modelos.Rol;
import bot.SincroStock.repositorios.RolRepositorio;


/**@author Javier Martinez Sodric
 * Servicio para acceder al repositorio Rol
 */
@Service
public class ServicioRoles {

    @Autowired 
    private RolRepositorio repositorio;

    public List<Rol> listarTodos() {
        return repositorio.findAll();
    }
    
}