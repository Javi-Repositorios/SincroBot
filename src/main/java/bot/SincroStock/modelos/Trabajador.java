package bot.SincroStock.modelos;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**@author Javier Martinez Sodric
 * El modelo de la entidad Trabajador.PK username. Relacionado con la entidad roles N a M, crea tabla intermedia dinamica roles_de_trabajadores.
 */
@Entity
@Table(name = "trabajadores")
public class Trabajador {

    @Id
    private String username; 

    private String password;
 
	private String nombre;
    private String apellidos;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER para cargar los roles al hacer login
    @JoinTable(
        name = "roles_de_trabajadores",
        joinColumns = @JoinColumn(name = "ref_user"),
        inverseJoinColumns = @JoinColumn(name = "ref_rol")
    )
    private Set<Rol> roles = new HashSet<>();

    
    //Constructor vacio 
    public Trabajador()
    {
    	
    }
    
    // Getters y Setters 
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<Rol> getRoles() { return roles; }
    public void setRoles(Set<Rol> roles) { this.roles = roles; }    
    public String getNombre() {return nombre;}
 	public void setNombre(String nombre) {this.nombre = nombre;}
 	public String getApellidos() {return apellidos;}
 	public void setApellidos(String apellidos) {this.apellidos = apellidos;}
}