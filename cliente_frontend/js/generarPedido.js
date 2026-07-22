// Leer el token
    const token = localStorage.getItem('token');
	
	
	// Definición centralizada del servidor backend
	const BACKEND_URL = "http://localhost/api"; 

	const urlBase = `${BACKEND_URL}/articulos`;
	const urlClientes = `${BACKEND_URL}/clientes`;
	const urlPedidos = `${BACKEND_URL}/pedidos/guardar`;
	



//SEGURIDAD: El Portero
	const rolesUsuario = JSON.parse(localStorage.getItem('roles') || "[]");
	if (!token || !rolesUsuario.includes('VENDEDOR'))
	{
	    console.error("Acceso denegado");
	    location.href = '/index.html';
	}	
	
//VALIDAR PEDIDO	
	async function validarYProcesarPedido() 
	{
	    // Obtener elementos del DOM
	    const selectClientes = document.getElementById('select-clientes');
	    const clienteId = selectClientes ? selectClientes.value : null;
	    let usernameVendedor = localStorage.getItem('usuario');
	    // Validacion FrontEnd
	    if (!usernameVendedor) 
		{
	        return alert("Error: No se detecta el vendedor. Vuelve a iniciar sesión.");
	    }
	    if (!clienteId || clienteId === "") 
		{
	        return alert("Selecciona un cliente de la lista.");
	    }		
	    //GESTION DE LA LINEA DE PEDIDO
		const inputs = document.querySelectorAll('.input-cantidad');
		const lineas = [];
		let totalAcumulado = 0;
	
		for (let i = 0; i < inputs.length; i++) 
			{
		    const input = inputs[i];
		    const cant = parseInt(input.value);

		    if (cant > 0) 
				{
		        // Extraemos el ID del atributo id del input
		        const idArt = input.id.replace('input-prod-', '');		        
		        // Buscamos la fila y el precio
		        const fila = input.closest('tr');
		        const precioTexto = fila.cells[2].innerText.replace('€', '').trim();
		        const precio = parseFloat(precioTexto);
		        // Creamos el objeto
		        const nuevaLinea = {
		            cantidad: cant,
		            precioVenta: precio,
		            articulo: { 
		                id_articulo: parseInt(idArt)
		            }
		        };
		        // AÑADIR a la lista y al total
		        lineas.push(nuevaLinea);
		        totalAcumulado += (precio * cant);
		    }
		}	  
		//CREAR EL OBJETO JAVASCRIPT DEL PEDIDO
		const pedidoData = {
		    cliente: { 
		        nif: clienteId 
		    },
		    vendedor: { username: usernameVendedor.trim() },
		    total: totalAcumulado,
		    lineas: lineas
		};
	    //ENVIO AL SERVIDOR
	    try {
	        const res = await fetch(urlPedidos, {
	            method: 'POST',
	            headers: {
	                'Authorization': 'Bearer ' + token,
	                'Content-Type': 'application/json'
	            },
	            body: JSON.stringify(pedidoData)
	        });

	        if (res.ok) 
			{
	            alert("Pedido guardado y stock actualizado.");
	            location.reload(); 
	        } 
			else 
			{
	            // Si falla, intentamos leer el mensaje de error de Spring
	            const errorTxt = await res.text();
	            console.error("Error del servidor:", errorTxt);
	            alert("Error " + res.status + ": " + errorTxt);
	        }
	    } 
		catch (e) 
		{
	        console.error("Error de conexión:", e);
	        alert("Error de conexión con el servidor.");
	    }
	}

//CARGAR INVENTARIO: (la lista de articulos para elegir)
async function cargarInventario() {
	
	try {
	    //PETICION
	    const res = await fetch(urlBase + "?t=" + new Date().getTime(), {
	        headers: { 'Authorization': 'Bearer ' + token }
	    });

	    if (!res.ok) throw new Error("Error al obtener productos");
		
		
		//MANEJO DE REPUESTA
	    const productos = await res.json();
	    const tabla = document.getElementById('cuerpo-inventario');
	    
	    //Variable para acumular el HTML
	    let contenidoHtml = '';

	    // 
		for (let i = 0; i < productos.length; i++) 
		{
		    const p = productos[i];
		    const id = p.id_articulo; 
		    const stock = p.stock_disponible;
		    // Formateo del precio (manejando si es nulo)
		    let precioFormateado = '0.00';
			
		    if (p.precio) 
			{
		        precioFormateado = p.precio.toFixed(2);
		    }

		    // Concatenamos la fila al contenido total
		    contenidoHtml += `
		        <tr>
		            <td>${id}</td>
		            <td><strong>${p.nombre}</strong></td>
		            <td>${precioFormateado}€</td>	            
		            <td id="stock-val-${id}">${stock}</td>		            
		            <td>
		                <input type="number" 
		                       id="input-prod-${id}" 
		                       class="input-cantidad" 
		                       min="0" 
		                       max="${stock}" 
		                       value="0"
		                       oninput="verificarStockVisual(${id}, ${stock})">
		            </td>
		        </tr>
		    `;
		}

	    // 3. Inyectamos todo el HTML generado de una sola vez
	    tabla.innerHTML = contenidoHtml;

	} 
	catch (e) 
	{
        console.error("Error cargando inventario:", e);
        document.getElementById('cuerpo-inventario').innerHTML = "<tr><td colspan='5'>Error al cargar los datos.</td></tr>";
    }
}

//AUXILIAR PARA BUCLE : VERIFICACION FRONTEND STOCK
function verificarStockVisual(id, stockMaximo)
{
    const input = document.getElementById(`input-prod-${id}`);
    const valor = parseInt(input.value);

    if (valor > stockMaximo) 
	{
        input.classList.add('input-error');
    } 
	else 
	{
        input.classList.remove('input-error');
    }
}


/// CARGAR CLIENTES: Para el selector
async function cargarClientes() {
	
    try 
	{
        const res = await fetch(urlClientes, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        
        if (!res.ok) throw new Error("No se pudieron cargar los clientes");
        
		//lista clientes y selector
        const clientes = await res.json();
        const select = document.getElementById('select-clientes');
        
		// Opción por defecto
		let opcionesHtml = '<option value="">-- Seleccione un cliente --</option>';

		// Usamos un bucle for clásico para recorrer los clientes
		for (let i = 0; i < clientes.length; i++) 
		{
		    const c = clientes[i];
		    // Vamos acumulando cada fila de <option>
		    opcionesHtml += '<option value="' + c.nif + '">' + c.nombre + ' (' + c.nif + ')</option>';
		}

		// 3. Rellenamos el combo de una sola vez al final
		select.innerHTML = opcionesHtml;         
    } 
	catch (e) 
	{
        console.error("Error cargando clientes:", e);
        document.getElementById('select-clientes').innerHTML = '<option value="">Error al cargar</option>';
    }
}

///LOG OUT	
    function cerrarSesion() 
	{
        localStorage.clear();
        location.href = '/index.html';
    }
//VOLVER
	function volver() 
	{
	    location.href = '/html/PanelesTrabajo/Ventas.html';
	}

// Actualiza el DOMContentLoaded para que cargue Inventario y Select de Cliente
document.addEventListener('DOMContentLoaded', () => {
    cargarInventario();
    cargarClientes();
});