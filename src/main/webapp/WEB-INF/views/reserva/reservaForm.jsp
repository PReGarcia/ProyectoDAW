<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

        <div class="container"
            style="max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">

            <h1 style="text-align: center;">Reservar Propiedad: ${p.getNombre()}</h1>
            <p style="text-align: center; color: #3498db; font-weight: bold;">Precio por noche: 💲
                ${p.getPrecio_habitacion()}</p>

            <c:if test="${not empty requestScope.error}">
                <p
                    style="color: #e74c3c; background: #fbe6e6; padding: 10px; border-radius: 4px; border: 1px solid #e74c3c;">
                    <strong>Error:</strong> ${requestScope.error}
                </p>
            </c:if>

            <form action="${pageContext.request.contextPath}/reserva/guardar" method="post"
                style="display: flex; flex-direction: column; gap: 15px;">

                <%-- AÑADIDO ID para que JavaScript pueda acceder a él --%>
                    <input type="hidden" name="propiedadId" id="propiedadIdHidden" value="${p.getPropiedad_id()}">

                    <div class="form-group">
                        <label for="dateRange" style="font-weight: bold; margin-bottom: 5px; display: block;">Selecciona
                            Fechas de Reserva:</label>
                        <input type="text" id="dateRange" name="dateRange" class="form-control"
                            placeholder="Fecha de Entrada y Salida" required
                            style="padding: 10px; border: 1px solid #ccc; border-radius: 4px; width: 100%; box-sizing: border-box;">

                        <input type="hidden" id="fechaInicio" name="fechaInicio">
                        <input type="hidden" id="fechaFin" name="fechaFin">
                    </div>

                    <button type="submit" class="button"
                        style="background-color: #2ecc71; color: white; padding: 12px; border: none; border-radius: 4px; cursor: pointer;">
                        Confirmar Reserva
                    </button>

                    <a href="${pageContext.request.contextPath}/propiedad/detalle?id=${p.getPropiedad_id()}"
                        class="button"
                        style="background-color: #95a5a6; color: white; padding: 12px; border: none; border-radius: 4px; cursor: pointer; text-align: center;">
                        Cancelar
                    </a>

            </form>

        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const propiedadId = document.getElementById('propiedadIdHidden').value;
                const dateRangeInput = document.getElementById('dateRange');

                // Función para inicializar Flatpickr con o sin fechas deshabilitadas
                function initFlatpickr(disabledDates = []) {
                    flatpickr(dateRangeInput, {
                        mode: "range", // Permite seleccionar un rango
                        minDate: "today", // No permite seleccionar fechas pasadas
                        dateFormat: "Y-m-d", // Formato compatible con Java LocalDate
                        disable: disabledDates, // Deshabilita los rangos de fechas reservadas

                        onChange: function (selectedDates, dateStr, instance) {
                            // Si se seleccionan dos fechas, actualiza los campos ocultos
                            if (selectedDates.length === 2) {
                                const [inicio, fin] = selectedDates.sort((a, b) => a - b);

                                // Solo actualiza si el rango es válido (inicio < fin)
                                if (inicio && fin && inicio.getTime() < fin.getTime()) {
                                    document.getElementById('fechaInicio').value = instance.formatDate(inicio, "Y-m-d");
                                    document.getElementById('fechaFin').value = instance.formatDate(fin, "Y-m-d");
                                } else {
                                    // Limpiar si no hay rango completo o el rango es inválido (ej: la misma fecha)
                                    document.getElementById('fechaInicio').value = '';
                                    document.getElementById('fechaFin').value = '';
                                }
                            } else {
                                // Limpiar si no hay rango completo
                                document.getElementById('fechaInicio').value = '';
                                document.getElementById('fechaFin').value = '';
                            }
                        }
                    });
                }

                // 1. Obtener fechas reservadas vía AJAX
                if (propiedadId) {
                    fetch('${pageContext.request.contextPath}/reserva/api/fechas-reservadas?propiedadId=' + propiedadId)
                        .then(response => {
                            if (!response.ok) {
                                console.error('Error al obtener las fechas reservadas: ' + response.statusText);
                                return [];
                            }
                            return response.json();
                        })
                        .then(disabledRanges => {
                            // disabledRanges es un array de objetos {from: '...', to: '...'}
                            // 2. Inicializar Flatpickr con los rangos deshabilitados
                            initFlatpickr(disabledRanges);
                        })
                        .catch(error => {
                            console.error('Error al procesar las fechas reservadas:', error);
                            // Si falla el fetch, inicializa el calendario sin deshabilitar fechas
                            initFlatpickr([]);
                        });
                } else {
                    // Inicializar Flatpickr normalmente si no hay ID de propiedad
                    initFlatpickr([]);
                }
            });
        </script>