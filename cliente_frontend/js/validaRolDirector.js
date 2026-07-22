
/**
 * Al cargar el documento se busca el Rol en el localStorage
 */
document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Recuperamos los roles del localStorage
    const roles = JSON.parse(localStorage.getItem('roles') || "[]");

    // 2. Verificamos si es DIRECTOR
    if (!roles.includes('DIRECTOR')) {
        alert("Acceso denegado: No tienes permisos de Director.");
        // Te manda a la página principal
        window.location.href = '../index.html';
    }
    
    // Si es director, no hace nada y la página carga normalmente
    console.log("Validación de rol completada: Acceso permitido.");
});