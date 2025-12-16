document.addEventListener("DOMContentLoaded", function () {
    const mapContainer = document.getElementById('map');
    
    // Obtenemos los datos de los atributos data-
    if (!mapContainer) return;

    const latStr = mapContainer.dataset.lat || "0.0";
    const lngStr = mapContainer.dataset.lng || "0.0";
    const nombre = mapContainer.dataset.nombre;
    const calle = mapContainer.dataset.calle;

    // Reemplazamos coma por punto para el parseFloat
    const lat = parseFloat(latStr.replace(',', '.'));
    const lng = parseFloat(lngStr.replace(',', '.'));

    // Verificamos que las coordenadas no sean 0.0
    if (lat !== 0.0 && lng !== 0.0) {
        // Inicializar el mapa
        var map = L.map('map').setView([lat, lng], 15);

        // Añadir tiles
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(map);

        // Añadir marcador y popup
        var marker = L.marker([lat, lng]).addTo(map);
        marker.bindPopup(`<b>${nombre}</b><br>${calle}`).openPopup();
    } else {
        // Mensaje de error si no hay coordenadas
        mapContainer.innerHTML = '<p style="text-align:center; padding-top: 150px; color: #7f8c8d;">Ubicación no disponible en el mapa.</p>';
    }
});