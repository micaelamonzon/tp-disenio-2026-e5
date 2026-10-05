// Datos de ejemplo: se reemplazan cuando se integre con el servicio de logística
const activeDeliveries = [
  {
    id: "DON-001",
    truckId: "CAMION-A1",
    status: "En traslado",
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
    status: "En traslado",
    eta: "15 minutos",
    progress: 90,
    destination: "Fundación Esperanza, Calle Bolívar 567, Buenos Aires",
    items: ["Medicamentos", "Artículos de higiene"],
    lastUpdate: "hace menos de 1 minuto",
    truckPos: { x: 21, y: 53 },
    destPos: { x: 48, y: 76 }
  }
];

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

        <div class="popover-dest">
          ${ICONS.pin}
        <div>
            <b>Destino:</b><br>
            ${del.destination}
          </div>
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
         
        </div>
      </div>

   

      <div class="delivery-dest">
        ${ICONS.pin}
        <div>
          <b>Destino:</b><br>
          ${del.destination}
        </div>
      </div>

      <div class="delivery-items">
        <span>Donaciones:</span>
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
