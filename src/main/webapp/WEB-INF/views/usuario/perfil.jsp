<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container" style="max-width: 800px; margin: 0 auto;">
    <h1>Mi Perfil</h1>

    <div class="propiedad-card" style="padding: 20px; margin-bottom: 30px;">
        <div style="display: flex; align-items: center; gap: 20px;">
            <div style="background-color: #3498db; color: white; width: 80px; height: 80px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem;">
                ${sessionScope.user.nombre.charAt(0)}
            </div>
            
            <div>
                <h2>${sessionScope.user.nombre} ${sessionScope.user.apellidos}</h2>
                <p><strong>Email:</strong> ${sessionScope.user.email}</p>
                <p><strong>Rol:</strong> <span style="background: #eee; padding: 2px 8px; border-radius: 4px; font-size: 0.9em;">${sessionScope.user.rol}</span></p>
            </div>
        </div>
        
        <div style="margin-top: 20px; text-align: right;">
             <a href="${pageContext.request.contextPath}/usuario/salir" class="button" style="background-color: #e74c3c;">Cerrar Sesión</a>
        </div>
    </div>

    <hr style="border: 0; border-top: 1px solid #ddd; margin: 30px 0;">
    
    <div style="text-align: center; color: #7f8c8d;">
        <h3>Mis Reservas</h3>
        <p>No tienes reservas activas en este momento.</p>
        <a href="${pageContext.request.contextPath}/propiedades" style="color: #3498db;">Explorar propiedades</a>
    </div>
</div>