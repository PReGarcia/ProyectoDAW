<%@page contentType = "text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Formulario usuarios</title>
    <link rel="stylesheet" href="../../static/css/main.css">
</head>

<body>
    <div>
        <h1>AÃ±adir un producto</h1>
        <form action="/cliente/nuevoCliente" method="POST" class="form">
            <label for="nombre">Nombre</label>
            <input type="text" name="nombre" placeholder="Nombre">
            <label for="apellidos">Apellidos</label>
            <input type="text" name="apellidos" placeholder="Apellidos">
            <label for="email">Email</label>
            <input type="email" name="email" placeholder="Email">
            <label for="password">ContraseÃ±a</label>
            <input type="password" name="password" placeholder="ContraseÃ±a">
            <input type="submit" name="submit" value="Enviar" class="button">
        </form>
    </div>
</body>

</html>