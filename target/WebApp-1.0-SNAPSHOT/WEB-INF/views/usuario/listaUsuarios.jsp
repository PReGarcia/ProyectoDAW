<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container">
    <h1>Gestión de Usuarios</h1>
    
    <table class="table">
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
                                <span style="color: red; font-weight: bold;">ADMIN</span>
                            </c:when>
                            <c:when test="${u.getRol() == 'PROP'}">
                                <span style="color: green;">Propietario</span>
                            </c:when>
                            <c:otherwise>
                                Usuario
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/usuario/admin/eliminar?id=${u.usuario_id}" 
                           class="button" 
                           style="background-color: #e74c3c; padding: 5px 10px; font-size: 0.8rem;"
                           onclick="return confirm('¿Estás seguro de que quieres eliminar a este usuario?');">
                           Eliminar
                        </a>
                    </td>
                </tr>
            </c:forEach>
            
            <c:if test="${empty requestScope.usuarios}">
                <tr><td colspan="5" style="text-align: center;">No se encontraron usuarios.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>