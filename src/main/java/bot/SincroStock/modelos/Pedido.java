package bot.SincroStock.modelos;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**@author Javier Martinez Sodric
 * Modelo de la entidad Pedido. PK id
 */
@Entity
@Table(name = "pedidos")
public class Pedido {
    

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private Double total = 0.0;
	
	@Column(name = "fecha_pedido")
	private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "ref_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "ref_usuario")
    private Trabajador vendedor; 

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("pedido") // Evita que la línea intente serializar el pedido de vuelta
    private List<LineasPedido> lineas;
 

    
    
    //CONSTRUCTORES:
    public Pedido(Long id, LocalDateTime fecha, Cliente cliente, Trabajador vendedor, List<LineasPedido> lineas) {
		this.id = id;
		this.fecha = fecha;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.lineas = lineas;
	}
    
    public Pedido() {
	
	}
    // Getters y Setters
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Trabajador getVendedor() {
		return vendedor;
	}

	public void setVendedor(Trabajador vendedor) {
		this.vendedor = vendedor;
	}

	public List<LineasPedido> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineasPedido> lineas) {
		this.lineas = lineas;
	}
}
