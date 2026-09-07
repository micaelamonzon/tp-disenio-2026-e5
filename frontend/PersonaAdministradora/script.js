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
// AJUSTAR cuando exista login real / servicio de autenticación (Entrega 5).
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
const logoutBtn = document.getElementById("logoutBtn");

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
    if (!userMenu.hidden && !userMenu.contains(e.target) && e.target !== userMenuTrigger) closeUserMenu();
});
document.addEventListener("keydown", (e) => { if (e.key === "Escape") closeUserMenu(); });
logoutBtn.addEventListener("click", () => {
    closeUserMenu();
    alert("Sesión cerrada");
});

const sidebarLinks = document.querySelectorAll(".sidebar-link");
const adminViews = document.querySelectorAll(".admin-view");
const adminShell = document.getElementById("adminShell");
const sidebarToggle = document.getElementById("sidebarToggle");
const sidebarOverlay = document.getElementById("sidebarOverlay");

// Cada vista se carga (fetch) cuando se abre, para que siempre muestre
// datos frescos del backend y no algo que haya quedado obsoleto.
const cargadoresDeVista = {
    dashboard: cargarDashboard,
    inventario: cargarInventario,
    asignacion: cargarAsignacion,
};

function activarVista(view) {
    sidebarLinks.forEach(l => l.classList.toggle("active", l.dataset.view === view));
    adminViews.forEach(v => v.classList.toggle("active", v.id === `view-${view}`));
    adminShell.classList.remove("sidebar-open");
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
   ============================================================ */

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

//TODO: Necesidad.java no tiene @JsonTypeInfo, así que Jackson
// va a serializar los campos propios de la subclase real, pero no hay
// garantía de un discriminador explícito ("tipo"/"@type"); por eso está
// inferido el tipo mirando qué campos aparecen. Si el equipo agrega un
// @JsonTypeInfo o un getter "getTipo()" en Necesidad, esto se puede
// simplificar bastante.
function mapNecesidad(raw) {
    const entidad = raw.entidadBeneficiaria || {};
    const esExtraordinaria = raw.cantidadRequerida !== undefined;
    const esRecurrente = raw.cantidadObjetivoPorPeriodo !== undefined || raw.periodo !== undefined;
    let tipo = raw.tipo; // por si en algún momento se homogeneiza con NecesidadDTO
    if (!tipo) tipo = esExtraordinaria ? "Extraordinaria" : (esRecurrente ? "Recurrente" : "Necesidad");

    return {
        id: raw.id,
        entidad: entidad.razonSocial || entidad.nombre || "Entidad",
        descripcion: raw.descripcion || "-",
        tipo,
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
// (obtenerTodosLosDonantesUnificados): sin
// endpoint dedicado, trayendo todo y filtrando en el navegador.

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
    "Vestimenta": ["Camperas de abrigo", "Remeras", "Pantalones", "Ropa infantil"],
    "Mobiliario": ["Sillas", "Mesas", "Estantes"],
    "Abrigo": ["Frazadas", "Colchones"],
};
const CATEGORIAS_CON_ESTADO = ["Mobiliario", "Vestimenta"];
const CATEGORIAS_PERECEDERAS = ["Alimentos"];

// TODO (gap real de backend, no solo de nombres): Unidad.java
// solo define KILOGRAMOS, LITROS, CENTIMETROS, METROS. No existe un valor
// para contar unidades discretas (sillas, paquetes, prendas), aunque la
// consigna dice textualmente "se deberá contabilizar la cantidad ... en
// una unidad determinada (kilogramos, unidades o según corresponda)" y los
// ejemplos del enunciado son justamente "6 sillas" y "100 paquetes de
// fideos". Hasta que el equipo agregue algo como UNIDADES al enum, dejo
// solo las 4 opciones que el backend realmente acepta; para bienes que se
// cuentan por unidad (sillas, paquetes, prendas) no hay forma correcta de
// mandar la unidad todavía.
const UNIDADES = [
    { value: "KILOGRAMOS", label: "kilogramos" },
    { value: "LITROS", label: "litros" },
    { value: "CENTIMETROS", label: "centímetros" },
    { value: "METROS", label: "metros" },
];

function categoriaOptionsHTML(selected) {
    return `<option value="">Seleccionar categoría</option>` +
        Object.keys(CATEGORIAS).map(cat => `<option value="${cat}" ${cat === selected ? "selected" : ""}>${cat}</option>`).join("");
}
function subcategoriaOptionsHTML(categoria) {
    const subs = CATEGORIAS[categoria] || [];
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
        <span class="field-pending-note">⚠ Falta un valor para "unidades" sueltas (sillas, paquetes, prendas) en el enum Unidad del backend.</span>
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

// --- Helpers de mapeo hacia los DTOs reales ---

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

// Convierte "yyyy-mm-dd" (input type=date) a un LocalDateTime ISO que
// Jackson pueda parsear ("yyyy-mm-ddT00:00:00").
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

    // Arma cada bien con la forma real de BienDTO:
    //   record BienDTO(String nombre, String descripcion, SubcategoriaDTO subcategoria,
    //                   LocalDateTime fechaDeVencimiento, EstadoDeUso esUsado,
    //                   Unidad tipoUnidad, Integer cantidad)
    // BienDTO pide "nombre" Y "descripcion" por separado, pero el formulario
    // solo tiene un campo de texto libre; uso ese texto para los dos. El
    // algoritmo de matchmaking (CompatibilidadSemantica) compara la
    // subcategoría de la donación contra la necesidad, no el nombre del
    // bien, así que esto no afecta el matching.
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
                // record PersonaJuridicaDTO(Long id, String cuit, String razonSocial, String rubro,
                //                            Tipo tipo, List<MedioDeNotificacionDTO> mediosDeNotificacion,
                //                            List<DonacionSinSegmentarDTO> donaciones)
                // No tiene "direccion" ni forma de asociar representantes: esos
                // datos quedan cargados en el formulario pero no se envían (ver
                // notas en el HTML).
                nuevoDonante = await apiPost("/donante/juridico", {
                    cuit,
                    razonSocial,
                    rubro: document.getElementById("jurRubro").value.trim() || null,
                    tipo: mapTipoPersonaJuridica(document.getElementById("jurTipo").value),
                    mediosDeNotificacion,
                });
            } else {
                const nombre = document.getElementById("donNombre").value.trim();
                const apellido = document.getElementById("donApellido").value.trim();
                const documento = document.getElementById("donDocumento").value.trim();
                if (!nombre || !apellido || !documento) throw new Error("Nombre, apellido y documento son obligatorios.");
                // record PersonaHumanaDTO(Long id, String nombre, String apellido, Integer DNI,
                //                          String genero, String direccion,
                //                          List<DonacionSinSegmentarDTO> donaciones,
                //                          List<MedioDeNotificacionDTO> mediosDeNotificacion,
                //                          MedioDeNotificacionDTO medioDeNotificacionPredeterminado)
                // No tiene "edad": ese dato queda cargado en el formulario pero no
                // se envía (ver nota en el HTML). El campo de documento es "DNI"
                // (Integer), no "numeroDocumento".
                const medioPredeterminadoKey = document.getElementById("donMedioPredeterminado").value; // "email"|"telefono"|"whatsapp"
                const medioPredeterminado = mediosDeNotificacion.find(
                    m => m.tipoDeNotificacion === TIPO_NOTIFICACION[medioPredeterminadoKey]
                ) || mediosDeNotificacion[0] || null;

                nuevoDonante = await apiPost("/donante/humano", {
                    nombre,
                    apellido,
                    DNI: Number(documento) || null,
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

        // record DonacionSinSegmentarDTO(List<BienDTO> bienes, LocalDateTime fechaDeIngreso)
        //TODO REVISAR PENDIENTE: el DTO no tiene lugar para la "descripción
        // general" de la carga que pide la consigna (ver nota en el HTML);
        // hay que agregarle un campo si se quiere persistir. Mientras tanto,
        // el texto que carga la persona administradora no se envía.
        const donacionesSegmentadas = await apiPost(path, {
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

let inventarioItems = [];

async function cargarInventario() {
    inventoryTableBody.innerHTML = `<tr><td colspan="8"><div class="loading-inline"><span class="spinner"></span> Cargando inventario...</div></td></tr>`;
    inventoryEmpty.hidden = true;
    try {
        const [disponibles, vencidas] = await Promise.all([
            apiGet(`/donaciones?estado=${ESTADOS.EN_DEPOSITO}`),
            apiGet(`/donaciones?estado=${ESTADOS.VENCIDA}`).catch(() => []),
        ]);
        const cacheDonantes = await getDonantesCache();
        inventarioItems = [...(disponibles || []), ...(vencidas || [])].map(d => mapDonacion(d, cacheDonantes));
        renderInventory();
    } catch (err) {
        console.error(err);
        inventoryTableBody.innerHTML = "";
        inventoryEmpty.hidden = false;
        inventoryEmpty.textContent = "No se pudo cargar el inventario. ¿Está corriendo el backend en " + API_BASE + "?";
    }
}

function inventoryRowHTML(item) {
    const esDisponible = item.estado === ESTADOS.EN_DEPOSITO;
    //TODO El backend sólo expone PATCH /donacion/{id}/vencer (no existe la
    // acción inversa), así que una vez vencida sólo se puede consultar.
    const accion = esDisponible
        ? `<button class="action-link expire" data-vencer="${item.id}">Marcar vencida</button>`
        : `<span class="reciente-item-meta">—</span>`;
    return `
    <tr>
      <td>${item.id}</td>
      <td>${item.articulo}</td>
      <td>${item.cantidad} ${item.unidad}</td>
      <td>${item.categoria}</td>
      <td>${item.fechaIngreso}</td>
      <td>${item.vencimiento}</td>
      <td><span class="pill pill-${esDisponible ? "Disponible" : "Vencida"}">${esDisponible ? "Disponible" : "Vencida"}</span></td>
      <td>${accion}</td>
    </tr>
  `;
}

function renderInventory() {
    const query = inventorySearch.value.trim().toLowerCase();
    const estadoFiltro = inventoryFilterEstado.value;

    const filtrados = inventarioItems.filter(item => {
        const esDisponible = item.estado === ESTADOS.EN_DEPOSITO;
        if (estadoFiltro === "Disponible" && !esDisponible) return false;
        if (estadoFiltro === "Vencida" && esDisponible) return false;
        if (query && !item.articulo.toLowerCase().includes(query)) return false;
        return true;
    });

    inventoryTableBody.innerHTML = filtrados.map(inventoryRowHTML).join("");
    inventoryEmpty.hidden = filtrados.length !== 0;
    if (!filtrados.length) inventoryEmpty.textContent = "No hay donaciones que coincidan con la búsqueda.";

    inventoryTableBody.querySelectorAll("[data-vencer]").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.vencer;
            btn.disabled = true;
            btn.textContent = "Actualizando...";
            try {
                await apiPatch(`/donacion/${id}/vencer?responsableId=${RESPONSABLE_ACTUAL_ID}`);
                await cargarInventario();
            } catch (err) {
                console.error(err);
                alert("No se pudo marcar la donación como vencida.");
                btn.disabled = false;
                btn.textContent = "Marcar vencida";
            }
        });
    });
}

inventorySearch.addEventListener("input", renderInventory);
inventoryFilterEstado.addEventListener("change", renderInventory);

/* ============================================================
   ASIGNACIÓN DE DONACIONES — Matchmaking
   ============================================================ */
const matchCardsContainer = document.getElementById("matchCardsContainer");

// donacionId -> { donacion, propuesta (o null si no se ejecutó todavía) }
let donacionesPendientesMap = new Map();

async function cargarAsignacion() {
    matchCardsContainer.innerHTML = `<div class="loading-inline"><span class="spinner"></span> Cargando donaciones pendientes de asignación...</div>`;
    try {
        const enDeposito = await apiGet(`/donaciones?estado=${ESTADOS.EN_DEPOSITO}`);
        const cacheDonantes = await getDonantesCache();
        donacionesPendientesMap = new Map(
            (enDeposito || []).map(d => mapDonacion(d, cacheDonantes)).map(d => [d.id, { donacion: d, propuesta: null, cargandoPropuesta: false }])
        );
        renderMatchCards();
    } catch (err) {
        console.error(err);
        matchCardsContainer.innerHTML = `<p class="empty-state">No se pudieron cargar las donaciones pendientes. ¿Está corriendo el backend?</p>`;
    }
}

// El backend ejecuta el matchmaking para TODAS las donaciones "En
// Depósito" de una sola vez (MotorDeMatchmaking / MatchmakingScheduler),
// no hay un endpoint para ejecutarlo donación por donación. Por eso el
// botón de cada card dispara el mismo proceso batch y después busca el
// resultado puntual de esa donación.
//
//TODO revisar: está asumido que el id de la PropuestaMatch generada coincide
// con el id de la DonacionSegmentada (matcheoId === donacionId). Si el
// backend genera un id propio para la propuesta, esto va a fallar con
// un 404 al hacer el GET; en ese caso habría que exponer en el backend
// alguna forma de obtener el matcheoId a partir del donacionId.
async function ejecutarYObtenerPropuesta(donacionId) {
    await apiPost("/matcheos/ejecutar");
    return apiGet(`/matcheos/${donacionId}`);
}

//TODO revisar
// GET /matcheos/{id} devuelve el objeto PropuestaMatch con las entidades
// JPA reales adentro (Necesidad, DonacionSegmentada), no DTOs. Necesidad
// tiene "donacionesRecibidas" (OneToMany) y DonacionSegmentada tiene
// "necesidad" (ManyToOne) apuntándose mutuamente, sin @JsonIgnore ni
// @JsonManagedReference/@JsonBackReference. Si alguna Necesidad ya tiene
// donaciones recibidas asociadas, Jackson puede entrar en una recursión
// infinita al serializar (Necesidad -> donacionesRecibidas -> necesidad ->
// donacionesRecibidas -> ...) y el endpoint devolvería 500. Si eso pasa,
// hay que pedirle a quien mantiene esas entidades que corte el ciclo
// (@JsonIgnore en un lado, o exponer PropuestaMatch como DTO).

function matchOptionHTML(donacionId, necesidad, rank, variant) {
    const inputName = `seleccion-${donacionId}`;
    const inputId = `sel-${donacionId}-${variant}-${necesidad.id}`;
    if (variant === "coincidencia") {
        return `
      <label class="match-option" for="${inputId}">
        <input type="radio" name="${inputName}" id="${inputId}" value="${necesidad.id}">
        <span class="match-rank-badge">${rank}</span>
        <span class="match-option-body">
          <span class="match-option-name">${necesidad.entidad}</span>
          <span class="match-option-desc">${necesidad.descripcion}</span>
        </span>
      </label>
    `;
    }
    return `
    <label class="algo-item" for="${inputId}">
      <input type="radio" name="${inputName}" id="${inputId}" value="${necesidad.id}">
      <span class="algo-item-rank">${rank}.</span>
      <span class="algo-item-body">
        <span class="algo-item-name">${necesidad.entidad}</span>
        <span class="algo-item-desc">${necesidad.descripcion}</span>
      </span>
    </label>
  `;
}

function matchCardHTML(id, entry) {
    const { donacion, propuesta, cargandoPropuesta } = entry;

    if (propuesta?.necesidadSeleccionada) {
        const seleccionada = mapNecesidad(propuesta.necesidadSeleccionada);
        return `
      <article class="match-card">
        <div class="match-head">
          <div>
            <div class="match-title">${donacion.articulo}</div>
            <div class="match-meta">${donacion.cantidad} ${donacion.unidad} · ${donacion.categoria} · Donante: ${donacion.donante}</div>
          </div>
        </div>
        <span class="match-assigned-pill">${ICONS.check} Asignada a ${seleccionada.entidad}</span>
      </article>
    `;
    }

    const botonEjecutar = `<button class="btn btn-primary" data-ejecutar="${id}" ${cargandoPropuesta ? "disabled" : ""}>
    ${cargandoPropuesta ? "Ejecutando..." : "Ejecutar Matchmaking"}
  </button>`;

    if (!propuesta) {
        return `
      <article class="match-card">
        <div class="match-head">
          <div>
            <div class="match-title">${donacion.articulo}</div>
            <div class="match-meta">${donacion.cantidad} ${donacion.unidad} · ${donacion.categoria} · Donante: ${donacion.donante}</div>
          </div>
          ${botonEjecutar}
        </div>
      </article>
    `;
    }

    const coincidencias = (propuesta.rankingConjunto || []).map(mapNecesidad);
    const rankingsIndividuales = propuesta.rankingsIndividuales || [];

    return `
    <article class="match-card">
      <div class="match-head">
        <div>
          <div class="match-title">${donacion.articulo}</div>
          <div class="match-meta">${donacion.cantidad} ${donacion.unidad} · ${donacion.categoria} · Donante: ${donacion.donante}</div>
        </div>
        ${botonEjecutar}
      </div>

      ${coincidencias.length ? `
        <div class="coincidencia-box">
          <div class="coincidencia-title">${ICONS.check} Coincidencias en Ambos Algoritmos (${coincidencias.length})</div>
          <div class="coincidencia-sub">Las siguientes entidades fueron recomendadas por ambos algoritmos. Seleccione una:</div>
          ${coincidencias.map((n, i) => matchOptionHTML(id, n, i + 1, "coincidencia")).join("")}
        </div>
      ` : `<p class="algo-item-desc" style="margin-top:14px;">No hubo coincidencias entre ambos algoritmos. Elija una necesidad de los rankings de abajo.</p>`}

      <div class="algo-columns">
        ${rankingsIndividuales.map(r => {
        const necesidades = (r.necesidades || []).map(mapNecesidad);
        return `
            <div class="algo-column">
              <div class="algo-column-title"><span class="algo-dot semantica"></span> ${r.nombreAlgoritmo || "Algoritmo"}</div>
              ${necesidades.map((n, i) => matchOptionHTML(id, n, i + 1, r.nombreAlgoritmo || "algo")).join("") || `<p class="algo-item-desc">Sin necesidades compatibles.</p>`}
            </div>
          `;
    }).join("")}
      </div>

      <div class="match-actions">
        <button class="btn btn-primary" data-confirmar="${id}" disabled>Confirmar Asignación a Entidad</button>
      </div>
    </article>
  `;
}

function renderMatchCards() {
    if (!donacionesPendientesMap.size) {
        matchCardsContainer.innerHTML = `<p class="empty-state">No hay donaciones "En depósito" pendientes de asignación.</p>`;
        return;
    }

    matchCardsContainer.innerHTML = Array.from(donacionesPendientesMap.entries())
        .map(([id, entry]) => matchCardHTML(id, entry))
        .join("");

    matchCardsContainer.querySelectorAll("[data-ejecutar]").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.ejecutar;
            const entry = donacionesPendientesMap.get(Number(id)) || donacionesPendientesMap.get(id);
            entry.cargandoPropuesta = true;
            renderMatchCards();
            try {
                entry.propuesta = await ejecutarYObtenerPropuesta(id);
            } catch (err) {
                console.error(err);
                alert("No se pudo ejecutar el matchmaking para esta donación. Revisá la consola para más detalle.");
            } finally {
                entry.cargandoPropuesta = false;
                renderMatchCards();
            }
        });
    });

    matchCardsContainer.querySelectorAll('input[type="radio"]').forEach(radio => {
        radio.addEventListener("change", () => {
            const card = radio.closest(".match-card");
            card.querySelector("[data-confirmar]").disabled = false;
        });
    });

    matchCardsContainer.querySelectorAll("[data-confirmar]").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.confirmar;
            const entry = donacionesPendientesMap.get(Number(id)) || donacionesPendientesMap.get(id);
            const seleccionado = matchCardsContainer.querySelector(`input[name="seleccion-${id}"]:checked`);
            if (!seleccionado) return;

            btn.disabled = true;
            btn.textContent = "Confirmando...";
            try {
                entry.propuesta = await apiPost(`/matcheos/${id}/seleccion`, { necesidadId: Number(seleccionado.value) });
                renderMatchCards();
            } catch (err) {
                console.error(err);
                alert("No se pudo confirmar la asignación.");
                btn.disabled = false;
                btn.textContent = "Confirmar Asignación a Entidad";
            }
        });
    });
}

/* ============================================================
   INICIO
   ============================================================ */
cargarDashboard();