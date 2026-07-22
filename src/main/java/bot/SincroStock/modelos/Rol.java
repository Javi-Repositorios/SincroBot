package bot.SincroStock.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**@author Javier Martinez Sodric
 * Modelo de la entidad roles. PK rol_id
 */
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String rol; // Ejemplo: "DIRECTOR", "VENTAS"

    
    
    public Rol() {}
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
