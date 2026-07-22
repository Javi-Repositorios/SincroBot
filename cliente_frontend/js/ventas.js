
	const urlBase = "http://localhost/api";
    // 1. Recuperamos con la misma llave que el login
    const token = localStorage.getItem('token');
    const vendedor = localStorage.getItem('usuario'); 

	
	// 1. SEGURIDAD: El Portero
	const rolesUsuario = JSON.parse(localStorage.getItem('roles') || "[]");
	if (!token || !rolesUsuario.includes('VENDEDOR')) {
	    console.error("Acceso denegado");
	    location.href = '/index.html';
	}
	
	//Saludo personalizado
    document.getElementById('nombre-vendedor').innerText = vendedor;

	

	
///CARGAR PEDIDOS DEL VENDEDOR: 	
async function cargarPedidos() {
    if (!vendedor) return;

    let url = `${urlBase}/pedidos/vendedor/${vendedor}`; 
    
    try {
        const res = await fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const pedidos = await res.json();
            const tabla = document.getElementById('tabla-pedidos');
            
            if (pedidos.length === 0) {
                tabla.innerHTML = '<tr><td colspan="5">No se encontraron pedidos para ' + vendedor + '</td></tr>';
                return;
            }

            // Usamos un string vacío para acumular el HTML
            let html = '';
            for (const p of pedidos) {
                html += `
                <tr class="pedido-row" onclick="toggleAcordeon(${p.id})">
                    <td>#${p.id}</td>
                    <td>${new Date(p.fecha).toLocaleDateString()}</td>
                    <td>${p.cliente ? p.cliente.nombre : 'Sin cliente'}</td>
                    <td>${(p.total || 0).toFixed(2)}€</td>				       
                </tr>
				<tr id="detalle-${p.id}" class="detalle-row" style="display: none;">
				        <td colspan="5" style="padding: 0;">
				            <div id="contenido-${p.id}" class="detalle-container"></div>
				        </td>
				    </tr>`;
            }
            tabla.innerHTML = html;
        }
    } catch (e) {
        console.error("Error en fetch pedidos:", e);
    }
}
	
///LOG OUT	
    function cerrarSesion() 
	{
        localStorage.clear();
        location.href = '/index.html';
    }

////ACORDEON DE LOS PEDIDOS	    
	async function toggleAcordeon(pedidoId) {
	    const filaDetalle = document.getElementById(`detalle-${pedidoId}`);
	    const contenedor = document.getElementById(`contenido-${pedidoId}`);
	    let mensajeError = "";

	    // 1. Lógica de visibilidad (Toggle)
	    if (filaDetalle.style.display === 'table-row') 
		{
	        filaDetalle.style.display = 'none';
	    } 
		else 
		{
	        filaDetalle.style.display = 'table-row';
	       

	        try {
	            const res = await fetch(`${urlBase}/pedidos/${pedidoId}/lineas`, {
	                headers: { 'Authorization': `Bearer ${token}` }
	            });

	            if (res.ok) {
	                const lineas = await res.json();
	                console.log("Datos de líneas:", lineas);

	                if (lineas && lineas.length > 0) {
	                    // Uso de bucle tradicional en lugar de .map()
	                    let filasHtml = "";
	                    for (const l of lineas) {
							
							//OBTENER LOS VALORES
	                        const nombreArt = l.articulo ? l.articulo.nombre : 'Producto sin nombre';
	                        const cant = l.cantidad || 0;
	                        const precio = l.precioVenta || 0;
	                        const subtotal = cant * precio;

							//AÑADIR ++1 LINEA
	                        filasHtml += `
	                            <tr>
	                                <td>${nombreArt}</td>
	                                <td>${cant}</td>
	                                <td>${precio.toFixed(2)}€</td>
	                                <td>${subtotal.toFixed(2)}€</td>
	                            </tr>`;
	                    }

						//POBLAR EL CONTENEDOR DEL PEDIDO CON LA TABLA  DEL DETALLE  DE LINEAS
	                    contenedor.innerHTML = `
	                        <table class="tabla-lineas">
	                            <thead>
	                                <tr>
	                                    <th>Producto</th>
	                                    <th>Cantidad</th>
	                                    <th>Precio Unit.</th>
	                                    <th>Subtotal</th>
	                                </tr>
	                            </thead>
	                            <tbody>
	                                ${filasHtml}
	                            </tbody>
	                        </table>`;
	                } 
					else // no hay lineas
					{
	                    mensajeError = "No hay productos en este pedido.";
	                }
	            } 
				else // mala respuesta
				{
	                mensajeError = "Error al obtener los datos del servidor.";
	            }
	        } 
			catch (e) // error de fecth
			{
	            console.error("Error:", e);
	            mensajeError = "Error de red al cargar el pedido.";
	        }

	        if (mensajeError) 
			{
	            contenedor.innerHTML = mensajeError;
	        }
	    }

	    return; 
	}
    
cargarPedidos();
////////////////////////////