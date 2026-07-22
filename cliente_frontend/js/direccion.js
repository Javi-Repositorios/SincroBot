document.addEventListener("DOMContentLoaded", () => {
    cargarArticuloTop();
    cargarVendedorTop();
    cargarHistorialPedidos();
});

let todosLosPedidos = []; // Variable global para los filtros

// Obtener articulo más vendido
async function cargarArticuloTop() 
{
    try 
	{
        const response = await fetch('/api/articulos/mas-vendido');
        if (response.ok) 
		{
            const art = await response.json();
            document.getElementById('txt-articulo-top').innerText = art ? art.nombre : "Sin datos";
        }
    } 
	catch (error) 
	{
        console.error("Error cargando producto top:", error);
        document.getElementById('txt-producto-top').innerText = "Error";
    }
}

// Obtener mejor vendedor
async function cargarVendedorTop() {
    try 
	{
        const response = await fetch('/api/trabajadores/mejor-vendedor');
        if (response.ok) 
		{
            const vend = await response.json();
            document.getElementById('txt-vendedor-top').innerText = vend ? vend.nombre : "Sin datos";
        }
    } 
	catch (error) 
	{
        console.error("Error cargando vendedor top:", error);
        document.getElementById('txt-vendedor-top').innerText = "Error";
    }
}

//  Cargar historial 
async function cargarHistorialPedidos() {
    try 
	{
        const response = await fetch('/api/pedidos'); 
        todosLosPedidos = await response.json();
        cargarTabla(todosLosPedidos);
    }
	catch (error) 
	{
        console.error("Error:", error);
    }
}


function cargarTabla(lista) {
    const tbody = document.getElementById('tabla-pedidos-body');
	
    if (!tbody) return;
	
    tbody.innerHTML = ""; 

    if (!lista || lista.length === 0) 
	{
        tbody.innerHTML = "<tr><td colspan='6'>No hay pedidos para mostrar</td></tr>";
        return;
    }

    for (let i = 0; i < lista.length; i++) 
		{
        let pedido = lista[i];

        const trCabecera = document.createElement('tr');
        trCabecera.className = 'pedido-cabecera';
        trCabecera.onclick = function() { toggleAcordeon(pedido.id); };
        
        const vNombre = (pedido.vendedor && pedido.vendedor.nombre) ? pedido.vendedor.nombre : "Sin Vendedor";
        const cNombre = (pedido.cliente && pedido.cliente.nombre) ? pedido.cliente.nombre : "Sin Cliente";
        const totalPedido = pedido.total ? pedido.total.toFixed(2) : "0.00";

		trCabecera.innerHTML = `
		    <td>#${pedido.id}</td>
		    <td>${new Date(pedido.fecha).toLocaleDateString()}</td>
		    <td>${vNombre}</td>
		    <td>${cNombre}</td>
		    <td><strong>${totalPedido}€</strong></td>
		`;

        const trDetalle = document.createElement('tr');
        trDetalle.id = "detalle-" + pedido.id;
        trDetalle.className = 'fila-detalle';
        trDetalle.style.display = 'none';
        trDetalle.innerHTML = `
            <td colspan="6">
                <div class="contenido-detalle" id="contenido-${pedido.id}">
                    <p>Cargando productos...</p>
                </div>
            </td>
        `;

        tbody.appendChild(trCabecera);
        tbody.appendChild(trDetalle);
    	}
}

async function toggleAcordeon(id) {
	
    const filaDetalle = document.getElementById("detalle-" + id);
    const contenedor = document.getElementById("contenido-" + id);

    if (filaDetalle.style.display === "table-row") 
	{
        filaDetalle.style.display = "none";
        return;
    }

    filaDetalle.style.display = "table-row";

	//PEDIR LINEAS
    try 
	{
        const response = await fetch(`/api/pedidos/${id}/lineas`);
        if (!response.ok) throw new Error("Error detalle");
        const lineas = await response.json();

        if (!lineas || lineas.length === 0) {
            contenedor.innerHTML = "<p>Sin artículos.</p>";
            return;
        }
		
		//INSERTAR LINEAS
        let insertoHtml = `<table style="width:100%; border-collapse: collapse;">
            <thead>
                <tr><th>Producto</th><th>Cant.</th><th>Precio</th><th>Total</th></tr>
            </thead><tbody>`;

        for (let i = 0; i < lineas.length; i++) {
            let l = lineas[i];
            insertoHtml += `<tr>
                <td>${l.articulo.nombre}</td>
                <td>${l.cantidad}</td>
                <td>${l.precioVenta.toFixed(2)}€</td>
                <td>${(l.cantidad * l.precioVenta).toFixed(2)}€</td>
            </tr>`;
        }
        insertoHtml += `</tbody></table>`;
        contenedor.innerHTML = insertoHtml;
    } 
	catch (error) 
	{
        contenedor.innerHTML = "<p>Error al cargar.</p>";
    }
}

function filtrarPedidos() {
	
	//CONSEGUIR VALORES DE FILTROS
    const f_vendedor = document.getElementById('filtro-vendedor').value.toLowerCase();
    const f_cliente = document.getElementById('filtro-cliente').value.toLowerCase();
    const f_fecha = document.getElementById('filtro-fecha') ? document.getElementById('filtro-fecha').value : "";

    const filtrados = [];  //ARRAY VACIO
	
    for (let i = 0; i < todosLosPedidos.length; i++) 
		{
        let pedidos = todosLosPedidos[i];
		
		// Se obtienen los objetos cliente y vendedor del pedido, se compara el nombre vs el contenido del filtro.
        const coincideVendedor = pedidos.vendedor.nombre.toLowerCase().includes(f_vendedor);
        const coincideCliente = pedidos.cliente.nombre.toLowerCase().includes(f_cliente);
        
        let coincideFecha = true;
		
		//La fecha hay que recortarla para quitar la hora
        if (f_fecha !== "") 
		{
            coincideFecha = (pedidos.fecha.substring(0, 10) === f_fecha);
        }

		//Resultado positivo se añade a la lista de filtrados.
        if (coincideVendedor && coincideCliente && coincideFecha) 
		{
            filtrados.push(pedidos);
        }
    }
	//Actualizar
    cargarTabla(filtrados);
}

function limpiarFiltros() {
	
    document.getElementById('filtro-vendedor').value = "";
    document.getElementById('filtro-cliente').value = "";
    const inputFecha = document.getElementById('filtro-fecha');	
    if(inputFecha) inputFecha.value = "";
    cargarTabla(todosLosPedidos);
}

///LOG OUT	
    function cerrarSesion() 
	{
        localStorage.clear();
        location.href = '/index.html';
    }