/*
// Datos de ejemplo: se reemplazan cuando exista la API de notificaciones
const notifications = [
  { type: "asignada", title: "Donación asignada", desc: "Tus 100 paquetes de fideos secos fueron asignados a Fundación Esperanza", time: "Hace 10 minutos", unread: true },
  { type: "mision", title: "Misión cumplida", desc: "¡Completaste la misión Racha de 10!", time: "Hace 2 horas", unread: true },
  { type: "recibida", title: "Donación recibida por la entidad", desc: "Comedor Los Girasoles confirmó la recepción de tus 6 sillas", time: "Hace 5 horas", unread: true },
  { type: "categoria", title: "Subida de categoría", desc: "¡Felicitaciones! Alcanzaste Transformador", time: "Ayer", unread: false },
  { type: "asignada", title: "Donación asignada", desc: "Tu donación de 3 mesas fue asignada a Centro Comunitario San Martín", time: "Ayer", unread: false },
  { type: "recibida", title: "Donación recibida por la entidad", desc: "Casa del Niño recibió tus 15 pantalones y agradeció tu generosidad", time: "Hace 2 días", unread: false },
  { type: "mision", title: "Misión cumplida", desc: "¡Completaste la misión de Habil Donador!", time: "Hace 3 días", unread: false },
];

const notificationsList = document.getElementById("notificationsList");
const newPill = document.getElementById("newPill");

function notifIconHTML(type) {
  switch (type) {
    case "asignada": return ICONS.box;
    case "mision": return ICONS.check;
    case "recibida": return ICONS.gift;
    case "categoria": return ICONS.trending;
    default: return ICONS.box;
  }
}

function notifCardHTML(n, index) {
  return `
    <article class="notif-card" data-index="${index}">
      <span class="notif-icon ${n.type}">${notifIconHTML(n.type)}</span>
      <div class="notif-body">
        <h3 class="notif-title">${n.title}</h3>
        <p class="notif-desc">${n.desc}</p>
        <span class="notif-time">${ICONS.clock} ${n.time}</span>
      </div>
      ${n.unread ? '<span class="notif-unread-dot" data-role="markRead" title="Marcar como leída"></span>' : ""}
    </article>
  `;
}

function renderNotifications() {
  notificationsList.innerHTML = notifications.map(notifCardHTML).join("");
  const unreadCount = notifications.filter(n => n.unread).length;
  newPill.hidden = unreadCount === 0;
  newPill.textContent = `${unreadCount} nueva${unreadCount === 1 ? "" : "s"}`;
}

notificationsList.addEventListener("click", (e) => {
  const dot = e.target.closest('[data-role="markRead"]');
  if (!dot) return;
  const notification = notifications[Number(dot.closest(".notif-card").dataset.index)];
  if (notification) {
    notification.unread = false;
    renderNotifications();
  }
});

renderNotifications();
*/

// La lista de notificaciones ahora la arma Thymeleaf en el servidor con los
// datos reales de notificaciones-service (ver DonanteController#notificaciones).
// Este archivo queda para agregar interacciones del lado del cliente si hacen falta.