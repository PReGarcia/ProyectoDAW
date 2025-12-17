document.addEventListener('DOMContentLoaded', function() {
    const inputBusqueda = document.getElementById('buscadorPropiedades');
    const contenedor = document.getElementById('listaPropiedades');
    // Obtenemos el context path de algún sitio, o lo hardcodeamos dinámicamente si es necesario
    // Un truco es leerlo de un atributo data en el body o deducirlo, pero aquí asumiremos raíz relativa
    const baseUrl = window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));

    let timeoutId;

    inputBusqueda.addEventListener('input', function(e) {
        const texto = e.target.value;

        // Debounce: Esperar 300ms a que el usuario termine de escribir para no saturar el servidor
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            realizarBusqueda(texto);
        }, 300);
    });

    function realizarBusqueda(query) {
        // Llamada AJAX al controlador
        fetch(`${baseUrl}/propiedad/api/buscar?q=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                actualizarVista(data);
            })
            .catch(error => console.error('Error buscando:', error));
    }

    function actualizarVista(propiedades) {
        contenedor.innerHTML = ''; // Limpiar contenido actual

        if (propiedades.length === 0) {
            contenedor.innerHTML = '<p>No se encontraron propiedades.</p>';
            return;
        }

        propiedades.forEach(p => {
            // Reconstruir el HTML de la tarjeta. 
            // ADAPTAR ESTE HTML AL QUE USAS REALMENTE EN TU JSP
            const html = `
                <div class="propiedad-card">
                    <div class="propiedad-img-container">
                        <img src="${p.foto}" alt="${p.nombre}" class="propiedad-img">
                    </div>
                    <div class="propiedad-info">
                        <h3 class="propiedad-titulo">${p.nombre}</h3>
                        <p class="propiedad-ubicacion"><small>${p.ciudad}</small></p>
                        <div class="propiedad-footer">
                            <span class="propiedad-precio">${p.precio} € / noche</span>
                            <a href="${baseUrl}/propiedad/detalle?id=${p.id}" class="button">Ver más</a>
                        </div>
                    </div>
                </div>
            `;
            contenedor.innerHTML += html;
        });
    }
});