(function () {
  const TRUCK_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M10 17h4V5H10z"/><path d="M20 17h2v-7l-3-4h-3v11h4z"/><circle cx="7.5" cy="17.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></svg>`;
  const PIN_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>`;

  // false: el camión se ubica en su último avance real (o en el depósito si no hay).
  // true: recorre la ruta solo, útil para demos sin GPS.
  const SIMULAR_AVANCE = false;
  const DURACION_SIMULADA_MS = 120000;

  const ESTADOS = {
    PENDIENTE:   { texto: "Pendiente",   clase: "status-orange" },
    EN_CAMINO:   { texto: "En traslado", clase: "status-blue" },
    ENTREGADA:   { texto: "Entregada",   clase: "status-green" },
    NO_RECIBIDA: { texto: "No recibida", clase: "status-red" }
  };
  const estadoInfo = (e) => ESTADOS[e] || { texto: e, clase: "status-orange" };

  const mapEl = document.getElementById("mapViewport");
  if (!mapEl || typeof L === "undefined") return;

  const map = L.map(mapEl, { zoomControl: true }).setView([-34.6037, -58.3816], 12);
  L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener">OpenStreetMap</a>'
  }).addTo(map);

  const layer = L.layerGroup().addTo(map);
  let seguimientos = [];
  let inicio = Date.now();

  const esc = (s) => String(s ?? "").replace(/[&<>"']/g, c =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

  const truckIcon = L.divIcon({
    className: "lf-marker lf-truck", html: TRUCK_SVG,
    iconSize: [38, 38], iconAnchor: [19, 19], popupAnchor: [0, -22]
  });
  const destIcon = L.divIcon({
    className: "lf-marker lf-dest", html: PIN_SVG,
    iconSize: [34, 34], iconAnchor: [17, 17], popupAnchor: [0, -20]
  });

  /* ---------- "hace X segundos / minutos / horas" ---------- */
  function tiempoRelativo(ts) {
    const ms = new Date(ts).getTime();
    if (isNaN(ms)) return "";
    const seg = Math.max(0, Math.floor((Date.now() - ms) / 1000));
    if (seg < 60) return seg <= 5 ? "hace unos segundos" : `hace ${seg} segundos`;
    const min = Math.floor(seg / 60);
    if (min < 60) return min === 1 ? "hace 1 minuto" : `hace ${min} minutos`;
    const h = Math.floor(min / 60);
    if (h < 24) return h === 1 ? "hace 1 hora" : `hace ${h} horas`;
    const d = Math.floor(h / 24);
    return d === 1 ? "hace 1 día" : `hace ${d} días`;
  }

  function actualizarTiempos() {
    document.querySelectorAll("[data-ultimo-avance]").forEach(el => {
      el.textContent = tiempoRelativo(el.dataset.ts);
    });
  }

  function aplicarEstados() {
    document.querySelectorAll("[data-estado]").forEach(el => {
      const est = estadoInfo(el.dataset.estado);
      el.textContent = est.texto;
      el.classList.add(est.clase);
    });
  }

  /* ---------- datos ---------- */
  function normalizar(e, i) {
    const ruta = (e.rutaCompleta || [])
        .filter(p => p.latitud != null && p.longitud != null)
        .map(p => [p.latitud, p.longitud]);
    const destino = e.destino ? [e.destino.latitud, e.destino.longitud] : ruta[ruta.length - 1];
    const av = e.ultimoAvance && e.ultimoAvance.posicion ? e.ultimoAvance : null;
    return {
      id: e.entregaId, numero: i + 1, estado: e.estadoActual, direccion: e.direccion,
      ruta, destino, subcategorias: e.subcategorias || [],
      real: av ? [av.posicion.latitud, av.posicion.longitud] : null,
      ultimaActualizacion: av ? av.fecha : null,
      fraccion: 0
    };
  }

  function indiceMasCercano(ruta, p) {
    let mejor = 0, mejorD = Infinity;
    ruta.forEach(([lat, lon], i) => {
      const d = (lat - p[0]) ** 2 + (lon - p[1]) ** 2;
      if (d < mejorD) { mejorD = d; mejor = i; }
    });
    return mejor;
  }

  // Dónde está el camión y qué parte de la ruta lleva hecha
  function calcularPosicion(s) {
    if (SIMULAR_AVANCE) {
      const f = Math.min((Date.now() - inicio) / DURACION_SIMULADA_MS, 1);
      const pos = f * (s.ruta.length - 1);
      const i = Math.min(Math.floor(pos), s.ruta.length - 2);
      const t = pos - i, a = s.ruta[i], b = s.ruta[i + 1];
      return { indice: i, punto: [a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t], fraccion: f };
    }
    if (s.real) {
      const i = indiceMasCercano(s.ruta, s.real);
      return { indice: i, punto: s.real, fraccion: i / (s.ruta.length - 1) };
    }
    return { indice: 0, punto: s.ruta[0], fraccion: 0 };
  }

  function popupHtml(s) {
    const est = estadoInfo(s.estado);
    const subs = s.subcategorias.length
        ? `<div class="popover-row"><span>Donaciones:</span> <b>${s.subcategorias.map(esc).join(", ")}</b></div>` : "";
    const actualizacion = s.ultimaActualizacion
        ? `<div class="popover-time">Último avance:
             <b data-ultimo-avance data-ts="${esc(s.ultimaActualizacion)}">${tiempoRelativo(s.ultimaActualizacion)}</b></div>`
        : `<div class="popover-time">Sin avances registrados todavía</div>`;
    return `
      <div class="popover-title"><span>${TRUCK_SVG} <b>Entrega ${s.numero}</b></span></div>
      ${subs}
      <div class="popover-row"><span>Estado:</span> <span class="badge-status ${est.clase}">${est.texto}</span></div>
      <div class="popover-dest">${PIN_SVG}<div><b>Destino:</b><br>${esc(s.direccion)}</div></div>
      <div class="popover-pct">${Math.round(s.fraccion * 100)}% completado</div>
      ${actualizacion}`;
  }

  function actualizar(s) {
    const { indice, punto, fraccion } = calcularPosicion(s);
    s.fraccion = fraccion;
    s.camion.setLatLng(punto);
    s.hecho.setLatLngs([...s.ruta.slice(0, indice + 1), punto]);       // tramo recorrido (sólido)
    s.pendiente.setLatLngs([punto, ...s.ruta.slice(indice + 1)]);      // tramo faltante (punteado)

    if (s.card) {
      const pct = Math.round(fraccion * 100);
      const fill = s.card.querySelector("[data-progress-fill]");
      const label = s.card.querySelector("[data-progress-label]");
      if (fill) fill.style.width = pct + "%";
      if (label) label.textContent = pct + "% del recorrido";
    }
  }

  function render(entregas) {
    layer.clearLayers();
    seguimientos = entregas.map(normalizar).filter(s => s.ruta.length > 1);
    const bounds = [];

    seguimientos.forEach(s => {
      s.card = document.querySelector(`[data-entrega-id="${s.id}"]`);
      s.hecho = L.polyline([], { color: "#3b82f6", weight: 4, opacity: 0.9 }).addTo(layer);
      s.pendiente = L.polyline(s.ruta, { color: "#3b82f6", weight: 3, dashArray: "6 6", opacity: 0.8 }).addTo(layer);

      // Punto de partida: primer punto de la ruta (el depósito)
      L.circleMarker(s.ruta[0], {
        radius: 8, color: "#3b82f6", weight: 5, fillColor: "#ffffff", fillOpacity: 1, interactive: false
      }).addTo(layer);

      // Pin de destino: es el que abre el popup
      s.destMarker = L.marker(s.destino, { icon: destIcon, title: s.direccion })
          .bindPopup(() => popupHtml(s), { className: "tracking-popup", minWidth: 250, maxWidth: 250 })
          .addTo(layer);

      // Camión: solo se ve, no responde al click
      s.camion = L.marker(s.ruta[0], {
        icon: truckIcon, title: `Entrega ${s.numero}`, zIndexOffset: 500, interactive: false
      }).addTo(layer);

      bounds.push(...s.ruta);
    });

    if (bounds.length) map.fitBounds(bounds, { padding: [60, 60], maxZoom: 16 });
    inicio = Date.now();
    seguimientos.forEach(actualizar);
  }

  // Click en una tarjeta del panel lateral -> centra el mapa en ese camión
  document.querySelectorAll("[data-entrega-id]").forEach(card => {
    card.addEventListener("click", () => {
      const s = seguimientos.find(x => x.id === card.dataset.entregaId);
      if (s) {
        map.setView(s.destMarker.getLatLng(), Math.max(map.getZoom(), 14));
        s.destMarker.openPopup();
      }
    });
  });

  aplicarEstados();
  render(Array.isArray(window.ENTREGAS_ACTIVAS) ? window.ENTREGAS_ACTIVAS : []);
  actualizarTiempos();

  async function refrescar() {
    try {
      if (!window.SEGUIMIENTO_URL) return;
      const r = await fetch(window.SEGUIMIENTO_URL, { headers: { Accept: "application/json" } });
      if (!r.ok) return;
      const datos = await r.json();

      datos.forEach(e => {
        const s = seguimientos.find(x => x.id === e.entregaId);
        if (!s) return;

        const av = e.ultimoAvance && e.ultimoAvance.posicion ? e.ultimoAvance : null;
        s.real = av ? [av.posicion.latitud, av.posicion.longitud] : null;
        s.ultimaActualizacion = av ? av.fecha : null;
        s.estado = e.estadoActual;

        actualizar(s); // mueve el camión y los tramos sólido/punteado, y la barra de la tarjeta

        const footer = s.card && s.card.querySelector(".delivery-footer");
        if (footer) {
          footer.innerHTML = s.ultimaActualizacion
              ? `Último avance: <b data-ultimo-avance data-ts="${esc(s.ultimaActualizacion)}"></b>`
              : "Sin avances registrados todavía";
        }

        if (s.destMarker.isPopupOpen()) s.destMarker.getPopup().update();
      });
      actualizarTiempos();
    } catch (e) {

    }
  }

  setInterval(refrescar, 5000);

  setInterval(actualizarTiempos, 1000);
  if (SIMULAR_AVANCE) setInterval(() => seguimientos.forEach(actualizar), 250);

  window.actualizarSeguimiento = render;
})();