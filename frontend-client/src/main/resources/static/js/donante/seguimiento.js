/* =====================================================================
 * Seguimiento de entregas - mapa OpenStreetMap (Leaflet)
 *
 * Espera una lista global `window.ENTREGAS_ACTIVAS` (la inyecta Thymeleaf):
 * [{
 *   id: "DON-001",
 *   truckId: "CAMION-A1",
 *   status: "En traslado",
 *   destination: "Centro Comunitario San Martín, Av. Rivadavia 1234, CABA",
 *   destLat: -34.6095, destLng: -58.3925,
 *   progress: 45,                       // opcional
 *   lastUpdate: "hace menos de 1 minuto",
 *   posiciones: [ {latitud: -34.60, longitud: -58.38}, ... ]   // PosicionDTO, en orden
 * }]
 * La última posición de la lista = ubicación actual del camión.
 * ===================================================================== */
(function () {
  const TRUCK_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M10 17h4V5H10z"/><path d="M20 17h2v-7l-3-4h-3v11h4z"/><circle cx="7.5" cy="17.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></svg>`;
  const PIN_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>`;

  // Datos de prueba: se usan solo si el backend todavía no inyecta nada
  const DEMO = [
    {
      id: "DON-001", truckId: "CAMION-A1", status: "En traslado",
      destination: "Centro Comunitario San Martín, Av. Rivadavia 1234, CABA",
      destLat: -34.6095, destLng: -58.3925, progress: 45,
      lastUpdate: "hace menos de 1 minuto",
      posiciones: [
        { latitud: -34.6400, longitud: -58.4300 },
        { latitud: -34.6330, longitud: -58.4150 },
        { latitud: -34.6250, longitud: -58.4050 },
        { latitud: -34.6180, longitud: -58.3990 }
      ]
    },
    {
      id: "DON-002", truckId: "CAMION-B2", status: "En traslado",
      destination: "Fundación Esperanza, Calle Bolívar 567, Buenos Aires",
      destLat: -34.6125, destLng: -58.3735, progress: 70,
      lastUpdate: "hace menos de 1 minuto",
      posiciones: [
        { latitud: -34.5800, longitud: -58.4200 },
        { latitud: -34.5900, longitud: -58.4000 },
        { latitud: -34.6000, longitud: -58.3850 }
      ]
    }
  ];

  const mapEl = document.getElementById("mapViewport");
  if (!mapEl || typeof L === "undefined") return;

  const map = L.map(mapEl, { zoomControl: true }).setView([-34.6037, -58.3816], 12);
  L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener">OpenStreetMap</a>'
  }).addTo(map);

  const layer = L.layerGroup().addTo(map);
  const truckMarkers = {}; // truckId -> marker (para enfocar desde el panel lateral)

  const esc = (s) => String(s ?? "").replace(/[&<>"']/g, c =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

  const truckIcon = L.divIcon({
    className: "lf-marker lf-truck",
    html: TRUCK_SVG, iconSize: [38, 38], iconAnchor: [19, 19], popupAnchor: [0, -22]
  });
  const destIcon = L.divIcon({
    className: "lf-marker lf-dest",
    html: PIN_SVG, iconSize: [34, 34], iconAnchor: [17, 17]
  });

  function popupHtml(e) {
    const pct = e.progress != null
        ? `<div class="popover-pct">${esc(e.progress)}% completado</div>` : "";
    return `
      <div class="popover-title"><span>${TRUCK_SVG} <b>${esc(e.truckId)}</b></span></div>
      <div class="popover-row"><span>Donación:</span> <b>${esc(e.id)}</b></div>
      <div class="popover-row"><span>Estado:</span> <span class="badge-status status-orange">${esc(e.status)}</span></div>
      <div class="popover-dest">${PIN_SVG}<div><b>Destino:</b><br>${esc(e.destination)}</div></div>
      ${pct}
      <div class="popover-time">Última actualización: ${esc(e.lastUpdate)}</div>`;
  }

  function render(entregas) {
    layer.clearLayers();
    Object.keys(truckMarkers).forEach(k => delete truckMarkers[k]);
    const bounds = [];

    entregas.forEach(e => {
      const recorrido = (e.posiciones || [])
          .filter(p => p.latitud != null && p.longitud != null)
          .map(p => [p.latitud, p.longitud]);
      const tieneDestino = e.destLat != null && e.destLng != null;
      const destino = tieneDestino ? [e.destLat, e.destLng] : null;

      // Recorrido ya hecho por el camión
      if (recorrido.length > 1) {
        L.polyline(recorrido, { color: "#3b82f6", weight: 4, opacity: 0.9 }).addTo(layer);
        bounds.push(...recorrido);
      }
      // Punto de inicio de la ruta
      if (recorrido.length > 0) {
        L.circleMarker(recorrido[0], {
          radius: 8,
          color: "#3b82f6",     // anillo azul
          weight: 5,
          fillColor: "#ffffff", // centro blanco
          fillOpacity: 1,
          interactive: false
        }).addTo(layer);
      }

      // Tramo pendiente: última posición -> destino (punteado, como antes)
      const actual = recorrido[recorrido.length - 1];
      if (actual && destino) {
        L.polyline([actual, destino], {
          color: "#3b82f6", weight: 3, dashArray: "6 6", opacity: 0.8
        }).addTo(layer);
      }

      if (destino) {
        L.marker(destino, { icon: destIcon, title: e.destination }).addTo(layer);
        bounds.push(destino);
      }

      if (actual) {
        const m = L.marker(actual, { icon: truckIcon, title: `Camión ${e.truckId}`, zIndexOffset: 500 })
            .bindPopup(popupHtml(e), { className: "tracking-popup", minWidth: 250, maxWidth: 250 })
            .addTo(layer);
        truckMarkers[e.truckId] = m;
      }
    });

    if (bounds.length) map.fitBounds(bounds, { padding: [60, 60], maxZoom: 16 });
  }

  // Click en una tarjeta del panel lateral -> centra el mapa en ese camión
  document.querySelectorAll("[data-truck-id]").forEach(card => {
    card.addEventListener("click", () => {
      const m = truckMarkers[card.dataset.truckId];
      if (m) { map.setView(m.getLatLng(), Math.max(map.getZoom(), 15)); m.openPopup(); }
    });
  });

  const datos = Array.isArray(window.ENTREGAS_ACTIVAS) ? window.ENTREGAS_ACTIVAS : DEMO;
  render(datos);

  // Para "tiempo real" más adelante: exponé esto y llamalo con un fetch cada X segundos
  window.actualizarSeguimiento = render;
})();