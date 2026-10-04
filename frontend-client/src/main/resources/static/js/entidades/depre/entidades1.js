const registeredNeeds = [
    {
        id: 1,
        category: "Arroz",
        description: "Arroz fino",
        quantity: 50,
        unit: "KG",
        priority: "Recurrente",
        deadline: "2026-05-14"
    }
];

/* ============================================================
   MENÚ DE USUARIO
   ============================================================ */
const userMenuTrigger = document.getElementById("userMenuTrigger");
const userMenu = document.getElementById("userMenu");
const logoutBtn = document.getElementById("logoutBtn");

function closeUserMenu() {
    userMenu.hidden = true;
    userMenuTrigger.setAttribute("aria-expanded", "false");
}

userMenuTrigger.addEventListener("click", (e) => {
    e.stopPropagation();
    const isOpen = !userMenu.hidden;
    userMenu.hidden = isOpen;
    userMenuTrigger.setAttribute("aria-expanded", !isOpen);
});

document.addEventListener("click", (e) => {
    if (!userMenu.hidden && !userMenu.contains(e.target) && e.target !== userMenuTrigger) {
        closeUserMenu();
    }
});

logoutBtn.addEventListener("click", () => {
    closeUserMenu();
    alert("Sesión cerrada");
});

/* ============================================================
   TABS
   ============================================================ */
const tabs = document.querySelectorAll(".tab");
const views = document.querySelectorAll(".view");

tabs.forEach(tab => {
    tab.addEventListener("click", () => {
        tabs.forEach(t => t.classList.remove("active"));
        views.forEach(v => v.classList.remove("active"));
        tab.classList.add("active");
        document.getElementById("view-" + tab.dataset.tab).classList.add("active");
    });
});

/* ============================================================
   REQUERIMIENTO 1: REGISTRAR NECESIDADES
   ============================================================ */
const needForm = document.getElementById("needForm");
const registeredNeedsList = document.getElementById("registeredNeedsList");


function renderRegisteredNeeds() {
    if (registeredNeeds.length === 0) {
        registeredNeedsList.innerHTML = `<p class="empty-state">No hay necesidades registradas aún.</p>`;
        return;
    }

    registeredNeedsList.innerHTML = registeredNeeds.map(need => `
    <article class="need-item-card">
      <div class="need-item-header">
        <h3 class="need-item-title">${need.category}</h3>
        <button class="delete-btn" onclick="deleteNeed(${need.id})" title="Eliminar necesidad">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="3 6 5 6 21 6"></polyline>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
          </svg>
        </button>
      </div>
      <p class="need-item-desc">${need.description}</p>
      <div class="need-item-meta">
        <span>Cantidad: <b>${need.quantity} ${need.unit}</b></span>
        <span>Prioridad: <span class="badge-priority priority-${need.priority.toLowerCase()}">${need.priority}</span></span>
      </div>
    </article>
  `).join("");
}

const sendNeedsBtn = document.getElementById("sendNeedsBtn");

sendNeedsBtn.addEventListener("click", () => {
    if (registeredNeeds.length === 0) {
        alert("No hay necesidades registradas para enviar.");
        return;
    }

    alert(`Se enviaron ${registeredNeeds.length} necesidad(es) correctamente.`);

    registeredNeeds.splice(0, registeredNeeds.length);
    renderRegisteredNeeds();
});

function deleteNeed(id) {
    const index = registeredNeeds.findIndex(n => n.id === id);
    if (index !== -1) {
        registeredNeeds.splice(index, 1);
        renderRegisteredNeeds();
    }
}

needForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const newNeed = {
        id: Date.now(),
        category: document.getElementById("needCategory").value,
        description: document.getElementById("needDescription").value,
        quantity: document.getElementById("needQuantity").value,
        unit: document.getElementById("needUnit").value,
        priority: document.getElementById("needPriority").value
    };

    registeredNeeds.unshift(newNeed);
    renderRegisteredNeeds();
    needForm.reset();
});

/* ============================================================
   ÍCONOS DE ESTADO (compartidos entre Estado de Necesidades
   y Recepción de Donación)
   ============================================================ */
function getStatusIcon(status) {
    switch (status) {
        case "Pendiente":
            return `<svg class="status-icon icon-pending" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`;
        case "Entregado":
            return `<svg class="status-icon icon-delivered" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`;
        default:
            return "";
    }
}

/* ============================================================
   REQUERIMIENTO 2: ESTADO DE NECESIDADES / DONACIONES ASIGNADAS
   ============================================================ */
const assignedDonations = [
    {
        id: 101,
        title: "Leche fresca",
        description: " 100 paquetes de leche fresca.",
        quantity: "100",
        timeAgo: "Hace 2 días",
        status: "Pendiente",
        tipo:""
    },
    {
        id: 102,
        title: "Camperas",
        description: "30 camperas de invierno.",
        quantity: "30",
        timeAgo: "Hace 4 días",
        status: "Pendiente",
        tipo:""
    },
    {
        id: 103,
        title: "Zapatillas",
        description: "20 zapatillas para niños.",
        quantity: "20",
        timeAgo: "Hace 1 semana",
        status: "Entregado",
        tipo:""
    }
];

const statusNeedsList = document.getElementById("statusNeedsList");

function renderStatusNeeds() {
    if (assignedDonations.length === 0) {
        statusNeedsList.innerHTML = `<p class="empty-state">No posee donaciones o necesidades en seguimiento.</p>`;
        return;
    }

    statusNeedsList.innerHTML = assignedDonations.map(item => `
    <article class="status-card">
      <div class="status-card-left">
        ${getStatusIcon(item.status)}
        <div class="status-card-info">
          <h3>${item.title}</h3>
          <p class="status-card-sub">${item.description}</p>
          <div class="status-card-details">
            <span>Cantidad: ${item.quantity}</span>
            <span>${item.timeAgo}</span>
          </div>
        </div>
      </div>
      <div class="status-card-right">
        <span class="status-tag status-${item.status.toLowerCase().replace(" ", "-")}">${item.status}</span>
      </div>
    </article>
  `).join("");
}

// Inicialización de la vista
renderRegisteredNeeds();
renderStatusNeeds();

/* ============================================================
   AGREGADO (Euge - Entidad Beneficiaria):
   Recepción de Donación / Notificaciones / Seguimiento de Entrega
   ============================================================ */

/* ---------- REQUERIMIENTO: RECEPCIÓN DE DONACIÓN ---------- */
const donations = [
    {
        id: "0415",
        title: "Fideos tirabuzón",
        donor: "Molinos San José S.A",
        type: "Fideos",
        assignDate: "15 de Abril, 2026",
        description: "50 paquetes de fideos tirabuzón, que están bien envasados y tienen fecha de vencimiento superior a 6 meses.",
        quantity: "50 Paquetes",
        timeAgo: "Hace 2 horas",
        status: "Pendiente",
        photos: []
    },
    {
        id: "0412",
        title: "Frazadas de invierno",
        donor: "Corporación textil S.A",
        type: "Frazadas",
        assignDate: "8 de Abril, 2026",
        description: "60 frazadas, todas en buen estado.",
        quantity: "60 unidades",
        timeAgo: "Hace 1 día",
        status: "Pendiente",
        photos: []
    },
    {
        id: "0410",
        title: "Cuadernos escolares",
        donor: "Librería Central S.A.",
        type: "Cuadernos",
        assignDate: "2 de Abril, 2026",
        description: "80 cuadernos cuadriculados.",
        quantity: "80 unidades",
        timeAgo: "Hace 5 días",
        status: "Entregado",
        photos: ["cuadernos.jpg"]
    }
];

let openPhotosFor = null; // id de la donación con el panel de detalle/fotos abierto

const pendingDonationsList = document.getElementById("pendingDonationsList");
const confirmedDonationsList = document.getElementById("confirmedDonationsList");

function photoUrl(photo) {
    return typeof photo === "string" ? photo : URL.createObjectURL(photo);
}

function photosPanel(d) {
    if (openPhotosFor !== d.id) return "";

    const alreadyConfirmed = d.status === "Entregado";
    const hasPhotos = d.photos.length > 0;

    return `
    <div class="photos-panel">
      <div class="field-row">
        <div class="field"><span>Fecha de Asignación</span><p>${d.assignDate}</p></div>
        <div class="field"><span>Donante</span><p>${d.donor}</p></div>
      </div>
      <div class="field-row">
        <div class="field"><span>Tipo de Donación</span><p>${d.type}</p></div>
        <div class="field"><span>Cantidad estimada</span><p>${d.quantity}</p></div>
      </div>
      <div class="field"><span>Descripción</span><p>${d.description}</p></div>

      ${alreadyConfirmed && hasPhotos ? `
        <div class="field">
          <span>Fotografías de la donación</span>
          <div class="photo-gallery">
            ${d.photos.map(p => `<img src="${photoUrl(p)}" alt="Foto de la donación">`).join("")}
          </div>
        </div>
      ` : `
        <div class="upload-actions-row">
          <button type="button" class="btn btn-outline btn-camera" data-role="cameraBtn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>
            Tomar foto
          </button>
        </div>
        <input type="file" accept="image/*" capture="environment" data-role="cameraInput" hidden>
        <div class="upload-dropzone" data-role="dropzone">
          <input type="file" accept="image/png, image/jpeg" multiple hidden data-role="fileInput">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
          <p><b>Click para subir fotografías</b></p>
          <span class="page-sub">PNG, JPG (MAX. 5MB)</span>
          <div class="upload-preview" data-role="preview">${d.photos.map(p => `<img src="${photoUrl(p)}" alt="Foto">`).join("")}</div>
        </div>
      `}

      ${d.status === "Pendiente" ? `
        <div class="form-actions-right">
          <button type="button" class="btn btn-outline" data-role="cancel">Cancelar</button>
          <button type="button" class="btn btn-submit" data-role="confirm" ${d.photos.length === 0 ? "disabled" : ""}>Confirmar Recepción</button>
        </div>
      ` : ""}
    </div>`;
}

function renderDonations() {
    const pending = donations.filter(d => d.status === "Pendiente");
    const confirmed = donations.filter(d => d.status === "Entregado");

    pendingDonationsList.innerHTML = pending.length
        ? pending.map(d => donationCard(d)).join("")
        : `<p class="empty-state">No hay donaciones pendientes de confirmar.</p>`;

    confirmedDonationsList.innerHTML = confirmed.length
        ? confirmed.map(d => donationCard(d)).join("")
        : `<p class="empty-state">Aún no confirmó ninguna donación.</p>`;
}

function donationCard(d) {
    const icon = d.status === "Entregado" ? getStatusIcon("Entregado") : getStatusIcon("Pendiente");
    return `
    <article class="status-card donation-card" data-donation-id="${d.id}">
      <div class="status-card-row">
        <div class="status-card-left">
          ${icon}
          <div class="status-card-info">
            <h3>${d.title}</h3>
            <p class="status-card-sub">${d.donor}</p>
            <div class="status-card-details">
              <span>Cantidad: ${d.quantity}</span>
              <span>${d.timeAgo}</span>
              ${d.photos.length ? `<span>${d.photos.length} foto(s) adjunta(s)</span>` : ""}
            </div>
          </div>
        </div>
        <div class="status-card-right">
          <span class="status-tag status-${d.status.toLowerCase()}">${d.status === "Entregado" ? "Entregado" : "Pendiente"}</span>
          <button type="button" class="link-btn-secondary" data-role="togglePhotos">${openPhotosFor === d.id ? "Ocultar detalle" : "Ver detalle y fotos"}</button>
        </div>
      </div>
      ${photosPanel(d)}
    </article>`;
}

function findDonation(id) {
    return donations.find(d => d.id === id);
}

function getCard(el) {
    return el.closest(".donation-card");
}

document.addEventListener("click", (e) => {
    const card = getCard(e.target);
    if (!card) return;
    const d = findDonation(card.dataset.donationId);
    if (!d) return;

    if (e.target.closest('[data-role="togglePhotos"]')) {
        openPhotosFor = openPhotosFor === d.id ? null : d.id;
        renderDonations();
    }
    if (e.target.closest('[data-role="dropzone"]')) {
        card.querySelector('[data-role="fileInput"]').click();
    }
    if (e.target.closest('[data-role="cameraBtn"]')) {
        card.querySelector('[data-role="cameraInput"]').click();
    }
    if (e.target.closest('[data-role="cancel"]')) {
        openPhotosFor = null;
        renderDonations();
    }
    if (e.target.closest('[data-role="confirm"]')) {
        d.status = "Entregado";
        openPhotosFor = null;
        renderDonations();
        alert("Recepción confirmada con " + d.photos.length + " foto(s).");
    }
});

document.addEventListener("change", (e) => {
    const card = getCard(e.target);
    if (!card) return;
    const d = findDonation(card.dataset.donationId);
    if (!d) return;

    if (e.target.matches('[data-role="fileInput"]') || e.target.matches('[data-role="cameraInput"]')) {
        Array.from(e.target.files).forEach(file => {
            if (file.type.startsWith("image/")) d.photos.push(file);
        });
        renderDonations();
    }
});

document.addEventListener("dragover", (e) => {
    if (e.target.closest('[data-role="dropzone"]')) e.preventDefault();
});
document.addEventListener("drop", (e) => {
    const dropzone = e.target.closest('[data-role="dropzone"]');
    if (!dropzone) return;
    e.preventDefault();
    const card = getCard(e.target);
    const d = findDonation(card.dataset.donationId);
    Array.from(e.dataTransfer.files).forEach(file => {
        if (file.type.startsWith("image/")) d.photos.push(file);
    });
    renderDonations();
});

renderDonations();

/* ---------- REQUERIMIENTO: NOTIFICACIONES ---------- */
const notifications = [
    {
        id: 1,
        type: "assigned",
        title: "Nueva donación asignada.",
        description: "Se le ha asignado una donación de mesas de la Muebleria Arcos S.A",
        timeAgo: "Hace 2 horas",
        read: false
    },
    {
        id: 2,
        type: "transit",
        title: "Donación en tránsito.",
        description: "La donación de 10 cajas de azúcar está en camino.",
        timeAgo: "Hace 5 horas",
        read: false
    },
    {
        id: 3,
        type: "pending",
        title: "Confirmación de entrega pendiente",
        description: "Recuerde confirmar la recepción de la donación #DON-2026-04.",
        timeAgo: "Hace 1 día",
        read: true
    },
    {
        id: 4,
        type: "completed",
        title: "Entrega completada",
        description: "Se ha confirmado la entrega exitosa de la donación #DON-2026-10.",
        timeAgo: "Hace 2 días",
        read: true
    },
    {
        id: 5,
        type: "assigned",
        title: "Nueva donación asignada",
        description: "Se le ha asignado una donación calzado de parte de la Corporación zapatos S.A.",
        timeAgo: "Hace 3 días",
        read: true
    },
    {
        id: 6,
        type: "transit",
        title: "Donación en tránsito",
        description: "La donación #DON-2026-08 está en camino. Conductor: Juan Pérez - Patente ABC123.",
        timeAgo: "Hace 4 días",
        read: true
    }
];

const notificationsList = document.getElementById("notificationsList");
const markAllReadBtn = document.getElementById("markAllReadBtn");

function getNotificationIcon(type) {
    switch (type) {
        case "assigned":
            return `<svg class="notification-icon type-assigned" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>`;
        case "transit":
            return `<svg class="notification-icon type-transit" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>`;
        case "pending":
            return `<svg class="notification-icon type-pending" viewBox="0 0 24 24" fill="none" stroke="red" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
        case "completed":
            return `<svg class="notification-icon type-completed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`;
        default:
            return "";
    }
}

function renderNotifications() {
    if (notifications.length === 0) {
        notificationsList.innerHTML = `<p class="empty-state">No hay notificaciones.</p>`;
        return;
    }

    notificationsList.innerHTML = notifications.map(n => `
    <article class="notification-card ${n.read ? "" : "unread"}">
      ${getNotificationIcon(n.type)}
      <div>
        <div class="notification-title">${n.title}</div>
        <div class="notification-desc">${n.description}</div>
        <div class="notification-time">${n.timeAgo}</div>
      </div>
      ${n.read ? "" : '<span class="notification-unread-dot"></span>'}
    </article>
  `).join("");
}

markAllReadBtn.addEventListener("click", () => {
    notifications.forEach(n => n.read = true);
    renderNotifications();
});

renderNotifications();

/* ---------- REQUERIMIENTO: SEGUIMIENTO DE ENTREGA ---------- */
const locationHistoryData = [
    { address: "Av. Corrientes 1234", time: "14:32 hs" },
    { address: "Av. Santa Fe 2800", time: "14:15 hs" },
    { address: "Av. Libertador 5000", time: "13:45 hs" }
];

function renderLocationHistory() {
    const locationHistory = document.getElementById("locationHistory");
    locationHistory.innerHTML = locationHistoryData.map(loc => `
    <li>${loc.address}<span>${loc.time}</span></li>
  `).join("");
}

renderLocationHistory();

/* ---------- LIGHTBOX: ver fotos en grande ---------- */
const photoLightbox = document.getElementById("photoLightbox");
const lightboxImg = document.getElementById("lightboxImg");
const lightboxClose = document.getElementById("lightboxClose");

document.addEventListener("click", (e) => {
    const img = e.target.closest(".upload-preview img, .photo-gallery img");
    if (img) {
        lightboxImg.src = img.src;
        photoLightbox.hidden = false;
    }
});

lightboxClose.addEventListener("click", () => {
    photoLightbox.hidden = true;
});

photoLightbox.addEventListener("click", (e) => {
    if (e.target === photoLightbox) {
        photoLightbox.hidden = true;
    }
});