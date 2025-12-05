<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Propiedades</title>
    <%-- Asumo que usas el mismo CSS principal --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
</head>
<body>
    
    <nav>
        <a href="${pageContext.request.contextPath}/index.html">Inicio</a> | 
        <a href="${pageContext.request.contextPath}/propiedad/nuevo">Registrar Propiedad</a>
    </nav>

    <div class="container">
        <h1>Nuestras Propiedades</h1>

        <%-- Contenedor principal de la rejilla de tarjetas --%>
        <div class="propiedades-grid">
            
            <%-- Verificamos si la lista no está vacía --%>
            <c:if test="${!empty requestScope.propiedades}">
                
                <%-- Iteramos sobre la lista enviada por el controlador --%>
                <c:forEach var="p" items="${requestScope.propiedades}">
                    
                    <div class="propiedad-card">
                        
                        <%-- Contenedor de la imagen --%>
                        <div class="propiedad-img-container">
                            <%-- Placeholder para la imagen (marcador de posición) --%>
                            <img src="${pageContext.request.contextPath}/static/img/placeholder_house.jpg" 
                                 alt="Imagen de ${p.nombre}" 
                                 class="propiedad-img">
                        </div>

                        <%-- Contenido de la tarjeta --%>
                        <div class="propiedad-info">
                            <h3 class="propiedad-titulo">${p.nombre}</h3>
                            
                            <p class="propiedad-ubicacion">
                                <small>${p.ciudad}</small>
                            </p>
                            
                            <div class="propiedad-footer">
                                <span class="propiedad-precio">${p.precio_habitacion} € / noche</span>
                                <%-- Enlace opcional para ver detalles si lo implementas luego --%>
                                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.propiedad_id}" class="btn-detalle">Ver más</a>
                            </div>
                        </div>
                        
                    </div>
                </c:forEach>
            </c:if>

            <%-- Mensaje si no hay propiedades --%>
            <c:if test="${empty requestScope.propiedades}">
                <p class="mensaje-vacio">No hay propiedades registradas en este momento.</p>
            </c:if>
            
        </div>
    </div>
</body>
</html>