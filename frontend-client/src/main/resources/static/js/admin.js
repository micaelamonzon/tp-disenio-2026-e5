/* ============================================================
   CONFIGURACIÓN DE LA API
   ============================================================
*/
const API_BASE = "http://localhost:8080/servicioDeDonaciones";

// Nombres de estado usados para filtrar GET /donaciones?estado=...
const ESTADOS = {
    EN_DEPOSITO: "EN_DEPOSITO",
    ASIGNADA: "ASIGNACION_REALIZADA",
    VENCIDA: "VENCIDA",
};

/* ---------- Helpers genéricos de fetch ---------- */
async function apiGet(path) {
    const res = await fetch(`${API_BASE}${path}`);
    if (!res.ok) throw new Error(`GET ${path} -> ${res.status}`);
    return res.status === 204 ? null : res.json();
}

async function apiPost(path, body) {
    const res = await fetch(`${API_BASE}${path}`, {
        method: "POST",
        headers: body ? { "Content-Type": "application/json" } : undefined,
        body: body ? JSON.stringify(body) : undefined,
    });
    if (!res.ok) throw new Error(`POST ${path} -> ${res.status}`);
    return res.status === 204 ? null : res.json().catch(() => null);
}

async function apiPatch(path, body) {
    const res = await fetch(`${API_BASE}${path}`, {
        method: "PATCH",
        headers: body ? { "Content-Type": "application/json" } : undefined,
        body: body ? JSON.stringify(body) : undefined,
    });
    if (!res.ok) throw new Error(`PATCH ${path} -> ${res.status}`);
    return res.status === 204 ? null : res.json().catch(() => null);
}

// Responsable de la acción (persona administradora logueada).
// REVISAR cuando exista login real / servicio de autenticación (Entrega 5).
const RESPONSABLE_ACTUAL_ID = "admin-demo";

/* ============================================================
   ICONOS
   ============================================================ */
const ICONS = {
    check: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>',
    x: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>',
};

/* ============================================================
   NAVEGACIÓN: TOPBAR (menú de usuario) + SIDEBAR
   ============================================================ */
const userMenuTrigger = document.getElementById("userMenuTrigger");
const userMenu = document.getElementById("userMenu");

function closeUserMenu() {
    userMenu.hidden = true;
    userMenuTrigger.setAttribute("aria-expanded", "false");
}

userMenuTrigger.addEventListener("click", (e) => {
    e.stopPropagation();
    const isOpen = !userMenu.hidden;
    isOpen ? closeUserMenu() : (userMenu.hidden = false, userMenuTrigger.setAttribute("aria-expanded", "true"));
});
document.addEventListener("click", (e) => {
    if (!userMenu.hidden && !userMenu.contains(e.target) && !userMenuTrigger.contains(e.target)) closeUserMenu();
});
document.addEventListener("keydown", (e) => { if (e.key === "Escape") closeUserMenu(); });
// El logout lo resuelve el <form th:action="@{/logout}"> del HTML (Spring Security).

const sidebarLinks = document.querySelectorAll(".sidebar-link");
const adminViews = document.querySelectorAll(".admin-view");
const adminShell = document.getElementById("adminShell");
const sidebarToggle = document.getElementById("sidebarToggle");
const sidebarOverlay = document.getElementById("sidebarOverlay");

// Cada vista se carga (fetch) cuando se abre, para que siempre muestre
// datos frescos del backend y no algo que haya quedado obsoleto.
// Camiones no necesita cargador: Thymeleaf ya renderizó la tabla en el servidor.
const cargadoresDeVista = {
    dashboard: cargarDashboard,
};

function activarVista(view) {
    if (!document.getElementById(`view-${view}`)) view = "dashboard";
    sidebarLinks.forEach(l => l.classList.toggle("active", l.dataset.view === view));
    adminViews.forEach(v => v.classList.toggle("active", v.id === `view-${view}`));
    adminShell.classList.remove("sidebar-open");
    history.replaceState(null, "", `#${view}`);
    window.scrollTo({ top: 0, behavior: "smooth" });
    if (cargadoresDeVista[view]) cargadoresDeVista[view]();
}

sidebarLinks.forEach(link => {
    link.addEventListener("click", () => activarVista(link.dataset.view));
});

sidebarToggle.addEventListener("click", () => adminShell.classList.toggle("sidebar-open"));
sidebarOverlay.addEventListener("click", () => adminShell.classList.remove("sidebar-open"));

/* ============================================================
   ADAPTADORES DE DATOS
   ============================================================
*/

let donantesCache = null;

async function getDonantesCache() {
    if (donantesCache) return donantesCache;
    donantesCache = new Map();
    try {
        const todos = await apiGet("/donantes");
        (todos || []).forEach(d => {
            const esJuridica = d.idJuridico != null;
            const id = esJuridica ? d.idJuridico : d.idHumano;
            if (id == null) return; // registro sin id de ningún tipo, no se puede resolver
            const tipoKey = esJuridica ? "JURIDICO" : "HUMANO";
            const nombreMostrado = esJuridica
                ? (d.razonSocial || `Donante jurídico #${id}`)
                : (`${d.nombre || ""} ${d.apellido || ""}`.trim() || `Donante #${id}`);
            donantesCache.set(`${tipoKey}-${id}`, nombreMostrado);
        });
    } catch (err) {
        console.error("No se pudo precargar la lista de donantes:", err);
    }
    return donantesCache;
}

function mapDonacion(raw, cacheDonantes) {
    const sub = raw.subcategoria || {};
    const bienes = raw.bienesDelMismoTipo || [];
    const primerBien = bienes[0] || {};
    const cantidadTotal = bienes.reduce((acc, b) => acc + (b.cantidad || 0), 0);

    // RESUELTO: TipoDonante.java define HUMANO / JURIDICO (sin la "A" final).
    const tipoDonanteKey = (raw.tipoDonante || "").toUpperCase() === "JURIDICO" ? "JURIDICO" : "HUMANO";
    const donanteKey = `${tipoDonanteKey}-${raw.idDelDonante}`;
    const nombreDonante = cacheDonantes?.get(donanteKey)
        || (raw.idDelDonante ? `Donante #${raw.idDelDonante}` : "Donante");

    return {
        id: raw.id,
        articulo: sub.nombre || primerBien.nombre || "Artículo sin nombre",
        categoria: sub.categoria?.nombre || "-",
        subcategoria: sub.nombre || "-",
        cantidad: cantidadTotal || primerBien.cantidad || 0,
        // Unidad.tipoUnidad es el enum KILOGRAMOS/LITROS/CENTIMETROS/METROS (Unidad.java).
        unidad: (primerBien.tipoUnidad || "").toString().toLowerCase(),
        estado: raw.estadoActual || "-",
        donante: nombreDonante,
        fechaIngreso: (raw.fechaDeIngreso || "").toString().slice(0, 10) || "-",
        vencimiento: (primerBien.fechaDeVencimiento || "").toString().slice(0, 10) || "N/A",
    };
}

/* ============================================================
   PANEL / DASHBOARD
   ============================================================ */
async function cargarDashboard() {
    const errorBox = document.getElementById("dashboardError");
    const lista = document.getElementById("dashboardRecientesList");
    errorBox.hidden = true;
    lista.innerHTML = `<div class="loading-inline"><span class="spinner"></span> Cargando donaciones recientes...</div>`;

    try {
        const [enDeposito, vencidas, asignadas] = await Promise.all([
            apiGet(`/donaciones?estado=${ESTADOS.EN_DEPOSITO}`),
            apiGet(`/donaciones?estado=${ESTADOS.VENCIDA}`).catch(() => []),
            apiGet(`/donaciones?estado=${ESTADOS.ASIGNADA}`).catch(() => []),
        ]);

        document.getElementById("statDisponibles").textContent = (enDeposito || []).length;
        document.getElementById("statVencidas").textContent = (vencidas || []).length;
        document.getElementById("statAsignadas").textContent = (asignadas || []).length;

        const cacheDonantes = await getDonantesCache();
        const recientes = [...(enDeposito || []), ...(asignadas || [])]
            .map(d => mapDonacion(d, cacheDonantes))
            .slice(0, 6);

        lista.innerHTML = recientes.length
            ? recientes.map(d => `
          <div class="reciente-item">
            <div>
              <div class="reciente-item-name">${d.articulo}</div>
              <div class="reciente-item-meta">Donante: ${d.donante}</div>
            </div>
            <div class="reciente-item-right">
              <div class="reciente-item-cant">${d.cantidad} ${d.unidad}</div>
              <span class="pill pill-${d.estado === ESTADOS.EN_DEPOSITO ? "Disponible" : "Asignada"}">${d.estado === ESTADOS.EN_DEPOSITO ? "Disponible" : "Asignada"}</span>
            </div>
          </div>`).join("")
            : `<p class="empty-state">No hay donaciones registradas todavía.</p>`;
    } catch (err) {
        console.error(err);
        errorBox.hidden = false;
        errorBox.textContent = "No se pudo conectar con el Servicio de Donaciones. Verificá que el backend esté corriendo en " + API_BASE + ".";
        lista.innerHTML = "";
    }
}

/* ============================================================
   REGISTRAR DONACIONES
   ============================================================ */
const donorModeRadios = document.querySelectorAll('input[name="donorMode"]');
const donorSearchBlock = document.getElementById("donorSearchBlock");
const donorNewBlock = document.getElementById("donorNewBlock");
const donorSearchInput = document.getElementById("donorSearchInput");
const donorSearchBtn = document.getElementById("donorSearchBtn");
const donorSearchResult = document.getElementById("donorSearchResult");

donorModeRadios.forEach(radio => {
    radio.addEventListener("change", () => {
        const esExistente = document.querySelector('input[name="donorMode"]:checked').value === "existente";
        donorSearchBlock.hidden = !esExistente;
        donorNewBlock.hidden = esExistente;
    });
});

// Búsqueda de donante existente contra GET /servicioDeDonaciones/donantes
// (obtenerTodosLosDonantesUnificados), sin endpoint dedicado, trayendo todo y filtrando en el navegador.

//   record PersonaDonanteDTO(Long idHumano, String nombre, String apellido,
//                             Long idJuridico, String razonSocial,
//                             List<DonacionIncentivosDTO> donaciones)
// No trae email, documento, ni un id/tipo unificado. Por eso la búsqueda
// solo puede filtrar por nombre / apellido / razón social (ver también
// la nota en el HTML, arriba del input). Si más adelante hace falta
// buscar por email o documento, ese dato tiene que agregarse al DTO.

donorSearchBtn.addEventListener("click", async () => {
    const query = donorSearchInput.value.trim().toLowerCase();
    donorSearchResult.hidden = false;
    if (!query) {
        donorSearchResult.className = "donor-search-result not-found";
        donorSearchResult.innerHTML = "Ingresá un nombre, apellido o razón social para buscar.";
        return;
    }
    donorSearchResult.className = "donor-search-result";
    donorSearchResult.innerHTML = `<span class="loading-inline"><span class="spinner"></span> Buscando...</span>`;
    try {
        const todos = await apiGet("/donantes");
        const coincidencias = (todos || []).filter(d => {
            const nombreCompleto = `${d.nombre || ""} ${d.apellido || ""}`.toLowerCase();
            const razonSocial = (d.razonSocial || "").toLowerCase();
            return nombreCompleto.includes(query) || razonSocial.includes(query);
        });
        const encontrado = coincidencias[0];

        if (encontrado) {
            const esJuridica = encontrado.idJuridico != null;
            const id = esJuridica ? encontrado.idJuridico : encontrado.idHumano;
            const nombreMostrado = esJuridica
                ? encontrado.razonSocial
                : `${encontrado.nombre || ""} ${encontrado.apellido || ""}`.trim();
            donorSearchResult.innerHTML = `
        <b>${nombreMostrado}</b> · ${esJuridica ? "Persona jurídica" : "Persona humana"}
        ${coincidencias.length > 1 ? `<br><span class="reciente-item-meta">Hay ${coincidencias.length} coincidencias, se tomó la primera. Afiná la búsqueda si no es la persona correcta.</span>` : ""}
      `;
            donorSearchResult.dataset.foundId = id;
            donorSearchResult.dataset.foundTipo = esJuridica ? "juridica" : "humana";
        } else {
            donorSearchResult.className = "donor-search-result not-found";
            donorSearchResult.innerHTML = "No se encontró ningún donante con ese nombre. Podés registrarlo como donante nuevo.";
            delete donorSearchResult.dataset.foundId;
        }
    } catch (err) {
        console.error(err);
        donorSearchResult.className = "donor-search-result not-found";
        donorSearchResult.innerHTML = "No se pudo consultar a los donantes. ¿Está corriendo el backend?";
    }
});

// Tipo de persona (humana / jurídica)
const tipoPersonaSelect = document.getElementById("tipoPersona");
const camposHumana = document.getElementById("camposHumana");
const camposJuridica = document.getElementById("camposJuridica");

tipoPersonaSelect.addEventListener("change", () => {
    const esJuridica = tipoPersonaSelect.value === "juridica";
    camposHumana.hidden = esJuridica;
    camposJuridica.hidden = !esJuridica;
});

// Lista dinámica de bienes
const bienesList = document.getElementById("bienesList");
const agregarBienBtn = document.getElementById("agregarBienBtn");
let bienIdCounter = 0;

const CATEGORIAS = {
    "Alimentos": ["Fideos secos", "Arroz", "Legumbres secas", "Aceite vegetal", "Leche en polvo", "Galletitas"],
    "Vestimenta": ["Ropa de abrigo", "Remeras", "Pantalones", "Ropa infantil"],
    "Mobiliario": ["Sillas", "Mesas", "Estantes"],
    "Abrigo": ["Frazadas", "Colchones"],
};
const CATEGORIAS_CON_ESTADO = ["Mobiliario", "Vestimenta"];
const CATEGORIAS_PERECEDERAS = ["Alimentos"];

const UNIDADES = [
    { value: "UNIDADES", label: "unidades" },
    { value: "KILOGRAMOS", label: "kilogramos" },
    { value: "LITROS", label: "litros" },
    { value: "CENTIMETROS", label: "centímetros" },
    { value: "METROS", label: "metros" },
];

const CATALOGO_BD = window.CATALOGO_SUBCATEGORIAS || {};

function nombresDeCategorias() {
    return [...new Set([...Object.keys(CATEGORIAS), ...Object.keys(CATALOGO_BD)])];
}
function subcategoriasDe(categoria) {
    return [...new Set([...(CATEGORIAS[categoria] || []), ...(CATALOGO_BD[categoria] || [])])];
}

function categoriaOptionsHTML(selected) {
    return `<option value="">Seleccionar categoría</option>` +
        nombresDeCategorias().map(cat => `<option value="${cat}" ${cat === selected ? "selected" : ""}>${cat}</option>`).join("");
}
function subcategoriaOptionsHTML(categoria) {
    const subs = subcategoriasDe(categoria);
    if (!subs.length) return `<option value="">Elegí primero una categoría</option>`;
    return `<option value="">Seleccionar subcategoría</option>` + subs.map(s => `<option value="${s}">${s}</option>`).join("");
}
function unidadOptionsHTML() {
    return UNIDADES.map(u => `<option value="${u.value}">${u.label}</option>`).join("");
}

function crearBienRow() {
    bienIdCounter++;
    const id = bienIdCounter;
    const wrapper = document.createElement("div");
    wrapper.className = "bien-item";
    wrapper.dataset.bienId = id;
    wrapper.innerHTML = `
    <div class="bien-item-head">
      <span>Bien #${id}</span>
      <button type="button" class="btn-remove-bien" data-remove="${id}">${ICONS.x} Quitar</button>
    </div>
    <div class="fields-grid">
      <label class="field field-grow">
        <span>Descripción del bien *</span>
        <input type="text" class="bien-descripcion" placeholder="Ej: Arroz, Sillas de madera, Camperas talle M">
      </label>
      <label class="field">
        <span>Categoría *</span>
        <select class="bien-categoria">${categoriaOptionsHTML()}</select>
      </label>
      <label class="field">
        <span>Subcategoría *</span>
        <select class="bien-subcategoria">${subcategoriaOptionsHTML(null)}</select>
      </label>
      <label class="field">
        <span>Cantidad *</span>
        <input type="number" class="bien-cantidad" min="1" placeholder="Ej: 50">
      </label>
      <label class="field">
        <span>Unidad</span>
        <select class="bien-unidad">${unidadOptionsHTML()}</select>
      </label>
      <label class="field bien-condicion-field" hidden>
        <span>Condición</span>
        <select class="bien-condicion">
          <option value="Nuevo">Nuevo</option>
          <option value="Usado">Usado</option>
        </select>
      </label>
      <label class="field bien-vencimiento-field" hidden>
        <span>Fecha de vencimiento</span>
        <input type="date" class="bien-vencimiento">
      </label>
      <label class="field">
        <span>Foto (opcional)</span>
        <input type="file" class="bien-foto" accept="image/*">
      </label>
    </div>
  `;

    const categoriaSelect = wrapper.querySelector(".bien-categoria");
    const subcategoriaSelect = wrapper.querySelector(".bien-subcategoria");
    const condicionField = wrapper.querySelector(".bien-condicion-field");
    const vencimientoField = wrapper.querySelector(".bien-vencimiento-field");

    categoriaSelect.addEventListener("change", () => {
        subcategoriaSelect.innerHTML = subcategoriaOptionsHTML(categoriaSelect.value);
        condicionField.hidden = !CATEGORIAS_CON_ESTADO.includes(categoriaSelect.value);
        vencimientoField.hidden = !CATEGORIAS_PERECEDERAS.includes(categoriaSelect.value);
    });

    wrapper.querySelector(`[data-remove="${id}"]`).addEventListener("click", () => {
        if (bienesList.children.length <= 1) return;
        wrapper.remove();
        actualizarBotonesQuitar();
    });

    return wrapper;
}

function actualizarBotonesQuitar() {
    const soloUno = bienesList.children.length <= 1;
    bienesList.querySelectorAll(".btn-remove-bien").forEach(btn => { btn.disabled = soloUno; });
}

agregarBienBtn.addEventListener("click", () => {
    bienesList.appendChild(crearBienRow());
    actualizarBotonesQuitar();
});

bienesList.appendChild(crearBienRow());
actualizarBotonesQuitar();

const registrarForm = document.getElementById("registrarForm");
const registrarError = document.getElementById("registrarError");
const segmentacionResumen = document.getElementById("segmentacionResumen");
const submitBtn = registrarForm.querySelector('button[type="submit"]');

function mostrarErrorFormulario(msg) {
    registrarError.hidden = false;
    registrarError.textContent = msg;
    registrarError.scrollIntoView({ behavior: "smooth", block: "center" });
}

// --- Helpers de mapeo hacia los DTOs  ---

const TIPO_NOTIFICACION = { email: "EMAIL", telefono: "SMS", whatsapp: "WHATSAPP" };

function construirMediosDeNotificacion({ email, telefono, whatsapp }) {
    const medios = [];
    if (email) medios.push({ tipoDeNotificacion: TIPO_NOTIFICACION.email, datoDeContacto: email });
    if (telefono) medios.push({ tipoDeNotificacion: TIPO_NOTIFICACION.telefono, datoDeContacto: telefono });
    if (whatsapp) medios.push({ tipoDeNotificacion: TIPO_NOTIFICACION.whatsapp, datoDeContacto: whatsapp });
    return medios;
}

function mapTipoPersonaJuridica(valorSelect) {
    const normalizado = (valorSelect || "")
        .toUpperCase()
        .normalize("NFD").replace(/[\u0300-\u036f]/g, ""); // saca tildes (ej. "Institución" -> "INSTITUCION")
    return normalizado || null;
}

// Convertimos "yyyy-mm-dd" (input type=date) a un LocalDateTime ISO que
// Jackson puede parsear ("yyyy-mm-ddT00:00:00").
function fechaInputALocalDateTime(fechaStr) {
    if (!fechaStr) return null;
    return `${fechaStr}T00:00:00`;
}

registrarForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    registrarError.hidden = true;
    segmentacionResumen.hidden = true;

    const modoExistente = document.querySelector('input[name="donorMode"]:checked').value === "existente";
    const esJuridica = tipoPersonaSelect.value === "juridica";
    const bienes = [];
    for (const row of bienesList.children) {
        const descripcionLibre = row.querySelector(".bien-descripcion").value.trim();
        const categoria = row.querySelector(".bien-categoria").value;
        const subcategoria = row.querySelector(".bien-subcategoria").value;
        const cantidad = row.querySelector(".bien-cantidad").value;
        const unidad = row.querySelector(".bien-unidad").value;
        const vencimiento = row.querySelector(".bien-vencimiento").value;
        const condicionSelect = row.querySelector(".bien-condicion");
        const condicionVisible = !row.querySelector(".bien-condicion-field").hidden;
        const vencimientoVisible = !row.querySelector(".bien-vencimiento-field").hidden;

        if (!descripcionLibre || !categoria || !subcategoria || !cantidad) {
            return mostrarErrorFormulario("Completá descripción, categoría, subcategoría y cantidad en todos los bienes.");
        }
        if (vencimientoVisible && !vencimiento) {
            return mostrarErrorFormulario("Esta categoría es perecedera: ingresá la fecha de vencimiento.");
        }

        bienes.push({
            nombre: descripcionLibre,
            descripcion: descripcionLibre,
            subcategoria: {
                nombre: subcategoria,
                esPerecedero: CATEGORIAS_PERECEDERAS.includes(categoria),
                categoria: { nombre: categoria },
            },
            fechaDeVencimiento: vencimientoVisible ? fechaInputALocalDateTime(vencimiento) : null,
            // EstadoDeUso.NUEVO / EstadoDeUso.USADO (mayúsculas), a partir de "Nuevo"/"Usado" del <select>.
            esUsado: condicionVisible ? (condicionSelect.value || "").toUpperCase() : null,
            tipoUnidad: unidad || null,
            cantidad: Number(cantidad),
        });
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "Registrando...";

    try {
        let donanteId;
        let tipoDelDonante;

        if (modoExistente) {
            if (!donorSearchResult.dataset.foundId) {
                throw new Error("Buscá y confirmá un donante existente antes de registrar la donación.");
            }
            donanteId = donorSearchResult.dataset.foundId;
            tipoDelDonante = donorSearchResult.dataset.foundTipo;
        } else {
            const email = document.getElementById("donEmail").value.trim();
            if (!email) throw new Error("El email del donante es obligatorio.");
            const telefono = document.getElementById("donTelefono").value.trim();
            const whatsapp = document.getElementById("donWhatsapp").value.trim();
            const mediosDeNotificacion = construirMediosDeNotificacion({ email, telefono, whatsapp });

            let nuevoDonante;
            if (esJuridica) {
                const razonSocial = document.getElementById("jurRazonSocial").value.trim();
                const cuit = document.getElementById("jurCuit").value.trim();
                if (!razonSocial) throw new Error("La razón social es obligatoria.");
                if (!cuit) throw new Error("El CUIT es obligatorio.");
                // "direccion" sigue sin existir en el DTO, así que ese campo del form todavía no se envía.
                const repNombre = document.getElementById("jurRepresentanteNombre").value.trim();
                const repApellido = document.getElementById("jurRepresentanteApellido").value.trim();
                const repDni = document.getElementById("jurRepresentanteDni").value.trim();
                const representantes = repNombre
                    ? [{
                        nombre: repNombre,
                        apellido: repApellido || null,
                        numeroDeDocumento: Number(repDni) || null,
                        mediosDeNotificacion: [],
                    }]
                    : [];

                nuevoDonante = await apiPost("/donante/juridico", {
                    cuit,
                    razonSocial,
                    rubro: document.getElementById("jurRubro").value.trim() || null,
                    tipo: mapTipoPersonaJuridica(document.getElementById("jurTipo").value),
                    representantes,
                    mediosDeNotificacion,
                });
            } else {
                const nombre = document.getElementById("donNombre").value.trim();
                const apellido = document.getElementById("donApellido").value.trim();
                const documento = document.getElementById("donDocumento").value.trim();
                if (!nombre || !apellido || !documento) throw new Error("Nombre, apellido y documento son obligatorios.");
                const medioPredeterminadoKey = document.getElementById("donMedioPredeterminado").value; // "email"|"telefono"|"whatsapp"
                const medioPredeterminado = mediosDeNotificacion.find(
                    m => m.tipoDeNotificacion === TIPO_NOTIFICACION[medioPredeterminadoKey]
                ) || mediosDeNotificacion[0] || null;
                const edad = document.getElementById("donEdad").value.trim();

                nuevoDonante = await apiPost("/donante/humano", {
                    nombre,
                    apellido,
                    DNI: Number(documento) || null,
                    edad: edad ? Number(edad) : null,
                    genero: document.getElementById("donGenero").value || null,
                    direccion: document.getElementById("donDireccion").value.trim() || null,
                    mediosDeNotificacion,
                    medioDeNotificacionPredeterminado: medioPredeterminado,
                });
            }
            donanteId = nuevoDonante?.id;
            tipoDelDonante = esJuridica ? "juridica" : "humana";
            if (!donanteId) throw new Error("El donante se creó pero el backend no devolvió un id. Revisá la respuesta en la pestaña Network.");
            donantesCache = null; // fuerza a refrescar la caché de nombres en la próxima vista que la use
        }

        const path = tipoDelDonante === "juridica"
            ? `/juridico/donacion/${donanteId}`
            : `/humano/donacion/${donanteId}`;

        const donacionesSegmentadas = await apiPost(path, {
            descripcionGeneral: document.getElementById("descripcionGeneral").value.trim() || null,
            bienes,
            fechaDeIngreso: fechaInputALocalDateTime(new Date().toISOString().slice(0, 10)),
        });

        const lista = donacionesSegmentadas || [];
        segmentacionResumen.hidden = false;
        segmentacionResumen.innerHTML = `
      <h3>${ICONS.check} Donación registrada — el backend generó ${lista.length} donación${lista.length === 1 ? "" : "es"} segmentada${lista.length === 1 ? "" : "s"}</h3>
      <ul>
        ${lista.map(d => {
            const bienesDeLaDonacion = d.bienes || [];
            const cantidadTotal = bienesDeLaDonacion.reduce((acc, b) => acc + (b.cantidad || 0), 0);
            const primerBien = bienesDeLaDonacion[0] || {};
            const nombreMostrado = primerBien.subcategoria?.nombre || primerBien.nombre || "Donación";
            const unidadMostrada = (primerBien.tipoUnidad || "").toString().toLowerCase();
            return `<li><b>${nombreMostrado}</b> — ${cantidadTotal || "-"} ${unidadMostrada}</li>`;
        }).join("") || "<li>El backend no devolvió el detalle segmentado.</li>"}
      </ul>
    `;
        segmentacionResumen.scrollIntoView({ behavior: "smooth", block: "nearest" });

        registrarForm.reset();
        bienesList.innerHTML = "";
        bienesList.appendChild(crearBienRow());
        actualizarBotonesQuitar();
        camposHumana.hidden = false;
        camposJuridica.hidden = true;
        donorSearchResult.hidden = true;
    } catch (err) {
        console.error(err);
        mostrarErrorFormulario(err.message || "No se pudo registrar la donación. Verificá que el backend esté corriendo.");
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Registrar Donación";
    }
});

/* ============================================================
   INVENTARIO DE DEPÓSITO
   ============================================================ */
const inventoryTableBody = document.getElementById("inventoryTableBody");
const inventoryEmpty = document.getElementById("inventoryEmpty");
const inventorySearch = document.getElementById("inventorySearch");
const inventoryFilterEstado = document.getElementById("inventoryFilterEstado");

function filtrarInventario() {
    const query = inventorySearch.value.trim().toLowerCase();
    const estadoFiltro = inventoryFilterEstado.value;
    let visibles = 0;
    inventoryTableBody.querySelectorAll("tr[data-estado]").forEach(fila => {
        const coincide = (estadoFiltro === "todos" || fila.dataset.estado === estadoFiltro)
            && (!query || fila.dataset.articulo.includes(query));
        fila.hidden = !coincide;
        if (coincide) visibles++;
    });
    inventoryEmpty.hidden = visibles !== 0;
}
inventorySearch.addEventListener("input", filtrarInventario);
inventoryFilterEstado.addEventListener("change", filtrarInventario);
filtrarInventario();

/* ============================================================
   RANKING DE DONANTES — alternar pestañas
   ============================================================ */

// funcion para manejar la alternacnia de pestañas dentro de la secicon del ranking
function switchRankingView(tipo) {
    const btnActual = document.getElementById('tabRankingActual');
    const btnHistorico = document.getElementById('tabRankingHistorico');
    const contActual = document.getElementById('rankingActualContainer');
    const contHistorico = document.getElementById('rankingHistoricoContainer');

    if (tipo === 'actual') {
        btnActual.classList.add('active');
        btnHistorico.classList.remove('active');
        contActual.style.display = 'block';
        contHistorico.style.display = 'none';
    } else {
        btnHistorico.classList.add('active');
        btnActual.classList.remove('active');
        contHistorico.style.display = 'block';
        contActual.style.display = 'none';
    }
}

// Muestra "Procesando..." en los formularios que pueden tardar (ej. ejecutar matchmaking)
document.querySelectorAll("form[data-loading]").forEach(form => {
    form.addEventListener("submit", () => {
        const boton = form.querySelector("button[type=submit]");
        boton.disabled = true;
        boton.textContent = "Procesando...";
    });
});

/* ============================================================
   GESTIÓN DE CAMIONES
   La tabla y los contadores los renderiza Thymeleaf en el servidor;
   acá solo se muestra/oculta el formulario "Nuevo Camión".
   ============================================================ */
const nuevoCamionBtn = document.getElementById("nuevoCamionBtn");
const nuevoCamionCard = document.getElementById("nuevoCamionCard");
const cancelarCamionBtn = document.getElementById("cancelarCamionBtn");

function mostrarFormCamion(mostrar) {
    nuevoCamionCard.hidden = !mostrar;
    if (mostrar) nuevoCamionCard.querySelector("input")?.focus();
}
nuevoCamionBtn.addEventListener("click", () => mostrarFormCamion(nuevoCamionCard.hidden));
cancelarCamionBtn.addEventListener("click", () => {
    nuevoCamionCard.querySelector("form").reset();
    mostrarFormCamion(false);
});

/* Utilidad compartida */
function escapeHTML(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
    }[c]));
}

/* ============================================================
   IMPORTAR DONANTES (CSV)
   ============================================================ */
// Endpoint del frontend-client que recibe el archivo (multipart, campo "archivo").
// TODAVÍA NO EXISTE en AdminController: hay que crearlo.
const IMPORTAR_URL = "/personaAdministradora/importar";

const csvDropzone = document.getElementById("csvDropzone");
const csvInput = document.getElementById("csvInput");
const archivoElegido = document.getElementById("archivoElegido");
const archivoNombre = document.getElementById("archivoNombre");
const archivoTamano = document.getElementById("archivoTamano");
const importarBtn = document.getElementById("importarBtn");
const quitarArchivoBtn = document.getElementById("quitarArchivoBtn");
const importarError = document.getElementById("importarError");
const historialImportaciones = document.getElementById("historialImportaciones");

let archivoCSV = null;
let historial = [];

function formatearTamano(bytes) {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function mostrarErrorImportar(msg) {
    importarError.hidden = !msg;
    importarError.textContent = msg || "";
}

function limpiarArchivo() {
    archivoCSV = null;
    csvInput.value = "";
    archivoElegido.hidden = true;
}

function elegirArchivo(file) {
    mostrarErrorImportar("");
    if (!file) return;
    if (!/\.csv$/i.test(file.name)) {
        limpiarArchivo();
        mostrarErrorImportar("El archivo tiene que ser un .csv.");
        return;
    }
    archivoCSV = file;
    archivoNombre.textContent = file.name;
    archivoTamano.textContent = formatearTamano(file.size);
    archivoElegido.hidden = false;
}

function renderHistorial() {
    if (!historial.length) {
        historialImportaciones.innerHTML = `<p class="empty-state">Todavía no hay importaciones en esta sesión.</p>`;
        return;
    }
    historialImportaciones.innerHTML = historial.map(h => `
      <div class="history-item">
        <div>
          <div class="history-name">${escapeHTML(h.nombre)}</div>
          <div class="history-date">${escapeHTML(h.fecha)}</div>
        </div>
        <div class="history-right">
          ${h.registros != null ? `<div class="history-count">${Number(h.registros).toLocaleString("es-AR")} registros</div>` : ""}
          <div class="history-status">Completado</div>
        </div>
      </div>`).join("");
}

csvDropzone.addEventListener("click", () => csvInput.click());
csvDropzone.addEventListener("keydown", (e) => {
    if (e.key === "Enter" || e.key === " ") { e.preventDefault(); csvInput.click(); }
});
csvInput.addEventListener("change", () => elegirArchivo(csvInput.files[0]));
["dragenter", "dragover"].forEach(ev => csvDropzone.addEventListener(ev, (e) => {
    e.preventDefault();
    csvDropzone.classList.add("dragover");
}));
["dragleave", "drop"].forEach(ev => csvDropzone.addEventListener(ev, (e) => {
    e.preventDefault();
    csvDropzone.classList.remove("dragover");
}));
csvDropzone.addEventListener("drop", (e) => elegirArchivo(e.dataTransfer.files[0]));
quitarArchivoBtn.addEventListener("click", () => { limpiarArchivo(); mostrarErrorImportar(""); });

importarBtn.addEventListener("click", async () => {
    if (!archivoCSV) return;
    mostrarErrorImportar("");
    importarBtn.disabled = true;
    importarBtn.textContent = "Importando...";
    try {
        const datos = new FormData();
        datos.append("archivo", archivoCSV);

        // Si Spring Security tiene CSRF activo, el token viene en los <meta> del head.
        const headers = {};
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
        if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

        const res = await fetch(IMPORTAR_URL, { method: "POST", body: datos, headers });
        if (!res.ok) throw new Error(`POST ${IMPORTAR_URL} -> ${res.status}`);
        const respuesta = await res.json().catch(() => null);

        historial.unshift({
            nombre: archivoCSV.name,
            fecha: new Date().toLocaleString("es-AR", { dateStyle: "long", timeStyle: "short" }),
            registros: respuesta?.registros ?? respuesta?.cantidad ?? null,
        });
        renderHistorial();
        limpiarArchivo();
    } catch (err) {
        console.error(err);
        mostrarErrorImportar("No se pudo importar el archivo. Verificá que el servicio de importación esté disponible.");
    } finally {
        importarBtn.disabled = false;
        importarBtn.textContent = "Importar";
    }
});

/* ============================================================
   CONFIRMAR ELIMINACIÓN DE CAMIÓN (reemplaza el confirm() del navegador)
   ============================================================ */
const confirmOverlay = document.getElementById("confirmOverlay");
const confirmText = document.getElementById("confirmText");
const confirmAccept = document.getElementById("confirmAccept");
const confirmCancel = document.getElementById("confirmCancel");
let formPorConfirmar = null;

function abrirConfirmacion(form) {
    formPorConfirmar = form;
    const patente = form.dataset.patente;
    confirmText.textContent = patente
        ? `Vas a eliminar el camión ${patente}. Esta acción no se puede deshacer.`
        : "Vas a eliminar este camión. Esta acción no se puede deshacer.";
    confirmOverlay.hidden = false;
    confirmCancel.focus();
}
function cerrarConfirmacion() {
    confirmOverlay.hidden = true;
    formPorConfirmar = null;
}

if (confirmOverlay) {
    // Delegación: captura el envío de cualquier formulario de baja de camión.
    document.addEventListener("submit", (e) => {
        const form = e.target.closest && e.target.closest(".form-eliminar-camion");
        if (!form) return;
        e.preventDefault();
        abrirConfirmacion(form);
    });
    confirmAccept.addEventListener("click", () => {
        const form = formPorConfirmar;
        cerrarConfirmacion();
        if (form) form.submit(); // submit() no vuelve a disparar el evento "submit"
    });
    confirmCancel.addEventListener("click", cerrarConfirmacion);
    confirmOverlay.addEventListener("click", (e) => { if (e.target === confirmOverlay) cerrarConfirmacion(); });
    document.addEventListener("keydown", (e) => { if (e.key === "Escape" && !confirmOverlay.hidden) cerrarConfirmacion(); });
}

/* ============================================================
   INICIO
   ============================================================ */
// Abre la vista indicada en la URL (#camiones, #inventario...) o el dashboard.
activarVista(location.hash.slice(1) || "dashboard");