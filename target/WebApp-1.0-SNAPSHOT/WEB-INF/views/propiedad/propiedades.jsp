<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lista de Propiedades</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
    </head>
    <body>

        <nav class="navbar">
            <%-- 1. BOTÓN HAMBURGUESA (Solo visible en móvil) --%>
            <button class="nav-toggle" id="navToggle" aria-label="Abrir menú">
                &#9776; <%-- Símbolo de tres líneas --%>
            </button>

            <%-- 2. CONTENEDOR DE ENLACES (Lo que se ocultará/mostrará) --%>
            <div class="nav-menu" id="navMenu">

                <div class="nav-left">
                    <a href="${pageContext.request.contextPath}/propiedades">Inicio</a>
                </div>

                <div class="nav-right">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/propiedad/nuevo">Registrar Propiedad</a>
                            <a href="${pageContext.request.contextPath}/usuario/perfil">Hola, ${sessionScope.user.nombre}</a>
                            <a href="${pageContext.request.contextPath}/usuario/salir" style="color: #e74c3c;">(Salir)</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/usuario/entrar">Iniciar Sesión</a>
                            <a href="${pageContext.request.contextPath}/usuario/nuevo">Registrarse</a>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </nav>

        <div class="container">
            <h1>Nuestras Propiedades</h1>

            <div class="propiedades-grid">

                <c:if test="${!empty requestScope.propiedades}">

                    <c:forEach var="p" items="${requestScope.propiedades}">

                        <div class="propiedad-card">

                            <div class="propiedad-img-container">
                                <img src="${pageContext.request.contextPath}/static/img/placeholder_house.jpg" 
                                     alt="Imagen de ${p.nombre}" 
                                     class="propiedad-img">
                            </div>

                            <div class="propiedad-info">
                                <h3 class="propiedad-titulo">${p.nombre}</h3>

                                <p class="propiedad-ubicacion">
                                    <small>${p.ciudad}</small>
                                </p>

                                <div class="propiedad-footer">
                                    <span class="propiedad-precio">${p.precio_habitacion} $ / noche</span>
                                    <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.propiedad_id}" class="btn-detalle">Ver más</a>
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
    </body>
</html>