<%@page contentType = "text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Formulario usuarios</title>
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
        <p>error</p>
    </body>

</html>