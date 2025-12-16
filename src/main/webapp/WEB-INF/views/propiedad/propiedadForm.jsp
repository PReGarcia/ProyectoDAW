<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container">
    <h1 class="text-center">${empty p ? 'Registrar Propiedad' : 'Editar Propiedad'}</h1>
    
    <form action="${pageContext.request.contextPath}/propiedad/guardar" method="POST" class="form" enctype="multipart/form-data">
        
        <input type="hidden" name="id" value="${p.propiedad_id}">

        <label for="nombre">Nombre:</label>
        <input type="text" name="nombre" value="${p.nombre}" required>
        <br>

        <label for="descripcion">Descripción:</label>
        <input type="text" name="descripcion" value="${p.descripcion}">
        <br>
        
        <label for="calle_numero">Calle y Número:</label>
        <input type="text" name="calle_numero" value="${p.calle_numero}" required>
        <br>

        <label for="ciudad">Ciudad:</label>
        <input type="text" name="ciudad" value="${p.ciudad}" required>
        <br>

        <label for="codigo_postal">Código Postal:</label>
        <input type="text" name="codigo_postal" value="${p.codigo_postal}" required>
        <br>

        <label for="habitaciones">Número de Habitaciones:</label>
        <input type="number" name="habitaciones" value="${p.habitaciones}" required>
        <br>

        <label for="precio_habitacion">Precio por Habitación:</label>
        <input type="number" step="0.01" name="precio_habitacion" value="${p.precio_habitacion}" required>
        <br>

        <label for="banos">Número de Baños:</label>
        <input type="number" name="banos" value="${p.baños}" required>
        <br>
        
        <label for="portada">Foto de portada ${not empty p ? '(Dejar vacío para mantener la actual)' : ''}:</label>
        <input type="file" name="portada" accept="image/*" ${empty p ? 'required' : ''} class="form-control-file">
        <br>
        
        <label for="imagenes">Fotos extra (se añadirán a las existentes):</label>
        <input type="file" name="imagenes" multiple accept="image/*" class="form-control-file">
        <br>

        <label for="latitud">Latitud:</label>
        <input type="number" step="any" name="latitud" value="${p.latitud}" required>
        <br>

        <label for="longitud">Longitud:</label>
        <input type="number" step="any" name="longitud" value="${p.longitud}" required>
        <br>

        <input type="submit" value="${empty p ? 'Guardar Propiedad' : 'Actualizar Propiedad'}" class="button">
    </form>
</div>
<script src="${pageContext.request.contextPath}/static/js/validaciones.js"></script>