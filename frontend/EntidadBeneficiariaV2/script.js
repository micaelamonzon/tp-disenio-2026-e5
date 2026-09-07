/* ============================================================
   DATOS (mock) — Estado inicial de necesidades
   ============================================================ */

const registeredNeeds = [
    {
        id: 1,
        category: "Alimentos",
        description: "Arroz y fideos",
        quantity: 50,
        unit: "kg",
        priority: "Alta",
        deadline: "2026-05-14",
        notes: ""
    }
];

const assignedDonations = [
    {
        id: 101,
        title: "Alimentos no perecederos",
        description: "Arroz, fideos, lentejas",
        quantity: "50",
        timeAgo: "Hace 2 días",
        status: "Pendiente"
    },
    {
        id: 102,
        title: "Ropa de abrigo",
        description: "Camperas y frazadas",
        quantity: "30",
        timeAgo: "Hace 4 días",
        status: "En proceso"
    },
    {
        id: 103,
        title: "Calzado",
        description: "Zapatillas para niños",
        quantity: "20",
        timeAgo: "Hace 1 semana",
        status: "Entregado"
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

function formatDate(dateStr) {
    if (!dateStr) return "";
    const parts = dateStr.split("-");
    if (parts.length !== 3) return dateStr;
    const date = new Date(parts[0], parts[1] - 1, parts[2]);
    return date.toLocaleDateString("es-ES", { day: "numeric", month: "long", year: "numeric" });
}

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
      <div class="need-item-date">
        Fecha límite: <b>${formatDate(need.deadline)}</b>
      </div>
    </article>
  `).join("");
}

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
        priority: document.getElementById("needPriority").value,
        deadline: document.getElementById("needDeadline").value,
        notes: document.getElementById("needNotes").value
    };

    registeredNeeds.unshift(newNeed);
    renderRegisteredNeeds();
    needForm.reset();
});

/* ============================================================
   REQUERIMIENTO 2: ESTADO DE NECESIDADES / DONACIONES ASIGNADAS
   ============================================================ */
const statusNeedsList = document.getElementById("statusNeedsList");

function getStatusIcon(status) {
    switch (status) {
        case "Pendiente":
            return `<svg class="status-icon icon-pending" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
        case "En proceso":
            return `<svg class="status-icon icon-process" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`;
        case "Entregado":
            return `<svg class="status-icon icon-delivered" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`;
        default:
            return "";
    }
}

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
const uploadDropzone = document.getElementById("uploadDropzone");
const donationPhotosInput = document.getElementById("donationPhotos");
const uploadPreview = document.getElementById("uploadPreview");
const confirmReceptionBtn = document.getElementById("confirmReceptionBtn");
const cancelReceptionBtn = document.getElementById("cancelReceptionBtn");

let uploadedPhotos = [];

uploadDropzone.addEventListener("click", () => donationPhotosInput.click());

uploadDropzone.addEventListener("dragover", (e) => {
    e.preventDefault();
    uploadDropzone.classList.add("dragover");
});
uploadDropzone.addEventListener("dragleave", () => {
    uploadDropzone.classList.remove("dragover");
});
uploadDropzone.addEventListener("drop", (e) => {
    e.preventDefault();
    uploadDropzone.classList.remove("dragover");
    handleFiles(e.dataTransfer.files);
});

donationPhotosInput.addEventListener("change", (e) => {
    handleFiles(e.target.files);
});

function handleFiles(fileList) {
    Array.from(fileList).forEach(file => {
        if (!file.type.startsWith("image/")) return;
        uploadedPhotos.push(file);
    });
    renderUploadPreview();
    confirmReceptionBtn.disabled = uploadedPhotos.length === 0;
}

function renderUploadPreview() {
    uploadPreview.innerHTML = uploadedPhotos.map(file => {
        const url = URL.createObjectURL(file);
        return `<img src="${url}" alt="Foto de recepción">`;
    }).join("");
}

confirmReceptionBtn.addEventListener("click", () => {
    document.getElementById("donationStatus").textContent = "Entregado";
    alert("Recepción de la donación confirmada con " + uploadedPhotos.length + " foto(s).");
});

cancelReceptionBtn.addEventListener("click", () => {
    uploadedPhotos = [];
    renderUploadPreview();
    confirmReceptionBtn.disabled = true;
});

/* ---------- REQUERIMIENTO: NOTIFICACIONES ---------- */
const notifications = [
    {
        id: 1,
        type: "assigned",
        title: "Nueva donación asignada",
        description: "Se le ha asignado una donación de alimentos no perecederos de Fundación Solidaria Buenos Aires",
        timeAgo: "Hace 2 horas",
        read: false
    },
    {
        id: 2,
        type: "transit",
        title: "Donación en tránsito",
        description: "La donación #DON-2026-0415 está en camino. Tiempo estimado de llegada: 3 horas",
        timeAgo: "Hace 5 horas",
        read: false
    },
    {
        id: 3,
        type: "pending",
        title: "Confirmación de entrega pendiente",
        description: "Recuerde confirmar la recepción de la donación #DON-2026-0412 antes de las 18:00 hs",
        timeAgo: "Hace 1 día",
        read: true
    },
    {
        id: 4,
        type: "completed",
        title: "Entrega completada",
        description: "Se ha confirmado la entrega exitosa de la donación #DON-2026-0410",
        timeAgo: "Hace 2 días",
        read: true
    },
    {
        id: 5,
        type: "assigned",
        title: "Nueva donación asignada",
        description: "Se le ha asignado una donación de ropa y calzado de Corporación Ayuda Mutua",
        timeAgo: "Hace 3 días",
        read: true
    },
    {
        id: 6,
        type: "transit",
        title: "Donación en tránsito",
        description: "La donación #DON-2026-0408 está en camino. Conductor: Juan Pérez - Patente ABC123",
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
            return `<svg class="notification-icon type-pending" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
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