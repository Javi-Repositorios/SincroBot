const token = localStorage.getItem('token');
const urlBase = "http://localhost/api/articulos";

if (!token) {
    location.href = '/index.html';
}

// 1. CARGAR ARTÍCULOS
async function cargarArticulos() {
    try {
        const res = await fetch(urlBase, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        
        if (res.status === 401) logout();
        
        const articulos = await res.json();
        const tabla = document.getElementById('tabla-articulos');
        
        // Limpiamos la tabla antes de empezar el bucle
        let htmlFinal = "";

        // Usamos un bucle for...of para recorrer la lista
        for (const art of articulos) {
            // Escapamos el nombre por si tiene comillas simples que rompan el onclick
            const nombreEscapado = art.nombre.replace(/'/g, "\\'");

            htmlFinal += `
                <tr>
                    <td>${art.id_articulo}</td>
                    <td>${art.nombre}</td>
                    <td>${art.stock_disponible}</td>
                    <td>${art.precio.toFixed(2)}€</td>
                    <td>
                        <button class="btn btn-edit" onclick="prepararEdicion(${art.id_articulo}, '${nombreEscapado}', ${art.precio}, ${art.stock_disponible})">Editar</button>
                        <button class="btn btn-delete" onclick="borrarArticulo(${art.id_articulo})">Borrar</button>
                    </td>
                </tr>`;
        }

        // Inyectamos todo el HTML generado de una sola vez
        tabla.innerHTML = htmlFinal;

    } catch (e) { 
        console.error("Error cargando artículos", e);
        document.getElementById('tabla-articulos').innerHTML = '<tr><td colspan="5">Error al conectar con el servidor</td></tr>';
    }
}

// 2. GUARDAR O ACTUALIZAR
const formulario = document.getElementById('form-articulo');
if (formulario) {
    formulario.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const id = document.getElementById('articulo-id').value;
        
        // El objeto debe tener los nombres EXACTOS de tu clase Java Articulo.java
        const articuloData = {
            nombre: document.getElementById('nombre').value,
            precio: parseFloat(document.getElementById('precio').value),
            stock_disponible: parseInt(document.getElementById('stock').value)
        };

		if (id) {
		   
		    articuloData.id_articulo = parseInt(id); 
		}

        const metodo = id ? 'PUT' : 'POST';
        const url = id ? `${urlBase}/${id}` : urlBase;

        try {
            const res = await fetch(url, {
                method: metodo,
                headers: { 
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify(articuloData)
            });

            if (res.ok) {
                alert(id ? "Artículo actualizado" : "Artículo creado");
                resetForm();
                cargarArticulos();
            } else {
                alert("Error al guardar: " + res.status);
            }
        } catch (error) {
            alert("Error de conexión");
        }
    });
}

// 3. BORRAR
async function borrarArticulo(id) {
    if (!confirm("¿Eliminar artículo?")) return;
    try {
        const res = await fetch(`${urlBase}/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (res.ok) cargarArticulos();
    } catch (e) { console.error(e); }
}

// 4. UTILIDADES
function prepararEdicion(id, nombre, precio, stock) {
    document.getElementById('articulo-id').value = id;
    document.getElementById('nombre').value = nombre;
    document.getElementById('precio').value = precio;
    document.getElementById('stock').value = stock;
    document.getElementById('btn-submit').innerText = "Actualizar Artículo";
    document.getElementById('btn-cancelar').style.display = "inline-block";
}

function resetForm() {
    if (formulario) formulario.reset();
    document.getElementById('articulo-id').value = "";
    document.getElementById('btn-submit').innerText = "Guardar Artículo";
    document.getElementById('btn-cancelar').style.display = "none";
}



function logout() {
    localStorage.clear();
    location.href = '/index.html';
}

document.addEventListener('DOMContentLoaded', cargarArticulos);