document.addEventListener("DOMContentLoaded", function () {
    const slides = document.querySelectorAll(".carousel-item");
    const prevBtn = document.querySelector(".carousel-btn.prev");
    const nextBtn = document.querySelector(".carousel-btn.next");
    let currentIndex = 0;

    // Solo inicializamos si hay imágenes
    if (slides.length > 0) {
        
        // Función para mostrar el slide actual
        function showSlide(index) {
            // Ocultar todos
            slides.forEach(slide => {
                slide.classList.remove("active");
                slide.style.display = "none"; // Asegurar que se oculten
            });
            
            // Mostrar el actual
            slides[index].classList.add("active");
            slides[index].style.display = "block";
        }

        // Inicializar el primero
        showSlide(currentIndex);

        // Evento Siguiente
        if (nextBtn) {
            nextBtn.addEventListener("click", function () {
                currentIndex++;
                if (currentIndex >= slides.length) {
                    currentIndex = 0; // Volver al inicio
                }
                showSlide(currentIndex);
            });
        }

        // Evento Anterior
        if (prevBtn) {
            prevBtn.addEventListener("click", function () {
                currentIndex--;
                if (currentIndex < 0) {
                    currentIndex = slides.length - 1; // Ir al final
                }
                showSlide(currentIndex);
            });
        }
    }
});