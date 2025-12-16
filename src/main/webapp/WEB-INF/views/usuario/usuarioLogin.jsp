<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <div class="container">
            <h1 class="text-center">Bienvenido</h1>
            <c:if test="${not empty requestScope.msg}">
                <div class="alert ${requestScope.style == 'error' ? 'alert-error' : 'alert-info'}">
                    ${requestScope.msg}
                </div>
            </c:if>
            <form action="${pageContext.request.contextPath}/usuario/validar" method="POST" class="form">
                <label for="email">Correo Electrónico:</label>
                <input type="email" id="email" name="email" placeholder="usuario@ejemplo.com">
                <br>

                <label for="contra">Contraseña:</label>
                <input type="password" id="contra" name="contra" placeholder="Contraseña">
                <br>

                <input type="submit" value="Entrar" class="button">
            </form>

            <p class="text-center">¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuario/nuevo">Regístrate aquí</a></p>
        </div>