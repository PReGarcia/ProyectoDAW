<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container">
            <h1 class="text-center">Gestión de Propiedades (Admin)</h1>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Ciudad</th>
                            <th>Precio</th>
                            <th>Propietario</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${requestScope.propiedades}">
                            <tr>
                                <td>${p.getPropiedad_id()}</td>
                                <td>${p.getNombre()}</td>
                                <td>${p.getCiudad()}</td>
                                <td>${p.getPrecio_habitacion()} $</td>
                                <td>
                                    ${p.getPropietario().getNombre()} <br>
                                    <small>(${p.getPropietario().getEmail()})</small>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.getPropiedad_id()}"
                                        class="button button-sm button-info">Ver</a>

                                    <a href="${pageContext.request.contextPath}/propiedad/eliminar?id=${p.getPropiedad_id()}"
                                        class="button button-danger button-sm"
                                        onclick="return confirm('¿Seguro que quieres borrar esta propiedad de la base de datos?');">
                                        Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty requestScope.propiedades}">
                            <tr>
                                <td colspan="6" class="text-center">No hay propiedades registradas.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>