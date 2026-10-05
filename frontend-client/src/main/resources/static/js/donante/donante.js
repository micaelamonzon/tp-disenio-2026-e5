/* Menú de usuario: común a todas las pantallas del donante */
const userMenuTrigger = document.getElementById("userMenuTrigger");
const userMenu = document.getElementById("userMenu");

function closeUserMenu() {
  userMenu.hidden = true;
  userMenuTrigger.setAttribute("aria-expanded", "false");
}

userMenuTrigger.addEventListener("click", (e) => {
  e.stopPropagation();
  if (!userMenu.hidden) {
    closeUserMenu();
  } else {
    userMenu.hidden = false;
    userMenuTrigger.setAttribute("aria-expanded", "true");
  }
});

document.addEventListener("click", (e) => {
  if (!userMenu.hidden && !userMenu.contains(e.target) && e.target !== userMenuTrigger) {
    closeUserMenu();
  }
});

document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closeUserMenu();
});

// TODO Etapa 2: "Cerrar sesión" pasa a ser un form POST /logout
