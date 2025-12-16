<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container container-narrow">
    <h1>Mis Reservas</h1>
    
    <c:choose>
        <c:when test="${not empty misReservas}">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Propiedad</th>
                        <th>Ubicación</th>
                        <th>Check-in</th>
                        <th>Check-out</th>
                        <th>Precio Total</th>
                        <th>Estado</th>
                        <th>Acciones</th> 
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="reserva" items="${misReservas}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${reserva.propiedad.propiedad_id}" title="Ver detalle de la propiedad">
                                    ${reserva.propiedad.nombre}
                                </a>
                            </td>
                            <td>${reserva.propiedad.ciudad}</td>
                            <td>${reserva.fecha_inicio}</td>
                            <td>${reserva.fecha_fin}</td>
                            <td>
                                <fmt:formatNumber value="${reserva.precio_total}" type="currency" currencySymbol="€"/>
                            </td>
                            <td>
                                <span class="badge badge-${reserva.estado eq 'CONFIRMADA' ? 'success' : 'pending'}">
                                    ${reserva.estado}
                                </span>
                            </td>
                            <c:if test="${reserva.estado eq 'CONFIRMADA'}">
                        <form action="${pageContext.request.contextPath}/reserva/cancelar" method="POST"
                              onsubmit="return confirm('¿Está seguro de que desea cancelar la reserva de la propiedad ${reserva.propiedad.nombre}? Esta acción es irreversible.');">
                            
                            <input type="hidden" name="id" value="${reserva.id}" />
                            <button type="submit" class="btn btn-danger btn-sm">Cancelar</button>
                        </form>
                    </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info" role="alert">
                Aún no tienes ninguna reserva activa.
            </div>
            <p>
                <a href="${pageContext.request.contextPath}/propiedad/lista" class="btn btn-primary">Buscar Propiedades</a>
            </p>
        </c:otherwise>
    </c:choose>
</div>
