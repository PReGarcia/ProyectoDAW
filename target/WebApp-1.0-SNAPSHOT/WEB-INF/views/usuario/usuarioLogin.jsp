<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
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