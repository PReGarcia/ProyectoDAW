<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container">
            <h1>Mis Propiedades Publicadas</h1>

            <div class="propiedades-grid">

                <c:if test="${!empty requestScope.propiedades}">
                    <c:forEach var="p" items="${requestScope.propiedades}">
                        <div class="propiedad-card">
                            <div class="propiedad-img-container">
                                <img src="${pageContext.request.contextPath}/${p.getPortada()}"
                                    alt="Imagen de ${p.nombre}" class="propiedad-img">
                            </div>
                            <div class="propiedad-info">
                                <h3 class="propiedad-titulo">${p.nombre}</h3>
                                <p class="propiedad-ubicacion">
                                    <small>${p.ciudad}</small>
                                </p>
                                <div class="propiedad-footer"
                                    style="flex-direction: column; gap: 10px; align-items: stretch;">
                                    <span class="propiedad-precio" style="text-align: center;">${p.precio_habitacion} $
                                        / noche</span>

                                    <div style="display: flex; gap: 5px; justify-content: space-between;">
                                        <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.propiedad_id}"
                                            class="button"
                                            style="flex: 1; text-align: center; font-size: 0.9rem;">Ver</a>

                                        <a href="${pageContext.request.contextPath}/propiedad/editar?id=${p.propiedad_id}"
                                            class="button"
                                            style="background-color: #f1c40f; flex: 1; text-align: center; font-size: 0.9rem; color: #fff;">Editar</a>

                                        <a href="${pageContext.request.contextPath}/propiedad/eliminar?id=${p.propiedad_id}"
                                            class="button"
                                            style="background-color: #e74c3c; flex: 1; text-align: center; font-size: 0.9rem; color: #fff;"
                                            onclick="return confirm('¿Estás seguro de que quieres eliminar esta propiedad?');">Borrar</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>

                <c:if test="${empty requestScope.propiedades}">
                    <div style="text-align: center; grid-column: 1 / -1;">
                        <p class="mensaje-vacio">Aún no has registrado ninguna propiedad.</p>
                        <a href="${pageContext.request.contextPath}/propiedad/nuevo" class="button"
                            style="margin-top: 10px;">Publicar mi primera propiedad</a>
                    </div>
                </c:if>

            </div>
        </div>