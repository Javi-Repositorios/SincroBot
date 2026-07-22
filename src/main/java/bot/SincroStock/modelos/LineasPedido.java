package bot.SincroStock.modelos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**@author Javier Martinez Sodric
 * Modelo de la entidad lineas_de_pedido PK id_linea
 */
@Entity
@Table(name="lineas_de_pedido")
public class LineasPedido {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_linea;
    
    private int cantidad;
    @Column(name="precio_venta")
    private float precioVenta;
    
    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo; 
    
	
	@ManyToOne
    @JoinColumn(name = "pedido_id")
	@JsonIgnoreProperties("lineas")
    private Pedido pedido;
	

	

	public LineasPedido() {}
	
	 // Constructor para cuando creas la línea desde el JS
    public LineasPedido(Articulo articulo, Integer cantidad) {
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.precioVenta = articulo.getPrecio(); // Capturamos el precio actual
    }
    
    public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
    
    
    public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	
	public int getId_linea() {
		return id_linea;
	}
	public void setId_linea(int id_linea) {
		this.id_linea = id_linea;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public float getPrecioVenta() {
		return precioVenta;
	}
	public void setPrecioVenta(float precioVenta) {
		this.precioVenta = precioVenta;
	}
	
	


}
