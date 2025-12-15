<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container" style="max-width: 800px; margin: 0 auto; padding: 20px;">

           <div class="propiedad-card" style="box-shadow: none; border: 1px solid #ddd;">

                <c:if test="${!empty requestScope.imagenes}">
                    <div class="carousel">
                        
                        <c:forEach var="f" items="${requestScope.imagenes}" varStatus="status">
                            <div class="carousel-item ${status.first ? 'active' : ''}">
                                <img src="${pageContext.request.contextPath}/${f.getUrl()}"
                                     alt="Imagen de ${p.nombre}" class="carousel-img">
                            </div>
                        </c:forEach>

                        <c:if test="${requestScope.imagenes.size() > 1}">
                            <button class="carousel-btn prev">&#10094;</button>
                            <button class="carousel-btn next">&#10095;</button>
                        </c:if>
                        
                    </div>
                </c:if>
                <c:if test="${empty requestScope.imagenes}">
                     <div class="carousel" style="display:flex; align-items:center; justify-content:center; color:#7f8c8d;">
                        <p>Sin imágenes disponibles</p>
                    </div>
                </c:if>
                <div class="propiedad-info"> 
                    <h1 style="color: #2c3e50; font-size: 2rem;">${p.nombre}</h1>

                    <p class="propiedad-ubicacion" style="font-size: 1.2rem;">
                        📍 ${p.calle_numero}, ${p.codigo_postal} ${p.ciudad}
                    </p>

                    <div
                        style="display: flex; gap: 20px; margin: 20px 0; background: #f4f7f6; padding: 15px; border-radius: 5px;">
                        <span>🛏️ <strong>${p.habitaciones}</strong> Habitaciones</span>
                        <span>🚿 <strong>${p.baños}</strong> Baños</span>
                        <span>💲 <strong>${p.precio_habitacion}</strong> / noche</span>
                    </div>

                    <div class="descripcion" style="margin-bottom: 20px;">
                        <h3>Descripción</h3>
                        <p>${p.descripcion}</p>
                    </div>

                    <div style="margin-bottom: 20px;">
                        <h3>Ubicación</h3>

                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                            integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />

                        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
                            integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>

                        <div id="map"
                            style="height: 350px; width: 100%; border-radius: 5px; border: 1px solid #ddd; z-index: 1;">
                        </div>

                        <script>
                            // Esperamos a que el DOM esté cargado
                            document.addEventListener("DOMContentLoaded", function () {

                                // Obtenemos las coordenadas desde el JSP

                                var lat = parseFloat("${p.latitud}".replace(',', '.'));
                                var lng = parseFloat("${p.longitud}".replace(',', '.'));

                                // Verificamos que las coordenadas no sean 0.0 (valor por defecto de double)
                                if (lat !== 0.0 && lng !== 0.0) {

                                    // Inicializar el mapa centrado en las coordenadas
                                    var map = L.map('map').setView([lat, lng], 15);

                                    // Añadir la capa de tiles de OpenStreetMap
                                    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                        maxZoom: 19,
                                        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                                    }).addTo(map);

                                    // Añadir el marcador (pin)
                                    var marker = L.marker([lat, lng]).addTo(map);

                                    // Añadir un popup con el nombre de la propiedad
                                    marker.bindPopup("<b>${p.nombre}</b><br>${p.calle_numero}").openPopup();

                                } else {
                                    // Si no hay coordenadas válidas, mostramos un mensaje o una ubicación por defecto
                                    document.getElementById('map').innerHTML = '<p style="text-align:center; padding-top: 150px; color: #7f8c8d;">Ubicación no disponible en el mapa.</p>';
                                }
                            });
                        </script>
                    </div>

                    <div class="propiedad-footer"
                        style="border-top: 1px solid #eee; padding-top: 20px; display: flex; justify-content: space-between;">
                        <div>
                            <small>Publicado por: <strong>${p.propietario.nombre}
                                    ${p.propietario.apellidos}</strong></small>
                        </div>

                        <div>
                            <a href="${pageContext.request.contextPath}/propiedades" class="button"
                                style="background-color: #95a5a6; margin-right: 10px;">Volver</a>
                            <c:if test="${not empty sessionScope.user}">
                                <button class="button" onclick="alert('Funcionalidad de reserva próximamente')">Reservar
                                    Ahora</button>
                            </c:if>
                            <c:if test="${empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/usuario/entrar" class="button">Inicia sesión
                                    para reservar</a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>