/**
 * validaciones.js
 * Gestiona las validaciones del lado del cliente para WebApp
 */

document.addEventListener("DOMContentLoaded", function () {

    // --- CONFIGURACIÓN Y UTILIDADES --- //

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    /**
     * Muestra un mensaje de error debajo del input.
     * Inserta el mensaje justo después del elemento input.
     */
    function mostrarError(input, mensaje) {
        // 1. Primero limpiamos cualquier error previo de ESTE input específico
        limpiarError(input);

        // 2. Crear elemento de mensaje
        const errorSmall = document.createElement("small");
        errorSmall.textContent = mensaje;
        errorSmall.classList.add("error-msg"); 
        errorSmall.style.color = "#e74c3c";
        errorSmall.style.display = "block";
        errorSmall.style.marginTop = "5px";
        errorSmall.style.fontWeight = "bold";

        // 3. Estilo visual al input
        input.style.borderColor = "#e74c3c";

        // 4. Insertar DESPUÉS del input.
        // Usamos nextSibling para que se inserte antes del salto de línea o <br> que sigue al input.
        if (input.nextSibling) {
            input.parentNode.insertBefore(errorSmall, input.nextSibling);
        } else {
            input.parentNode.appendChild(errorSmall);
        }
    }

    /**
     * Limpia el mensaje de error ESPECÍFICO de un input.
     * CORRECCIÓN: Ahora mira el siguiente elemento hermano en lugar de buscar en todo el padre.
     */
    function limpiarError(input) {
        input.style.borderColor = "#ddd"; // Restaurar borde original

        // Buscamos el elemento que sigue inmediatamente al input
        const siguienteElemento = input.nextElementSibling;

        // Si existe y tiene la clase 'error-msg', es nuestro mensaje de error. Lo borramos.
        if (siguienteElemento && siguienteElemento.classList.contains("error-msg")) {
            siguienteElemento.remove();
        }
    }

    // --- 1. VALIDACIÓN FORMULARIO REGISTRO USUARIO --- //
    const formRegistro = document.querySelector('form[action$="/usuario/guardar"]');

    if (formRegistro) {
        formRegistro.addEventListener("submit", function (e) {
            let esValido = true;

            // Validar Nombre
            const nombre = formRegistro.querySelector('input[name="nombre"]');
            if (nombre && nombre.value.trim().length < 2) {
                mostrarError(nombre, "El nombre debe tener al menos 2 caracteres.");
                esValido = false;
            } else if (nombre) {
                limpiarError(nombre);
            }

            // Validar Apellidos
            const apellidos = formRegistro.querySelector('input[name="apellidos"]');
            if (apellidos && apellidos.value.trim().length < 2) {
                mostrarError(apellidos, "Los apellidos deben tener al menos 2 caracteres.");
                esValido = false;
            } else if (apellidos) {
                limpiarError(apellidos);
            }

            // Validar Email
            const email = formRegistro.querySelector('input[name="email"]');
            if (email && !emailRegex.test(email.value.trim())) {
                mostrarError(email, "Por favor, introduce un correo electrónico válido.");
                esValido = false;
            } else if (email) {
                limpiarError(email);
            }

            // Validar Contraseña
            const contra = formRegistro.querySelector('input[name="contra"]');
            if (contra && contra.value.length < 6) {
                mostrarError(contra, "La contraseña debe tener al menos 6 caracteres.");
                esValido = false;
            } else if (contra) {
                limpiarError(contra);
            }

            if (!esValido) e.preventDefault();
        });
    }

    // --- 2. VALIDACIÓN FORMULARIO LOGIN --- //
    const formLogin = document.querySelector('form[action$="/usuario/validar"]');

    if (formLogin) {
        formLogin.addEventListener("submit", function (e) {
            let esValido = true;

            const email = formLogin.querySelector('input[name="email"]');
            const contra = formLogin.querySelector('input[name="contra"]');

            if (email) {
                if (!email.value.trim()) {
                    mostrarError(email, "El correo es obligatorio.");
                    esValido = false;
                } else {
                    limpiarError(email);
                }
            }

            if (contra) {
                if (!contra.value) {
                    mostrarError(contra, "La contraseña es obligatoria.");
                    esValido = false;
                } else {
                    limpiarError(contra);
                }
            }

            if (!esValido) e.preventDefault();
        });
    }

    // --- 3. VALIDACIÓN FORMULARIO PROPIEDAD --- //
    const formPropiedad = document.querySelector('form[action$="/propiedad/guardar"]');

    if (formPropiedad) {
        formPropiedad.addEventListener("submit", function (e) {
            let esValido = true;

            // Validar campos de texto básicos
            const camposTexto = ['nombre', 'calle_numero', 'ciudad', 'codigo_postal'];
            camposTexto.forEach(campo => {
                const input = formPropiedad.querySelector(`input[name="${campo}"]`);
                if (input) {
                    if (input.value.trim().length === 0) {
                        mostrarError(input, "Este campo es obligatorio.");
                        esValido = false;
                    } else {
                        limpiarError(input);
                    }
                }
            });

            // Validar números positivos
            const camposNumericos = ['precio_habitacion', 'habitaciones', 'banos'];
            camposNumericos.forEach(campo => {
                const input = formPropiedad.querySelector(`input[name="${campo}"]`);
                if (input) {
                    if (input.value === "" || parseFloat(input.value) <= 0) {
                        mostrarError(input, "Debe ser un número mayor a 0.");
                        esValido = false;
                    } else {
                        limpiarError(input);
                    }
                }
            });

            // Validar Latitud (-90 a 90)
            const latitud = formPropiedad.querySelector('input[name="latitud"]');
            if (latitud) {
                const latVal = parseFloat(latitud.value);
                if (isNaN(latVal) || latVal < -90 || latVal > 90) {
                    mostrarError(latitud, "Latitud inválida (debe estar entre -90 y 90).");
                    esValido = false;
                } else {
                    limpiarError(latitud);
                }
            }

            // Validar Longitud (-180 a 180)
            const longitud = formPropiedad.querySelector('input[name="longitud"]');
            if (longitud) {
                const lonVal = parseFloat(longitud.value);
                if (isNaN(lonVal) || lonVal < -180 || lonVal > 180) {
                    mostrarError(longitud, "Longitud inválida (debe estar entre -180 y 180).");
                    esValido = false;
                } else {
                    limpiarError(longitud);
                }
            }
            const idInput = document.querySelector('input[name="id"]');
            const portadaInput = document.querySelector('input[name="portada"]');
            
            const esCreacion = !idInput || idInput.value.trim() === "";

            const portada = formPropiedad.querySelector('input[name="portada"]');
            if (portada) {
                if (esCreacion && portada.files.length === 0) {
                    mostrarError(portada, "Debes seleccionar una imagen de portada.");
                    esValido = false;
                } else {
                    const archivo = portada.files[0];
                    const extensionesValidas = ['image/jpeg', 'image/png', 'image/webp', 'image/avif'];
                    if (!extensionesValidas.includes(archivo.type)) {
                        mostrarError(portada, "Formato no válido. Usa JPG, PNG, WEBP o AVIF.");
                        esValido = false;
                    } else {
                        limpiarError(portada);
                    }
                }
            }

            if (!esValido) e.preventDefault();
        });
    }
});