<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <div class="container">
            <h1>Nuestras Propiedades</h1>

            <div class="propiedades-grid">

                <c:if test="${!empty requestScope.propiedades}">

                    <c:forEach var="p" items="${requestScope.propiedades}">

                        <div class="propiedad-card">

                            <div class="propiedad-img-container">
                                <img src="${pageContext.request.contextPath}/static/img/placeholder_house.jpg"
                                    alt="Imagen de ${p.nombre}" class="propiedad-img">
                            </div>

                            <div class="propiedad-info">
                                <h3 class="propiedad-titulo">${p.nombre}</h3>

                                <p class="propiedad-ubicacion">
                                    <small>${p.ciudad}</small>
                                </p>

                                <div class="propiedad-footer">
                                    <span class="propiedad-precio">${p.precio_habitacion} $ / noche</span>
                                    <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.propiedad_id}"
                                        class="btn-detalle">Ver más</a>
                                </div>
                            </div>

                        </div>
                    </c:forEach>
                </c:if>

                <c:if test="${empty requestScope.propiedades}">
                    <p class="mensaje-vacio">No hay propiedades registradas en este momento.</p>
                </c:if>

            </div>
        </div>