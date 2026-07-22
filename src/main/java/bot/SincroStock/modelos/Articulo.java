package bot.SincroStock.modelos;



import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;



/**@author Javier Martinez Sodric
 * Modelo de la entidad Articulo.
 */
@Entity
@Table(name="articulos")
public class Articulo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Para el SERIAL de Postgres
	@Column(name="articulo_id")
	int id_articulo;
	String nombre;
	float precio;
	
	@Column(name="stock_disponible")
	int stock_disponible;
	
	@OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL)
    @JsonIgnore 
    private Set<LineasPedido> lineasPedido;
	
	
	public Articulo() {}
	
	
	public Articulo(int id_articulo, String nombre, float precio, int stock_disponible) {
		super();
		this.id_articulo = id_articulo;
		this.nombre = nombre;
		this.precio = precio;
		this.stock_disponible = stock_disponible;
	}
	
	
	public int getId_articulo() {
		return id_articulo;
	}
	public void setId_articulo(int id_articulo) {
		this.id_articulo = id_articulo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public float getPrecio() {
		return precio;
	}
	public void setPrecio(float precio) {
		this.precio = precio;
	}
	public int getStock_disponible() {
		return stock_disponible;
	}
	public void setStock_disponible(int stock_disponible) {
		this.stock_disponible = stock_disponible;
	}


}
