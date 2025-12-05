<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Formulario Usuario</title>
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
            <h1>Bienvenido</h1>

            <form action="${pageContext.request.contextPath}/usuario/validar" method="POST" class="form">
                <label for="email">Correo Electrónico:</label>
                <input type="email" id="email" name="email" required placeholder="usuario@ejemplo.com">
                <br>

                <label for="contra">Contraseña:</label>
                <input type="password" id="contra" name="contra" required placeholder="Contraseña">
                <br>

                <input type="submit" value="Entrar" class="button">
            </form>

            <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/nuevo">Regístrate aquí</a></p>
        </div>
    </body>

    <script src="${pageContext.request.contextPath}/static/js/menu.js"></script>
    <footer id="footer">
        <p>&copy; 2024 Mi Aplicación de Propiedades. Todos los derechos reservados.</p> 
    </footer>
</html>