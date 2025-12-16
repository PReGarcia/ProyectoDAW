document.addEventListener('DOMContentLoaded', function () {
    const propiedadIdInput = document.getElementById('propiedadIdHidden');
    const dateRangeInput = document.getElementById('dateRange');
    const fechaInicioInput = document.getElementById('fechaInicio');
    const fechaFinInput = document.getElementById('fechaFin');

    if (!dateRangeInput || !propiedadIdInput) return;

    const propiedadId = propiedadIdInput.value;
    const apiUrl = dateRangeInput.dataset.apiUrl; // Obtenemos la URL del atributo

    // Función para inicializar Flatpickr
    function initFlatpickr(disabledDates = []) {
        flatpickr(dateRangeInput, {
            mode: "range",
            minDate: "today",
            dateFormat: "Y-m-d",
            disable: disabledDates,
            onChange: function (selectedDates, dateStr, instance) {
                if (selectedDates.length === 2) {
                    const [inicio, fin] = selectedDates.sort((a, b) => a - b);
                    if (inicio && fin && inicio.getTime() < fin.getTime()) {
                        fechaInicioInput.value = instance.formatDate(inicio, "Y-m-d");
                        fechaFinInput.value = instance.formatDate(fin, "Y-m-d");
                    } else {
                        fechaInicioInput.value = '';
                        fechaFinInput.value = '';
                    }
                } else {
                    fechaInicioInput.value = '';
                    fechaFinInput.value = '';
                }
            }
        });
    }

    // Lógica principal
    if (propiedadId && apiUrl) {
        fetch(`${apiUrl}?propiedadId=${propiedadId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error de red al obtener fechas');
                }
                return response.json();
            })
            .then(disabledRanges => {
                initFlatpickr(disabledRanges);
            })
            .catch(error => {
                console.error('Error al procesar las fechas reservadas:', error);
                initFlatpickr([]); // Fallback
            });
    } else {
        initFlatpickr([]);
    }
});