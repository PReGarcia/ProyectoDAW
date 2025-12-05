<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Formulario Usuario</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/WEB-INF/static/css/main.css">
    </head>

    <body>
        <div class="container">
            <h1>Bienvenido</h1>

            <form action="${pageContext.request.contextPath}/usuario/validar" method="POST" class="form">
                <label for="email">Correo ElectrÃ³nico:</label>
                <input type="email" id="email" name="email" required placeholder="usuario@ejemplo.com">
                <br>

                <label for="contra">ContraseÃ±a:</label>
                <input type="password" id="contra" name="contra" required placeholder="ContraseÃ±a">
                <br>

                <input type="submit" value="Entrar" class="button">
            </form>

            <p>Â¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/nuevo">RegÃ­strate aquÃ­</a></p>
        </div>
    </body>

    </html>