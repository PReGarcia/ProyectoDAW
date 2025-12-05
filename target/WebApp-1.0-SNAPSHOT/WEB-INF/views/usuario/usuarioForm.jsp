<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
        <div>
            <h1>Registrar Nuevo Usuario</h1>
            <form action="${pageContext.request.contextPath}/usuario/guardar" method="POST" class="form">

                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" required placeholder="Nombre">
                <br>

                <label for="apellidos">Apellidos:</label>
                <input type="text" id="apellidos" name="apellidos" required placeholder="Apellidos">
                <br>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required placeholder="ejemplo@correo.com">
                <br>

                <label for="contra">Contraseña:</label>
                <input type="password" id="contra" name="contra" required placeholder="Contraseña">
                <br>

                <input type="submit" value="Guardar Usuario" class="button">
            </form>

        </div>
    </body>

    <script src="${pageContext.request.contextPath}/static/js/menu.js"></script>
    <footer id="footer">
        <p>&copy; 2024 Mi Aplicación de Propiedades. Todos los derechos reservados.</p> 
    </footer>
</html>