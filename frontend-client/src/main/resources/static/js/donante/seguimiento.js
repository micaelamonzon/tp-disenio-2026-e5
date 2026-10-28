(function () {
  const TRUCK_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M10 17h4V5H10z"/><path d="M20 17h2v-7l-3-4h-3v11h4z"/><circle cx="7.5" cy="17.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></svg>`;
  const PIN_SVG = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>`;



  const ESTADOS = {
    PENDIENTE:   { texto: "Pendiente",   clase: "status-orange" },
    EN_TRASLADO:   { texto: "En traslado", clase: "status-blue" },
    LLEGO:       { texto: "Llegó, falta confirmar", clase: "status-blue" },
    ENTREGADA:   { texto: "Entregada",   clase: "status-green" }
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
  let camiones = [];
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

  function aplicarEstadoABadge(badge, estado) {
    const est = estadoInfo(estado);
    Object.values(ESTADOS).forEach(x => badge.classList.remove(x.clase));
    badge.dataset.estado = estado;
    badge.textContent = est.texto;
    badge.classList.add(est.clase);
  }

  function aplicarEstados() {
    document.querySelectorAll("[data-estado]").forEach(el => aplicarEstadoABadge(el, el.dataset.estado));
  }


  function indiceMasCercano(ruta, p) {
    let mejor = 0, mejorD = Infinity;
    ruta.forEach(([lat, lon], i) => {
      const d = (lat - p[0]) ** 2 + (lon - p[1]) ** 2;
      if (d < mejorD) { mejorD = d; mejor = i; }
    });
    return mejor;
  }


  function aplicarAvance(c, av) {
    if (!av || !av.posicion) return;
    if (c.ultimaActualizacion && new Date(av.fecha) <= new Date(c.ultimaActualizacion)) return;
    c.real = [av.posicion.latitud, av.posicion.longitud];
    c.indiceReal = av.indiceRuta ?? null;
    c.ultimaActualizacion = av.fecha;
  }


  function construir(entregas) {
    const porCamion = new Map();
    entregas.forEach((e, i) => {
      const ruta = (e.rutaCompleta || [])
          .filter(p => p.latitud != null && p.longitud != null)
          .map(p => [p.latitud, p.longitud]);
      if (ruta.length < 2) return;

      const clave = e.camionId || ("entrega-" + e.entregaId);
      let c = porCamion.get(clave);
      if (!c) {
        c = { clave, ruta, real: null, indiceReal: null, ultimaActualizacion: null,
          indice: 0, fraccion: 0, paradas: [] };
        porCamion.set(clave, c);
      }
      c.paradas.push({
        id: e.entregaId, numero: i + 1, estado: e.estadoActual, direccion: e.direccion,
        destino: e.destino ? [e.destino.latitud, e.destino.longitud] : ruta[ruta.length - 1],
        subcategorias: e.subcategorias || [],
        indiceParada: e.indiceParada ?? ruta.length - 1,
        fraccion: 0, llego: false, camion: c
      });
      aplicarAvance(c, e.ultimoAvance);
    });
    return [...porCamion.values()];
  }

  function calcularPosicion(c) {
    const n = c.ruta.length - 1;
    if (c.real) {
      const i = c.indiceReal != null ? Math.min(c.indiceReal, n) : indiceMasCercano(c.ruta, c.real);
      return { indice: i, punto: c.real, fraccion: i / n };
    }
    return { indice: 0, punto: c.ruta[0], fraccion: 0 };
  }


  function popupHtml(p) {
    const est = estadoInfo(p.estado);
    const c = p.camion;
    const subs = p.subcategorias.length
        ? `<div class="popover-row"><span>Donaciones:</span> <b>${p.subcategorias.map(esc).join(", ")}</b></div>` : "";
    const avance = p.llego ? "El camión llegó a este destino"
        : `${Math.round(p.fraccion * 100)}% del recorrido`;
    const actualizacion = c.ultimaActualizacion
        ? `<div class="popover-time">Último avance:
             <b data-ultimo-avance data-ts="${esc(c.ultimaActualizacion)}">${tiempoRelativo(c.ultimaActualizacion)}</b></div>`
        : `<div class="popover-time">Sin avances registrados todavía</div>`;
    return `
      <div class="popover-title"><span>${TRUCK_SVG} <b>Entrega ${p.numero}</b></span></div>
      ${subs}
      <div class="popover-row"><span>Estado:</span> <span class="badge-status ${est.clase}">${est.texto}</span></div>
      <div class="popover-dest">${PIN_SVG}<div><b>Destino:</b><br>${esc(p.direccion)}</div></div>
      <div class="popover-pct">${avance}</div>
      ${actualizacion}`;
  }

  function actualizarParada(p) {
    const c = p.camion;
    p.llego = c.indice >= p.indiceParada;
    //if (p.llego) p.estado = "ENTREGADA";
    //cada pantalla puede indicar que mostrar al llegar (la entidad todavia tiene que confirmar)
    if (p.llego) p.estado = window.ESTADO_AL_LLEGAR || "ENTREGADA";
    p.fraccion = p.llego ? 1 : Math.min(c.indice / Math.max(p.indiceParada, 1), 1);

    if (!p.card) return;


    const badge = p.card.querySelector("[data-estado]");
    if (badge) aplicarEstadoABadge(badge, p.estado);

    const pct = Math.round(p.fraccion * 100);
    const fill = p.card.querySelector("[data-progress-fill]");
    const label = p.card.querySelector("[data-progress-label]");
    if (fill) fill.style.width = pct + "%";
    if (label) label.textContent = p.llego ? "El camión llegó a este destino" : pct + "% del recorrido";
  }

  function actualizar(c) {
    const { indice, punto, fraccion } = calcularPosicion(c);
    c.indice = indice;
    c.fraccion = fraccion;
    c.marker.setLatLng(punto);
    c.hecho.setLatLngs([...c.ruta.slice(0, indice + 1), punto]);
    c.pendiente.setLatLngs([punto, ...c.ruta.slice(indice + 1)]);
    c.paradas.forEach(actualizarParada);
  }

  function render(entregas) {
    layer.clearLayers();
    camiones = construir(entregas);
    const bounds = [];

    camiones.forEach(c => {
      c.hecho = L.polyline([], { color: "#3b82f6", weight: 4, opacity: 0.9 }).addTo(layer);
      c.pendiente = L.polyline(c.ruta, { color: "#3b82f6", weight: 3, dashArray: "6 6", opacity: 0.8 }).addTo(layer);

      // Punto de partida: primer punto de la ruta (el depósito)
      L.circleMarker(c.ruta[0], {
        radius: 8, color: "#3b82f6", weight: 5, fillColor: "#ffffff", fillOpacity: 1, interactive: false
      }).addTo(layer);

      // Un pin por entrega: es el que abre el popup
      c.paradas.forEach(p => {
        p.card = document.querySelector(`[data-entrega-id="${p.id}"]`);
        p.destMarker = L.marker(p.destino, { icon: destIcon, title: p.direccion })
            .bindPopup(() => popupHtml(p), { className: "tracking-popup", minWidth: 250, maxWidth: 250 })
            .addTo(layer);
      });

      // Un solo camión por ruta: solo se ve, no responde al click
      c.marker = L.marker(c.ruta[0], { icon: truckIcon, zIndexOffset: 500, interactive: false }).addTo(layer);

      bounds.push(...c.ruta);
    });

    if (bounds.length) map.fitBounds(bounds, { padding: [60, 60], maxZoom: 16 });
    inicio = Date.now();
    camiones.forEach(actualizar);
  }


  document.querySelectorAll("[data-entrega-id]").forEach(card => {
    card.addEventListener("click", () => {
      const p = camiones.flatMap(c => c.paradas).find(x => x.id === card.dataset.entregaId);
      if (p) {
        map.setView(p.destMarker.getLatLng(), Math.max(map.getZoom(), 14));
        p.destMarker.openPopup();
      }
    });
  });

  aplicarEstados();
  render(Array.isArray(window.ENTREGAS_ACTIVAS) ? window.ENTREGAS_ACTIVAS : []);
  actualizarTiempos();


  function recorridoTerminado() {
    return camiones.length > 0 &&
        camiones.every(c => c.indiceReal != null && c.indiceReal >= c.ruta.length - 1);
  }

  async function refrescar() {
    if (!window.SEGUIMIENTO_URL) return;
    try {
      const r = await fetch(window.SEGUIMIENTO_URL, { headers: { Accept: "application/json" } });
      if (!r.ok) return;
      const datos = await r.json();
      const paradas = camiones.flatMap(c => c.paradas);

      datos.forEach(e => {
        const p = paradas.find(x => x.id === e.entregaId);
        if (!p) return;
        p.estado = e.estadoActual;
        aplicarAvance(p.camion, e.ultimoAvance);
      });

      camiones.forEach(actualizar);

      paradas.forEach(p => {
        if (p.card) {
          const badge = p.card.querySelector("[data-estado]");
          if (badge) aplicarEstadoABadge(badge, p.estado);
          const footer = p.card.querySelector(".delivery-footer");
          if (footer) {
            footer.innerHTML = p.camion.ultimaActualizacion
                ? `Último avance: <b data-ultimo-avance data-ts="${esc(p.camion.ultimaActualizacion)}"></b>`
                : "Sin avances registrados todavía";
          }
        }
        if (p.destMarker.isPopupOpen()) p.destMarker.getPopup().update();
      });
      actualizarTiempos();

      if (recorridoTerminado()) clearInterval(timerRefresco);
    } catch (e) {

    }
  }
  const timerRefresco = setInterval(refrescar, 5000);
  if (recorridoTerminado()) clearInterval(timerRefresco);
  setInterval(actualizarTiempos, 1000);


  window.actualizarSeguimiento = render;
})();