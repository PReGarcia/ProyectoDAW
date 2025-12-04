<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulario Usuario</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <div>
        <h1>Registrar Nuevo Usuario</h1>
        <form action="/WebApp/usuario/guardar" method="POST" class="form">
            
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
</html>