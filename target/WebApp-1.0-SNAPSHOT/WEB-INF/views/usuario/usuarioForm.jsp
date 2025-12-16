<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <div>
        <h1>Registrar Nuevo Usuario</h1>
        <form action="${pageContext.request.contextPath}/usuario/guardar" method="POST" class="form">

            <label for="nombre">Nombre:</label>
            <input type="text" id="nombre" name="nombre" placeholder="Nombre">
            <br>

            <label for="apellidos">Apellidos:</label>
            <input type="text" id="apellidos" name="apellidos" placeholder="Apellidos">
            <br>

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" placeholder="ejemplo@correo.com">
            <br>

            <c:if test="${sessionScope.user.rol == 'ADMIN'}">
                <label for="rol">Rol de Usuario:</label>
                <select name="rol" id="rol" class="form-control"
                    style="width: 100%; padding: 8px; margin-bottom: 10px;">
                    <option value="USER" ${u.rol=='USER' ? 'selected' : '' }>Usuario Normal</option>
                    <option value="PROP" ${u.rol=='PROP' ? 'selected' : '' }>Propietario</option>
                    <option value="ADMIN" ${u.rol=='ADMIN' ? 'selected' : '' }>Administrador</option>
                </select>
                <br>
            </c:if>

            <label for="contra">Contraseña:</label>
            <input type="password" id="contra" name="contra" placeholder="Contraseña">
            <br>

            <input type="submit" value="Guardar Usuario" class="button">
        </form>

    </div>