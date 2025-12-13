<%@page contentType="text/html" pageEncoding="UTF-8" %>

    <form action="${pageContext.request.contextPath}/foto/guardar" method="POST" class="form"
        enctype="multipart/form-data">
        <label for="imagenes">Fotos de la propiedad:</label>
        <input type="file" name="imagenes" multiple accept="image/*">
        <br>
        <input type="submit" value="Guardar Propiedad" class="button">
    </form>