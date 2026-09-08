/* ============================================================
   DATOS (mock) — en un backend real esto vendría de una API
   ============================================================ */

const ICONS = {
  box: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>',
  tag: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2H2v10l9.29 9.29a1 1 0 0 0 1.41 0l8.29-8.29a1 1 0 0 0 0-1.41Z"/><path d="M7 7h.01"/></svg>',
  calendar: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>',
  check: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>',
  gift: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="8" width="18" height="4" rx="1"/><path d="M12 8v13M19 12v7a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-7"/><path d="M7.5 8a2.5 2.5 0 0 1 0-5C11 3 12 8 12 8s1-5 4.5-5a2.5 2.5 0 0 1 0 5"/></svg>',
  trending: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>',
  clock: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>',
  building: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 22V4a1 1 0 0 1 1-1h10a1 1 0 0 1 1 1v18"/><path d="M2 22h20"/><path d="M9 7h1M14 7h1M9 11h1M14 11h1M9 15h1M14 15h1"/></svg>',
  users: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>',
  heart: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/></svg>',
  heartFilled: '<svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1"><path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/></svg>',
  truck: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10 17h4V5H10z"/><path d="M20 17h2v-7l-3-4h-3v11h4z"/><circle cx="7.5" cy="17.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></svg>',
  pin: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>',
};

const activeDeliveries = [
  {
    id: "DON-001",
    truckId: "CAMION-A1",
    status: "En camino",
    eta: "45 minutos",
    progress: 45,
    destination: "Centro Comunitario San Martín, Av. Rivadavia 1234, CABA",
    items: ["Alimentos no perecederos", "Agua mineral", "Ropa de invierno"],
    lastUpdate: "hace menos de 1 minuto",
    truckPos: { x: 34, y: 42 },
    destPos: { x: 42, y: 34 }
  },
  {
    id: "DON-002",
    truckId: "CAMION-B2",
    status: "Cerca del destino",
    eta: "15 minutos",
    progress: 90,
    destination: "Fundación Esperanza, Calle Bolívar 567, Buenos Aires",
    items: ["Medicamentos", "Artículos de higiene"],
    lastUpdate: "hace menos de 1 minuto",
    truckPos: { x: 21, y: 53 },
    destPos: { x: 48, y: 76 }
  }
];

const donations = [
  { qty: "6 unidades", name: "Sillas", category: "Mobiliario", subcategory: "Sillas", condition: "Usado", status: "Asignada", assignedTo: "Comedor Los Girasoles" },
  { qty: "100 paquetes", name: "Fideos secos", category: "Alimentos", subcategory: "Fideos secos", status: "Asignada", expires: "01/01/2027", assignedTo: "Fundación Esperanza" },
  { qty: "50 kg", name: "Arroz", category: "Alimentos", subcategory: "Granos", status: "Pendiente", expires: "15/06/2026" },
  { qty: "20 unidades", name: "Remeras", category: "Vestimenta", subcategory: "Remeras", condition: "Nuevo", status: "Pendiente" },
  { qty: "3 unidades", name: "Mesas", category: "Mobiliario", subcategory: "Mesas", condition: "Usado", status: "Asignada", assignedTo: "Centro Comunitario San Martín" },
  { qty: "200 litros", name: "Leche", category: "Alimentos", subcategory: "Lácteos", status: "Pendiente", expires: "10/05/2026" },
  { qty: "15 unidades", name: "Pantalones", category: "Vestimenta", subcategory: "Pantalones", condition: "Usado", status: "Recibida", assignedTo: "Casa del Niño" },
  { qty: "80 paquetes", name: "Galletitas", category: "Alimentos", subcategory: "Snacks", status: "Pendiente", expires: "20/12/2026" },
];

// Categoria -> lista de subcategorías (se usa para poblar el filtro dependiente)
const categoryMap = {};
donations.forEach(d => {
  if (!categoryMap[d.category]) categoryMap[d.category] = new Set();
  categoryMap[d.category].add(d.subcategory);
});

const entities = [
  { name: "Comedor Los Girasoles", category: "Alimentación", location: "Villa Devoto, CABA", received: 34, following: true, desc: "Brinda almuerzo y merienda diaria a más de 120 familias del barrio." },
  { name: "Fundación Esperanza", category: "Alimentación / Insumos", location: "Lanús, Buenos Aires", received: 51, following: true, desc: "Distribuye alimentos no perecederos a comedores comunitarios de la zona sur." },
  { name: "Centro Comunitario San Martín", category: "Mobiliario / Vivienda", location: "San Martín, Buenos Aires", received: 12, following: true, desc: "Equipa espacios comunitarios y apoya a familias en situación de calle." },
  { name: "Casa del Niño", category: "Vestimenta / Niñez", location: "Quilmes, Buenos Aires", received: 27, following: false, desc: "Acompaña a niños y niñas en situación de vulnerabilidad social." },
  { name: "Hogar de Ancianos San José", category: "Cuidado de adultos mayores", location: "Morón, Buenos Aires", received: 19, following: false, desc: "Residencia para adultos mayores sin cobertura social ni familia cercana." },
  { name: "Ropero Comunitario Norte", category: "Vestimenta", location: "Vicente López, Buenos Aires", received: 40, following: false, desc: "Organiza y entrega ropa en buen estado a familias de la zona norte." },
];

const missions = [
  { name: "Racha de 10", desc: "Dona por 10 meses consecutivos, una donación por día.", progress: 100, total: 100 },
  { name: "Completitud de 14 ", desc: "Realizar donaciones de 14 categorías distintas.", progress: 100, total: 100 },
  { name: "Hábil Donador de 8", desc: "Lograr que una donación supere 8 de cantidad de bienes.", progress:100 , total: 100 },
  { name: "Donaciones Exitosas de 5", desc: "Lograr que 5 donaciones sean recibidas exitosamente por una entidad beneficiaria.", progress: 10, total: 100},
];

const badges = [
  { name: "Colaborador", desc: "Completaste todas las misiones de esta categoria", icon: "🌱", unlocked: true },
  { name: "Sostenedor ", desc: "Completa la mision actual y las que faltán para desbloquear esta categoria", icon: "🛡️", unlocked: false },
  { name: "Transformador ", desc: "Completa todas las misiones de Sostenedor para alcanzar esta categoria", icon: "🏅", unlocked: false },
];

const notifications = [
  { type: "asignada", title: "Donación asignada", desc: "Tus 100 paquetes de fideos secos fueron asignados a Fundación Esperanza", time: "Hace 10 minutos", unread: true },
  { type: "mision", title: "Misión cumplida", desc: '¡Completaste la misión "Donar 10kg de arroz"! Ganaste 50 XP', time: "Hace 2 horas", unread: true },
  { type: "recibida", title: "Donación recibida por la entidad", desc: "Comedor Los Girasoles confirmó la recepción de tus 6 sillas", time: "Hace 5 horas", unread: true },
  { type: "categoria", title: "Subida de categoría", desc: "¡Felicitaciones! Alcanzaste el Nivel 5 - Donante Comprometido", time: "Ayer", unread: false },
  { type: "asignada", title: "Donación asignada", desc: "Tu donación de 3 mesas fue asignada a Centro Comunitario San Martín", time: "Ayer", unread: false },
  { type: "recibida", title: "Donación recibida por la entidad", desc: "Casa del Niño recibió tus 15 pantalones y agradeció tu generosidad", time: "Hace 2 días", unread: false },
  { type: "mision", title: "Misión cumplida", desc: 'Completaste "Seguir a 3 entidades beneficiarias". +30 XP', time: "Hace 3 días", unread: false },
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
  if (isOpen) {
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

logoutBtn.addEventListener("click", () => {
  closeUserMenu();
  // Acá se conectaría el cierre de sesión real (limpiar token, redirigir, etc.)
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
   MIS DONACIONES — filtros dependientes
   ============================================================ */
const filterEstado = document.getElementById("filterEstado");
const filterCategoria = document.getElementById("filterCategoria");
const filterSubcategoria = document.getElementById("filterSubcategoria");
const donationsGrid = document.getElementById("donationsGrid");
const donationsEmpty = document.getElementById("donationsEmpty");
const resultsCount = document.getElementById("resultsCount");

// Poblar select de Categoría
Object.keys(categoryMap).forEach(cat => {
  const opt = document.createElement("option");
  opt.value = cat;
  opt.textContent = cat;
  filterCategoria.appendChild(opt);
});

function refreshSubcategoryOptions() {
  const selectedCategory = filterCategoria.value;
  filterSubcategoria.innerHTML = '<option value="todas">Todas</option>';

  const subcats = selectedCategory === "todas"
    ? new Set(donations.map(d => d.subcategory))
    : categoryMap[selectedCategory] || new Set();

  subcats.forEach(sub => {
    const opt = document.createElement("option");
    opt.value = sub;
    opt.textContent = sub;
    filterSubcategoria.appendChild(opt);
  });
}

function donationCardHTML(d) {
  return `
    <article class="donation-card">
      <div class="donation-card-top">
        <span class="donation-qty">${ICONS.box} ${d.qty}</span>
        <span class="pill pill-${d.status}">${d.status}</span>
      </div>
      <h3 class="donation-name">${d.name}</h3>
      <span class="donation-meta">${ICONS.tag} ${d.category}/${d.subcategory}</span>
      ${d.condition ? `<span class="donation-meta">Condición: ${d.condition}</span>` : ""}
      ${d.expires ? `<span class="donation-meta">${ICONS.calendar} Vence: ${d.expires}</span>` : ""}
      ${d.assignedTo ? `
        <div class="donation-assigned">
          <span class="donation-assigned-label">Asignada a:</span>
          <a href="#" data-entity="${d.assignedTo}">${d.assignedTo}</a>
        </div>` : ""}
    </article>
  `;
}

function renderDonations() {
  const estado = filterEstado.value;
  const categoria = filterCategoria.value;
  const subcategoria = filterSubcategoria.value;

  const filtered = donations.filter(d => {
    if (estado !== "todos" && d.status !== estado) return false;
    if (categoria !== "todas" && d.category !== categoria) return false;
    if (subcategoria !== "todas" && d.subcategory !== subcategoria) return false;
    return true;
  });

  resultsCount.textContent = `Mostrando ${filtered.length} donacion${filtered.length === 1 ? "" : "es"}`;
  donationsGrid.innerHTML = filtered.map(donationCardHTML).join("");
  donationsEmpty.hidden = filtered.length !== 0;

  // Navegar a la entidad beneficiaria desde el link "Asignada a"
  donationsGrid.querySelectorAll("[data-entity]").forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelector('.tab[data-tab="entidades"]').click();
      const target = entities.find(en => en.name === link.dataset.entity);
      if (target) {
        setTimeout(() => {
          const card = document.querySelector(`[data-entity-name="${target.name}"]`);
          if (card) {
            card.scrollIntoView({ behavior: "smooth", block: "center" });
            card.classList.add("highlight");
          }
        }, 50);
      }
    });
  });
}

filterCategoria.addEventListener("change", () => {
  refreshSubcategoryOptions();
  renderDonations();
});
filterEstado.addEventListener("change", renderDonations);
filterSubcategoria.addEventListener("change", renderDonations);

refreshSubcategoryOptions();
renderDonations();

/* ============================================================
   ENTIDADES BENEFICIARIAS
   ============================================================ */
const entitiesGrid = document.getElementById("entitiesGrid");
const entitySearch = document.getElementById("entitySearch");

function entityCardHTML(en) {
  return `
    <article class="entity-card" data-entity-name="${en.name}">
      <div class="entity-top">
        <span class="entity-icon">${ICONS.building}</span>
        <div>
          <h3 class="entity-name">${en.name}</h3>
          <span class="entity-category">${en.category}</span>
        </div>
      </div>
      <p class="entity-desc">${en.desc}</p>
      <div class="entity-stats">
        <div class="entity-stat"><b>${en.received}</b><span>Donaciones recibidas</span></div>
        <div class="entity-stat"><b>${en.location.split(",")[0]}</b><span>Ubicación</span></div>
      </div>
      <div class="entity-actions">
        <button class="btn btn-primary">Ver perfil</button>
      </div>
    </article>
  `;
}

function renderEntities() {
  const query = entitySearch.value.trim().toLowerCase();
  const filtered = entities.filter(en =>
    en.name.toLowerCase().includes(query) || en.category.toLowerCase().includes(query)
  );
  entitiesGrid.innerHTML = filtered.map(entityCardHTML).join("");
}

entitySearch.addEventListener("input", renderEntities);
renderEntities();

/* ============================================================
   INCENTIVOS — misiones e insignias
   ============================================================ */
const missionsGrid = document.getElementById("missionsGrid");
const badgesGrid = document.getElementById("badgesGrid");

function missionCardHTML(m) {
  const done = m.progress >= m.total;
  const pct = Math.min(100, Math.round((m.progress / m.total) * 100));
  return `
    <article class="mission-card">
      <div class="mission-top">
        <div>
          <h3 class="mission-name">${m.name}</h3>
          <p class="mission-desc">${m.desc}</p>
        </div>
        ${done ? `<span class="mission-check">${ICONS.check}</span>` : ""}
      </div>
      <div class="mission-progress">
        <div class="mission-progress-labels">
          <span>Progreso</span>
          <span>${m.progress} / ${m.total}</span>
        </div>
        <div class="progress-track">
          <div class="progress-fill ${done ? "done" : ""}" style="width:${pct}%"></div>
        </div>
      </div>
    </article>
  `;
}

function badgeCardHTML(b) {
  return `
    <article class="badge-card ${b.unlocked ? "" : "locked"}">
      ${b.unlocked ? `<span class="badge-check">${ICONS.check}</span>` : ""}
      <div class="badge-circle">${b.icon}</div>
      <div class="badge-name">${b.name}</div>
      <div class="badge-desc">${b.desc}</div>
    </article>
  `;
}

missionsGrid.innerHTML = missions.map(missionCardHTML).join("");
badgesGrid.innerHTML = badges.map(badgeCardHTML).join("");

/* ============================================================
   SEGUIMIENTO EN TIEMPO REAL (REQUERIMIENTO IMPLEMENTADO)
   ============================================================ */
const routesSvg = document.getElementById("routesSvg");
const mapMarkers = document.getElementById("mapMarkers");
const deliveriesList = document.getElementById("deliveriesList");

let activePopoverTruckId = null;

function renderTrackingMap() {
  routesSvg.innerHTML = "";
  mapMarkers.innerHTML = "";

  activeDeliveries.forEach(del => {
    // Dibujar línea punteada entre el camión y el destino
    const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttribute("x1", `${del.truckPos.x}%`);
    line.setAttribute("y1", `${del.truckPos.y}%`);
    line.setAttribute("x2", `${del.destPos.x}%`);
    line.setAttribute("y2", `${del.destPos.y}%`);
    line.setAttribute("class", "route-line");
    routesSvg.appendChild(line);

    // Marker Camión
    const truckMarker = document.createElement("button");
    truckMarker.className = "marker marker-truck";
    truckMarker.style.left = `${del.truckPos.x}%`;
    truckMarker.style.top = `${del.truckPos.y}%`;
    truckMarker.innerHTML = ICONS.truck;
    truckMarker.title = `Camión ${del.truckId}`;
    truckMarker.onclick = (e) => {
      e.stopPropagation();
      activePopoverTruckId = activePopoverTruckId === del.truckId ? null : del.truckId;
      renderTrackingMap();
    };
    mapMarkers.appendChild(truckMarker);

    // Marker Destino
    const destMarker = document.createElement("div");
    destMarker.className = "marker marker-dest";
    destMarker.style.left = `${del.destPos.x}%`;
    destMarker.style.top = `${del.destPos.y}%`;
    destMarker.innerHTML = ICONS.pin;
    mapMarkers.appendChild(destMarker);

    // Ventana emergente (Popover) si está activo
    if (activePopoverTruckId === del.truckId) {
      const popover = document.createElement("div");
      popover.className = "map-popover";
      popover.style.left = `${del.truckPos.x}%`;
      popover.style.top = `${del.truckPos.y}%`;
      popover.innerHTML = `
        <div class="popover-title">
          <span>${ICONS.truck} <b>${del.truckId}</b></span>
        </div>
        <div class="popover-row"><span>Donación:</span> <b>${del.id}</b></div>
        <div class="popover-row"><span>Estado:</span> <span class="badge-status status-orange">${del.status}</span></div>
        <div class="popover-row"><span>Tiempo estimado:</span> <b>${del.eta}</b></div>
        <div class="popover-dest">
          ${ICONS.pin}
          <div>
            <b>Destino:</b><br>
            ${del.destination}
          </div>
        </div>
        <div class="progress-bar-container">
          <div class="progress-bar-fill" style="width: ${del.progress}%"></div>
        </div>
        <div class="popover-pct">${del.progress}% completado</div>
        <div class="popover-time">Última actualización: ${del.lastUpdate}</div>
      `;
      mapMarkers.appendChild(popover);
    }
  });
}

function renderTrackingList() {
  deliveriesList.innerHTML = activeDeliveries.map(del => `
    <article class="delivery-card">
      <div class="delivery-card-head">
        <div>
          <span class="delivery-id">${ICONS.box} ${del.id}</span>
          <span class="badge-status status-blue">${del.status}</span>
        </div>
        <div class="delivery-meta-right">
          <span class="truck-code">${del.truckId}</span>
          <span class="eta-text">${ICONS.clock} ${del.eta}</span>
        </div>
      </div>

      <div class="progress-track">
        <div class="progress-fill" style="width: ${del.progress}%"></div>
      </div>
      <span class="progress-label">${del.progress}% del recorrido</span>

      <div class="delivery-dest">
        ${ICONS.pin}
        <div>
          <b>Destino:</b><br>
          ${del.destination}
        </div>
      </div>

      <div class="delivery-items">
        <span>Artículos:</span>
        <ul>
          ${del.items.map(item => `<li>${item}</li>`).join("")}
        </ul>
      </div>

      <div class="delivery-footer">
        Última actualización: ${del.lastUpdate}
      </div>
    </article>
  `).join("");
}

document.addEventListener("click", () => {
  if (activePopoverTruckId !== null) {
    activePopoverTruckId = null;
    renderTrackingMap();
  }
});

renderTrackingMap();
renderTrackingList();


/* ============================================================
   NOTIFICACIONES
   ============================================================ */
const notificationsList = document.getElementById("notificationsList");
const newPill = document.getElementById("newPill");
const tabNotifDot = document.getElementById("tabNotifDot");

function notifIconHTML(type) {
  switch (type) {
    case "asignada": return ICONS.box;
    case "mision": return ICONS.check;
    case "recibida": return ICONS.gift;
    case "categoria": return ICONS.trending;
    default: return ICONS.box;
  }
}

function notifCardHTML(n) {
  return `
    <article class="notif-card">
      <span class="notif-icon ${n.type}">${notifIconHTML(n.type)}</span>
      <div class="notif-body">
        <h3 class="notif-title">${n.title}</h3>
        <p class="notif-desc">${n.desc}</p>
        <span class="notif-time">${ICONS.clock} ${n.time}</span>
      </div>
      ${n.unread ? '<span class="notif-unread-dot"></span>' : ""}
    </article>
  `;
}

function renderNotifications() {
  notificationsList.innerHTML = notifications.map(notifCardHTML).join("");
  const unreadCount = notifications.filter(n => n.unread).length;
  if (unreadCount > 0) {
    newPill.hidden = false;
    newPill.textContent = `${unreadCount} nueva${unreadCount === 1 ? "" : "s"}`;
    tabNotifDot.hidden = false;
  } else {
    newPill.hidden = true;
    tabNotifDot.hidden = true;
  }
}

renderNotifications();
