const token = localStorage.getItem('token');
const urlBase = "/api/trabajadores";

// 1. SEGURIDAD: El Portero
const rolesUsuario = JSON.parse(localStorage.getItem('roles') || "[]");
if (!token || !rolesUsuario.includes('DIRECTOR')) {
    console.error("Acceso denegado");
    location.href = '/index.html';
}

// 2. CARGAR USUARIOS EN LA TABLA
async function cargarUsuarios() {
    try {
        const res = await fetch(urlBase, {
            headers: { 
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        });
        
        if (res.status === 401 || res.status === 403) {
            logout();
            return;
        }
        
        const usuarios = await res.json();
        const tabla = document.getElementById('tabla-usuarios');
        if (!tabla) return;
  
        tabla.innerHTML = usuarios.map(u => {
            const nombresRoles = u.roles ? u.roles.map(r => r.rol).join(', ') : 'Sin rol';
            
            return `
            <tr>
                <td>${u.username}</td>
                <td>${u.nombre || ''}</td>
                <td>${u.apellidos || ''}</td> 
                <td>********</td>
                <td><strong>${nombresRoles}</strong></td>
                <td>
                    <button class="btn btn-edit" onclick="prepararEdicion('${u.username}', '${u.nombre || ''}', '${u.apellidos || ''}', '${nombresRoles}')">Editar</button>
                    <button class="btn btn-delete" onclick="borrarUsuario('${u.username}')">Borrar</button>
                </td>
            </tr>`;
        }).join('');
    } catch (e) { 
        console.error("Error cargando usuarios:", e); 
    }
}

// 3. CARGAR ROLES
async function cargarRolesCheckboxes() {
    try {
        const res = await fetch(`${urlBase}/roles-disponibles`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) return;
        
        const rolesData = await res.json();
        const lista = document.getElementById('lista-checkboxes');
        if (!lista) return;
        
        lista.innerHTML = rolesData.map(r => `
            <label style="display: inline-block; margin-right: 15px; cursor:pointer;">
                <input type="checkbox" name="roles-check" value="${r.rol}"> ${r.rol}
            </label>
        `).join('');
    } catch (e) {
        console.error("Error cargando roles:", e);
    }
}

// 4. CREAR O EDITAR (SUBMIT)
const formulario = document.getElementById('form-usuario');
if (formulario) {
    formulario.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const usernameOriginal = document.getElementById('usuario-id').value;
        const checksMarcados = document.querySelectorAll('input[name="roles-check"]:checked');
        const listaRoles = Array.from(checksMarcados).map(cb => ({ rol: cb.value }));

        const usuarioData = {
            username: document.getElementById('username').value.trim(),
            nombre: document.getElementById('nombre-real').value.trim(),
            apellidos: document.getElementById('apellidos').value.trim(), 
            password: document.getElementById('password').value,
            roles: listaRoles
        };

        // --- LOGS  ---
        console.log("--- DATOS A ENVIAR ---");
        console.log("Objeto JS:", usuarioData);
        console.log("JSON String:", JSON.stringify(usuarioData));
        console.log("URL Destino:", usernameOriginal ? `${urlBase}/${usernameOriginal}` : urlBase);
        console.log("Token usado:", token ? "Token presente" : "¡TOKEN FALTA!");
        // ------------------------------

        const esEdicion = usernameOriginal !== "";
        const metodo = esEdicion ? 'PUT' : 'POST';
        const url = esEdicion ? `${urlBase}/${usernameOriginal}` : urlBase;

        try {
            const res = await fetch(url, {
                method: metodo,
                headers: { 
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify(usuarioData)
            });

            console.log("Respuesta del servidor - Status:", res.status);

            if (res.ok) {
                alert(esEdicion ? "Actualizado correctamente" : "Creado correctamente");
                resetForm();
                cargarUsuarios();
            } else {
                const errorTexto = await res.text();
                console.error("Error del servidor:", errorTexto);
                alert("Error: " + res.status);
            }
        } catch (e) {
            console.error("Error de red:", e);
            alert("Error de conexión");
        }
    });
}

// 5. BORRAR
async function borrarUsuario(username) {
	
    if (!confirm(`¿Seguro que quieres eliminar a ${username}?`)) return;
    
	try 
	{
        const res = await fetch(`${urlBase}/${username}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (res.ok) cargarUsuarios();
    }
	 catch (e) { console.error(e); }
}

// 6. PREPARAR EDICIÓN
function prepararEdicion(username, nombre, apellidos, rolesString) {
    document.getElementById('usuario-id').value = username;
    document.getElementById('username').value = username;
    document.getElementById('username').disabled = true;
    document.getElementById('nombre-real').value = nombre;
    document.getElementById('apellidos').value = apellidos; 
    document.getElementById('password').placeholder = "Vacío para no cambiar";

    const listaRolesActuales = rolesString.split(', '); 
    document.querySelectorAll('input[name="roles-check"]').forEach(cb => {
        cb.checked = listaRolesActuales.includes(cb.value);
    });

    document.getElementById('btn-submit').innerText = "Actualizar";
    document.getElementById('btn-cancelar').style.display = "inline-block";
}

// 7. RESET
function resetForm() {
    if (formulario) formulario.reset();
    document.getElementById('usuario-id').value = "";
    document.getElementById('username').disabled = false;
    document.querySelectorAll('input[name="roles-check"]').forEach(cb => cb.checked = false);
    document.getElementById('btn-submit').innerText = "Añadir Trabajador";
    document.getElementById('btn-cancelar').style.display = "none";
}

// 8. LOGOUT
function logout() {
    localStorage.clear();
    location.href = '/index.html';
}

// 9. INICIO
document.addEventListener('DOMContentLoaded', () => {
    cargarUsuarios();
    cargarRolesCheckboxes();
});