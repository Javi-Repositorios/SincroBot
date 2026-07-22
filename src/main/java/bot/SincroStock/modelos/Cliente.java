package bot.SincroStock.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**@author Javier Martinez Sodric
 * El modelo de la entidad Cliente. PK:Nif
 */
@Entity
@Table(name="clientes")
public class Cliente {
	
	@Id
	@Column(name ="nif")
	String nif;
	String nombre;
	String email;
	String telefono;
	
	public Cliente() {}
	
	public Cliente(String nif, String nombre, String email, String telefono) {
		super();
		this.nif = nif;
		this.nombre = nombre;
		this.email = email;
		this.telefono = telefono;
	}
	
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	


}
