<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container">
            <h1>Gestión de Usuarios</h1>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Email</th>
                            <th>Rol</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${requestScope.usuarios}">
                            <tr>
                                <td>${u.getUsuario_id()}</td>
                                <td>${u.getNombre()}</td>
                                <td>${u.getEmail()}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.getRol() == 'ADMIN'}">
                                            <span class="badge badge-admin">ADMIN</span>
                                        </c:when>
                                        <c:when test="${u.getRol() == 'PROP'}">
                                            <span class="badge badge-prop">Propietario</span>
                                        </c:when>
                                        <c:otherwise>
                                            Usuario
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/usuario/admin/eliminar?id=${u.usuario_id}"
                                        class="button button-danger button-sm"
                                        onclick="return confirm('¿Estás seguro de que quieres eliminar a este usuario?');">
                                        Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty requestScope.usuarios}">
                            <tr>
                                <td colspan="5" style="text-align: center;">No se encontraron usuarios.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>