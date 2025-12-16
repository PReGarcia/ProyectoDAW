<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container">
    <h1 style="text-align: center;">Gestión de Reservas (Administrador)</h1>

    <c:if test="${empty requestScope.reservas}">
        <div class="no-content" style="text-align: center; margin-top: 40px; color: #7f8c8d;">
            <p>No hay reservas registradas en el sistema.</p>
        </div>
    </c:if>

    <c:if test="${not empty requestScope.reservas}">
        <div class="table-container" style="overflow-x: auto;">
            <table class="data-table" style="width: 100%; border-collapse: collapse; margin-top: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                <thead>
                    <tr style="background-color: #3498db; color: white;">
                        <th style="padding: 12px; text-align: left;">ID</th>
                        <th style="padding: 12px; text-align: left;">Propiedad</th>
                        <th style="padding: 12px; text-align: left;">Reservado por</th>
                        <th style="padding: 12px; text-align: left;">Inicio</th>
                        <th style="padding: 12px; text-align: left;">Fin</th>
                        <th style="padding: 12px; text-align: right;">Precio Total</th>
                        <th style="padding: 12px; text-align: center;">Estado</th>
                        <th style="padding: 12px; text-align: center;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reserva" items="${requestScope.reservas}">
                        <tr style="border-bottom: 1px solid #ddd;">
                            <td style="padding: 10px;">${reserva.reserva_id}</td>
                            <td style="padding: 10px;">
                                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${reserva.propiedad.propiedad_id}" 
                                   style="color: #3498db; text-decoration: none;">
                                    ${reserva.propiedad.nombre}
                                </a>
                            </td>
                            <td style="padding: 10px;">${reserva.usuario.nombre} ${reserva.usuario.apellidos}</td>
                            <td style="padding: 10px;">${reserva.fecha_inicio}</td>
                            <td style="padding: 10px;">${reserva.fecha_fin}</td>
                            <td style="padding: 10px; text-align: right;">${reserva.precio_total} €</td>
                            <td style="padding: 10px; text-align: center;">
                                <span class="tag-status 
                                    <c:choose>
                                        <c:when test="${reserva.estado eq 'PENDIENTE'}">tag-warning</c:when>
                                        <c:when test="${reserva.estado eq 'CONFIRMADA'}">tag-success</c:when>
                                        <c:otherwise>tag-error</c:otherwise>
                                    </c:choose>
                                ">
                                    ${reserva.estado}
                                </span>
                            </td>
                            <td style="padding: 10px; text-align: center;">
                                <a href="#" onclick="alert('Confirmar reserva ID: ${reserva.reserva_id}')" 
                                   class="button-small button-success" style="font-size: 0.8em; padding: 5px 10px; margin: 2px;">Confirmar</a>
                                <a href="#" onclick="alert('Cancelar reserva ID: ${reserva.reserva_id}')" 
                                   class="button-small button-error" style="font-size: 0.8em; padding: 5px 10px; margin: 2px; background-color: #e74c3c;">Cancelar</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <style>
        .data-table tbody tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .data-table tbody tr:hover {
            background-color: #f1f1f1;
        }
        .tag-status {
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: bold;
            color: white;
            display: inline-block;
            min-width: 80px;
        }
        .tag-warning { background-color: #f39c12; } /* Amarillo/Naranja para PENDIENTE */
        .tag-success { background-color: #2ecc71; } /* Verde para CONFIRMADA */
        .tag-error { background-color: #e74c3c; } /* Rojo para CANCELADA/RECHAZADA */
    </style>
</div>