<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Propiedad</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main.css">
</head>
<body>
    <div>
        <h1>Registrar Propiedad</h1>
        <form action="${pageContext.request.contextPath}/propiedad/guardar" method="POST" class="form">
            
            <label for="calle_numero">Calle y Número:</label>
            <input type="text" name="calle_numero" required>
            <br>

            <label for="ciudad">Ciudad:</label>
            <input type="text" name="ciudad" required>
            <br>

            <label for="codigo_postal">Código Postal:</label>
            <input type="text" name="codigo_postal" required>
            <br>

            <label for="nombre">Nombre:</label>
            <input type="text" name="nombre" required>
            <br>

            <label for="precio_habitacion">Precio por Habitación:</label>
            <input type="number" step="0.01" name="precio_habitacion" required>
            <br>

            <label for="habitaciones">Número de Habitaciones:</label>
            <input type="number" name="habitaciones" required>
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
</html>