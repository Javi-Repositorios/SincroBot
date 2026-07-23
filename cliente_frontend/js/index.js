




//LISTENER ENVIO FORMULARIO LOGIN
document.getElementById('btn-login').addEventListener('click', async (e) => {
 
    e.preventDefault();
	
	const urlserver = "/api/trabajadores/login";
	const urllocal = "/api/trabajadores/login";
	
	
	

	//RESCATAR DATOS
    const usuario = document.getElementById('userInput').value;
    const password = document.getElementById('passInput').value;

    // VALIDACION FRONTEND
    if (!usuario || !password)
	{
        alert("Por favor, rellena todos los campos");
        return;
    }

	// FETCH
    try 
	{
		//PREGUNTA		
        const respuesta = await fetch(urllocal, 
			{
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usuario, password: password })
        	}
			);

			//RESPUESTA
			if (respuesta.ok) {
			    const datos = await respuesta.json(); 
			    
			    // 1. Convertimos el string de Java en un Array, separado por comas, libre de espacios
			    const listaRoles = datos.roles.split(',').map(r => r.trim());

			    // 2. Guardamos lo esencial
			    localStorage.setItem('token', datos.token);
			    localStorage.setItem('usuario', datos.username);
			    
			    //3.  Guardamos la lista de roles convertida a JSON .
			    localStorage.setItem('roles', JSON.stringify(listaRoles));

			    // REDIRECCION
			    if (listaRoles.length > 1)
				{
			        window.location.href = 'html/SeleccionPerfil.html';
			    } 
				else if (listaRoles.includes('DIRECTOR')) 
				{
			        window.location.href = 'html/PanelesTrabajo/Direccion.html';
			    } 
				else if (listaRoles.includes('VENDEDOR')) 
				{
			        window.location.href = 'html/PanelesTrabajo/Ventas.html';
			    }
			}
			else 
			{
	            // Si el servidor responde pero con error (401, 403, etc.)
	            alert("Usuario o contraseña incorrectos");
	        }
    } 
    catch (error) 
	{
        console.error("Error en la conexión:", error);
        alert("Error crítico: No se pudo conectar con el servidor.");
    }
});