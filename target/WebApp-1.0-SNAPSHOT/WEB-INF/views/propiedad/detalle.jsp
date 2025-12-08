<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container" style="max-width: 800px; margin: 0 auto; padding: 20px;">
    
    <div class="propiedad-card" style="box-shadow: none; border: 1px solid #ddd;">
        
        <div class="propiedad-img-container" style="height: 400px;">
            <img src="${pageContext.request.contextPath}/static/img/placeholder_house.jpg" 
                 alt="Imagen de ${p.nombre}" 
                 class="propiedad-img">
        </div>

        <div class="propiedad-info">
            <h1 style="color: #2c3e50; font-size: 2rem;">${p.nombre}</h1>
            
            <p class="propiedad-ubicacion" style="font-size: 1.2rem;">
                📍 ${p.calle_numero}, ${p.codigo_postal} ${p.ciudad}
            </p>

            <div style="display: flex; gap: 20px; margin: 20px 0; background: #f4f7f6; padding: 15px; border-radius: 5px;">
                <span>🛏️ <strong>${p.habitaciones}</strong> Habitaciones</span>
                <span>🚿 <strong>${p.baños}</strong> Baños</span>
                <span>💲 <strong>${p.precio_habitacion}</strong> / noche</span>
            </div>

            <div class="descripcion" style="margin-bottom: 20px;">
                <h3>Descripción</h3>
                <p>${p.descripcion}</p>
            </div>
            
            <div style="margin-bottom: 20px;">
                 <a href="https://www.google.com/maps/search/?api=1&query=${p.latitud},${p.longitud}" 
                    target="_blank" 
                    style="color: #3498db; text-decoration: underline;">
                    Ver en el mapa
                 </a>
            </div>

            <div class="propiedad-footer" style="border-top: 1px solid #eee; padding-top: 20px; display: flex; justify-content: space-between;">
                <div>
                    <small>Publicado por: <strong>${p.propietario.nombre} ${p.propietario.apellidos}</strong></small>
                </div>
                
                <div>
                    <a href="${pageContext.request.contextPath}/propiedades" class="button" style="background-color: #95a5a6; margin-right: 10px;">Volver</a>
                    <c:if test="${not empty sessionScope.user}">
                        <button class="button" onclick="alert('Funcionalidad de reserva próximamente')">Reservar Ahora</button>
                    </c:if>
                    <c:if test="${empty sessionScope.user}">
                         <a href="${pageContext.request.contextPath}/usuario/entrar" class="button">Inicia sesión para reservar</a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>