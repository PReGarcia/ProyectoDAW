<%@page contentType="text/html" pageEncoding="UTF-8" %>
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

                <label for="contra">Contraseña:</label>
                <input type="password" id="contra" name="contra" placeholder="Contraseña">
                <br>

                <input type="submit" value="Guardar Usuario" class="button">
            </form>

        </div>
