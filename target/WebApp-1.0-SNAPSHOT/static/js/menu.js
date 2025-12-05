let btnMenu = document.getElementById("navToggle");
let menu = document.getElementById("navMenu");

btnMenu.addEventListener("click", function() {
    menu.classList.toggle("active");
});