<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container reserva-container">
            <h1 class="text-center">Reservar Propiedad: ${p.getNombre()}</h1>
            <p class="reserva-price">Precio por noche: 💲 ${p.getPrecio_habitacion()}</p>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-error">
                    <strong>Error:</strong> ${requestScope.error}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/reserva/guardar" method="post" class="form-reserva">
                <input type="hidden" name="propiedadId" id="propiedadIdHidden" value="${p.getPropiedad_id()}">

                <div class="form-group">
                    <label for="dateRange">Selecciona Fechas de Reserva:</label>
                    <input type="text" id="dateRange" name="dateRange" class="form-control"
                        placeholder="Fecha de Entrada y Salida" required
                        data-api-url="${pageContext.request.contextPath}/reserva/api/fechas-reservadas">



                    <input type="hidden" id="fechaInicio" name="fechaInicio">
                    <input type="hidden" id="fechaFin" name="fechaFin">
                </div>

                <button type="submit" class="button button-success">
                    Confirmar Reserva
                </button>

                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.getPropiedad_id()}"
                    class="button button-secondary text-center">
                    Cancelar
                </a>

            </form>

        </div>
        <script src="${pageContext.request.contextPath}/static/js/reservaForm.js"></script>