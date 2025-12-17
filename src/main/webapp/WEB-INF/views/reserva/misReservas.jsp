<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <div class="container container-narrow">
                <h1>Mis Reservas</h1>

                <c:choose>
                    <c:when test="${not empty misReservas}">
                        <div class="table-responsive">
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
                                                <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${reserva.propiedad.propiedad_id}"
                                                    title="Ver detalle de la propiedad">
                                                    ${reserva.propiedad.nombre}
                                                </a>
                                            </td>
                                            <td>${reserva.propiedad.ciudad}</td>
                                            <td>${reserva.fecha_inicio}</td>
                                            <td>${reserva.fecha_fin}</td>
                                            <td>
                                                <fmt:formatNumber value="${reserva.precio_total}" type="currency"
                                                    currencySymbol="€" />
                                            </td>
                                            <td>
                                                <span
                                                    class="badge badge-${reserva.estado eq 'CONFIRMADA' ? 'success' : 'pending'}">
                                                    ${reserva.estado}
                                                </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.getContextPath()}/reserva/cancelar?id=${reserva.reserva_id}"
                                                    onclick="return confirm('¿Estás seguro de que deseas cancelar esta reserva?');"
                                                    class="button button-sm button-danger">Cancelar</a>
                                            </td>
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
                            <a href="${pageContext.request.contextPath}/propiedades" class="button">Buscar
                                Propiedades</a>
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>