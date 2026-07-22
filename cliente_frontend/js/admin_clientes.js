
// LO PRIMERO CONSEGUIR EL TOKEN DEL ALMACENAJE Y ESTABLECER LA URL 
// DEL CONTROLADOR

const token = localStorage.getItem('token');
const urlBase ="/api/clientes";

// SEGURIDAD: Necesitas token para permanecer en la página.
// retorna Array de Roles o Array Vacio
const rolesUsuario = JSON.parse(localStorage.getItem('roles') || "[]");

	if (!token || !rolesUsuario.includes('DIRECTOR'))
		{
	    console.error("Acceso denegado");
	    location.href = '/index.html';
	}
////////

// CARGAR CLIENTES
async function cargarClientes()
{
	let htmlResultado = '';
	//Intentar obtener repuesta de la API
	//Parametros url y json
	try{
		
	const res = await fetch(urlBase,{
		headers: {
			'Authorization': 'Bearer ' + token,
			'Accept': 'application/json'		
		}
		
	});	
	
	//COMPROBAR RESPUESTA
	if(res.status === 401 || res.status ===403) 
	{
		logout();
	}
	else
	{		
		const tabla = document.getElementById('tabla-clientes');
			
		if(tabla != null)
		{
			const clientes = await res.json();

			            // Usamos un bucle for...of en lugar de map
			            for (const c of clientes) {
			               
							//Se concatena dentro de table cada TABLE ROW	. Las variables de cliente se extraen del JSON, por eso no necesitan getter		              
			                htmlResultado += `
			                <tr>
			                    <td>${c.nif}</td>
			                    <td>${c.nombre || ''}</td>
			                    <td>${c.email || ''}</td> 
								<td>${c.telefono || ''}</td> 
			               
			                    <td>
			                        <button class="btn btn-edit" onclick="prepararEdicion('${c.nif}', '${c.nombre }', '${c.email }', '${c.telefono}')">Editar</button>
			                        <button class="btn btn-delete" onclick="borrarCliente('${c.nif}')">Borrar</button>
			                    </td>
			                </tr>`;
			            }			           
			            tabla.innerHTML = htmlResultado;
			        }				
		}
	}
	catch(e)
	{
		console.error("Error cargando Clientes:" + e)
	}	
}
////////////////


// 2. GUARDAR (La lógica pura de comunicación con la API)
async function guardarCliente(clienteData, esEdicion) {
	
    const url = esEdicion ? `${urlBase}/${clienteData.nif}` : urlBase;
    const metodo = esEdicion ? 'PUT' : 'POST';

    try 
	{
        const res = await fetch(url, {
            method: metodo,
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clienteData)
        });

        if (res.ok) {
            alert(esEdicion ? "Cliente actualizado" : "Cliente creado");
            resetForm();
            cargarClientes();
        } else {
            alert("Error en la operación: " + res.status);
        }
    }
	catch (error) 
	{
        console.error("Error de conexión:", error);
    }
}
////

// 3. CARGAR BOTONES FORM (Configura el listener del formulario)
function cargarBotonesForm() {
	
    const formulario = document.getElementById('form-cliente');
	
    if (formulario) 
		{
        formulario.addEventListener('submit', (e) => {
            e.preventDefault();

            // Extraemos los datos del formulario
            const nifInput = document.getElementById('nif');
            const clienteData = {
                nif: nifInput.value,
                nombre: document.getElementById('nombre').value,
                email: document.getElementById('email').value,
                telefono: document.getElementById('telefono').value
            };

            // Determinamos si es edición basándonos en si el NIF está bloqueado
            const esEdicion = nifInput.hasAttribute('readonly');

            // Llamamos a la función de guardado
            guardarCliente(clienteData, esEdicion);
        });
    }
}
////////////

// 3. BORRAR
async function borrarCliente(nif)
{
    if (!confirm("¿Eliminar cliente?")) return;
    try 
	{
        const res = await fetch(`${urlBase}/${nif}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
		
        if (res.ok) cargarClientes();
    }
	catch (e) 
	{ 
		console.error(e); 
	}
}
///////////////

// 4. UTILIDADES
function prepararEdicion(nif, nombre, email, telefono) {
    const nifInput = document.getElementById('nif');
    
    // Rellenamos los campos
    nifInput.value = nif;
    document.getElementById('nombre').value = nombre;
    document.getElementById('email').value = email;
    document.getElementById('telefono').value = telefono;
    
    // BLOQUEO DEL NIF
    nifInput.readOnly = true; // Esto evita que el usuario lo cambie
    nifInput.style.backgroundColor = "#e9ecef"; // Opcional: darle un color gris de "deshabilitado"
    
    // Cambiamos el texto del botón para que el usuario sepa que está editando
    document.getElementById('btn-submit').innerText = "Actualizar Cliente";
    document.getElementById('btn-cancelar').style.display = "inline-block";
}
/////////////////

function resetForm() {
    const formulario = document.getElementById('form-cliente');
    if (formulario) formulario.reset();
    
    const nifInput = document.getElementById('nif');
    nifInput.readOnly = false; // <--- LIBERAR EL CAMPO
    nifInput.style.backgroundColor = "white"; 
    
    document.getElementById('btn-submit').innerText = "Guardar Cliente";
    document.getElementById('btn-cancelar').style.display = "none";
}
////////////////

function logout() 
{
    localStorage.clear();
    location.href = '/index.html';
}
//////////////


///CARGA POR EVENTO
document.addEventListener('DOMContentLoaded', () => {
    cargarClientes();
    cargarBotonesForm();
});
//////FIN DEL SCRIPT////////////

