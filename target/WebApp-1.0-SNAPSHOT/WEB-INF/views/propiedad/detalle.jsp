<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container detalle-container">
            <div class="propiedad-card detalle-card">
                <c:if test="${!empty requestScope.imagenes}">
                    <div class="carousel">

                        <c:forEach var="f" items="${requestScope.imagenes}" varStatus="status">
                            <div class="carousel-item ${status.first ? 'active' : ''}">
                                <img src="${pageContext.request.contextPath}/${f.getUrl()}" alt="Imagen de ${p.nombre}"
                                    class="carousel-img">
                            </div>
                        </c:forEach>

                        <c:if test="${requestScope.imagenes.size() > 1}">
                            <button class="carousel-btn prev">&#10094;</button>
                            <button class="carousel-btn next">&#10095;</button>
                        </c:if>

                    </div>
                </c:if>
                <c:if test="${empty requestScope.imagenes}">
                    <div class="carousel carousel-empty">
                        <p>Sin imágenes disponibles</p>
                    </div>
                </c:if>
                <div class="propiedad-info">
                    <h1 class="detalle-titulo">${p.nombre}</h1>

                    <p class="propiedad-ubicacion detalle-subtitulo">
                        📍 ${p.calle_numero}, ${p.codigo_postal} ${p.ciudad}
                    </p>
                    <div class="propiedad-features">
                        <span>🛏️ <strong>${p.habitaciones}</strong> Habitaciones</span>
                        <span>🚿 <strong>${p.baños}</strong> Baños</span>
                        <span>💲 <strong>${p.precio_habitacion}</strong> / noche</span>
                    </div>
                    <div class="propiedad-descripcion">
                        <h3>Descripción</h3>
                        <p>${p.descripcion}</p>
                    </div>

                    <div style="margin-bottom: 20px;">
                        <h3>Ubicación</h3>

                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                            integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />

                        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
                            integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>

                        <div id="map" class="mapa-detalle" data-lat="${p.latitud}" data-lng="${p.longitud}"
                            data-nombre="${p.nombre}" data-calle="${p.calle_numero}">
                        </div>

                        <script src="${pageContext.request.contextPath}/static/js/mapa-detalle.js"></script>
                    </div>
                    <div class="propiedad-footer detalle-footer">
                        <div>
                            <small>Publicado por: <strong>${p.propietario.nombre}
                                    ${p.propietario.apellidos}</strong></small>
                        </div>

                        <div class="propiedad-footer-buttons">
                            <c:if test="${empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/usuario/entrar" class="button">Inicia sesión
                                    para reservar</a>
                            </c:if>
                            <c:if test="${not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/reserva/nueva?propiedadId=${p.getPropiedad_id()}"
                                    class="button">Reservar Ahora</a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/propiedades"
                                class="button btn-secondary">Volver</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>