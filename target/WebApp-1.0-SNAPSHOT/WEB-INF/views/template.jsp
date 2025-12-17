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
                <button class="nav-toggle" id="navToggle" aria-label="Abrir menú">
                    &#9776;
                </button>

                <div class="nav-menu" id="navMenu">

                    <div class="nav-left">
                        <a href="${pageContext.request.contextPath}/propiedades">Inicio</a>
                        <c:if test="${sessionScope.user.rol == 'ADMIN'}">
                                <a href="${pageContext.request.contextPath}/usuario/admin/usuarios">Gestionar
                                    Usuarios</a>
                                <a href="${pageContext.request.contextPath}/propiedad/admin/lista">Gestionar
                                    Propiedades</a>
                                <a href="${pageContext.request.contextPath}/reserva/admin/lista">Ver Reservas</a>
                        </c:if>
                    </div>

                    <div class="nav-right">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/propiedad/nuevo">Registrar Propiedad</a>

                                    <a href="${pageContext.request.contextPath}/propiedad/mis-propiedades">Mis
                                        Propiedades</a>
                                    <a class="nav-link"
                                        href="${pageContext.request.contextPath}/reserva/mis-reservas">Mis Reservas</a>
                                    <a href="${pageContext.request.contextPath}/usuario/salir" class="nav-link-salir">(Salir)</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/usuario/entrar">Iniciar Sesión</a>
                                <a href="${pageContext.request.contextPath}/usuario/nuevo">Registrarse</a>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </nav>
            <main>
                <jsp:include page="${requestScope.view}" />
            </main>

            <script src="${pageContext.request.contextPath}/static/js/menu.js"></script>
            <script src="${pageContext.request.contextPath}/static/js/validaciones.js"></script>
            <script src="${pageContext.request.contextPath}/static/js/carrusel.js"></script>
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
            <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
            <footer id="footer">
                <p>&copy; 2024 Mi Aplicación de Propiedades. Todos los derechos reservados.</p>
            </footer>

        </html>