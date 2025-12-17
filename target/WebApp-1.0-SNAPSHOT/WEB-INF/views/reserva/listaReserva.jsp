<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container">
    <h1 class="text-center">Gestión de Reservas (Administrador)</h1>

    <c:if test="${empty requestScope.reservas}">
        <div class="mensaje-vacio">
            <p>No hay reservas registradas en el sistema.</p>
        </div>
    </c:if>

    <c:if test="${not empty requestScope.reservas}">
        <div class="table-responsive"> 
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Propiedad</th>
                        <th>Reservado por</th>
                        <th>Inicio</th>
                        <th>Fin</th>
                        <th>Precio Total</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reserva" items="${requestScope.reservas}">
                        <tr>
                            <td>${reserva.reserva_id}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${reserva.propiedad.propiedad_id}">
                                    ${reserva.propiedad.nombre}
                                </a>
                            </td>
                            <td>${reserva.usuario.nombre} ${reserva.usuario.apellidos}</td>
                            <td>${reserva.fecha_inicio}</td>
                            <td>${reserva.fecha_fin}</td>
                            <td>${reserva.precio_total} €</td>
                            <td>
                                <span class="badge 
                                    <c:choose>
                                        <c:when test="${reserva.estado eq 'PENDIENTE'}">badge-warning</c:when>
                                        <c:when test="${reserva.estado eq 'CONFIRMADA'}">badge-success</c:when>
                                        <c:otherwise>badge-danger</c:otherwise>
                                    </c:choose>
                                ">
                                    ${reserva.estado}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.getContextPath()}/reserva/cancelar?id=${reserva.reserva_id}" onclick="return confirm('¿Estás seguro de que deseas cancelar esta reserva?');" 
                                   class="button button-sm button-danger">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
    
    </div>