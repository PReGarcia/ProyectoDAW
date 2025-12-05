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
                                    <a href="${pageContext.request.contextPath}/usuario/perfil">Hola,
                                        ${sessionScope.user.nombre}</a>
                                    <a href="${pageContext.request.contextPath}/usuario/salir"
                                        style="color: #e74c3c;">(Salir)</a>
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
            <h1>Registrar Propiedad</h1>
            <form action="${pageContext.request.contextPath}/propiedad/guardar" method="POST" class="form">
                <label for="nombre">Nombre:</label>
                <input type="text" name="nombre" required>
                <br>

                <label for="descripcion">Descripción:</label>
                <input type="text" name="descripcion">
                <br>
                <label for="calle_numero">Calle y Número:</label>
                <input type="text" name="calle_numero" required>
                <br>

                <label for="ciudad">Ciudad:</label>
                <input type="text" name="ciudad" required>
                <br>

                <label for="codigo_postal">Código Postal:</label>
                <input type="text" name="codigo_postal" required>
                <br>

                <label for="habitaciones">Número de Habitaciones:</label>
                <input type="number" name="habitaciones" required>
                <br>

                <label for="precio_habitacion">Precio por Habitación:</label>
                <input type="number" step="0.01" name="precio_habitacion" required>
                <br>

                <label for="banos">Número de Baños:</label>
                <input type="number" name="banos" required>
                <br>

                <label for="latitud">Latitud:</label>
                <input type="number" step="any" name="latitud" required>
                <br>

                <label for="longitud">Longitud:</label>
                <input type="number" step="any" name="longitud" required>
                <br>

                <input type="submit" value="Guardar Propiedad" class="button">
            </form>
        </div>
    </body>

    
    <script src="${pageContext.request.contextPath}/static/js/menu.js"></script>

    <footer id="footer">
        <p>&copy; 2024 Mi Aplicación de Propiedades. Todos los derechos reservados.</p> 
    </footer>
    </html>