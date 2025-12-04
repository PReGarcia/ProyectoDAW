<%page contentType="text/html" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Formulario Usuario</title>
        <link rel="stylesheet" href="/css/main.css">
    </head>

    <body>
        <div class="container">
            <h1>Bienvenido</h1>

            <c:if test="${not empty error}">
                <div style="color: red; margin-bottom: 10px;">
                    ${error}
                </div>
            </c:if>

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

    </html>