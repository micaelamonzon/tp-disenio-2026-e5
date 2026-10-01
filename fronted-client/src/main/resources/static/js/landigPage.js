// ===================== MOCK DATA =====================
const ZONE_COLORS = {
    'Centro': '#2563eb',
    'Sur': '#1d4ed8',
    'Oeste': '#9333ea'
};

const MOCK_DONATIONS = [
    {
        id: 1,
        title: "Entrega de Alimentos",
        entity: "Comedor Los Chicos",
        location: "Av. Corrientes 1234, CABA",
        date: "02/09/2026",
        lat: -34.6037,
        lng: -58.3816,
        image: "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 2,
        title: "Ropa de Abrigo",
        entity: "Fundación Comunidad",
        location: "Av. Rivadavia 4500, CABA",
        date: "28/08/2026",
        lat: -34.6157,
        lng: -58.4333,
        image: "https://images.unsplash.com/photo-1578357078586-491adf1aa5ba?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 3,
        title: "Kits Sanitarios",
        entity: "Hogar San José",
        location: "Calle Las Heras 2100, CABA",
        date: "15/08/2026",
        lat: -34.5833,
        lng: -58.4000,
        image: "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=600&q=80"
    }
];

// ===================== SESIÓN (mock, solo front) =====================
const SESSION_KEY = 'donatrack_session';

function saveSession(user) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(user));
    updateAuthUI();
}

// Ajuste en las funciones auxiliares de lectura y limpieza de sesión
function loadSession() {
    const local = localStorage.getItem(SESSION_KEY);
    if (local) return JSON.parse(local);
    const session = sessionStorage.getItem(SESSION_KEY);
    return session ? JSON.parse(session) : null;
}

function clearSession() {
    localStorage.removeItem(SESSION_KEY);
    sessionStorage.removeItem(SESSION_KEY);
    updateAuthUI();
}

function getInitials(name) {
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return parts[0].substring(0, 2).toUpperCase();
}

function formatRoleLabel(role) {
    if (role === 'donante') return 'Persona Donante';
    if (role === 'entidad') return 'Entidad Beneficiaria';
    if (role === 'admin') return 'Administrador';
    return role || 'Persona Donante';
}

function updateAuthUI() {
    const authArea = document.getElementById('auth-area');
    if (!authArea) return;

    const user = loadSession();

    if (user) {
        const initials = getInitials(user.nombre);
        const roleLabel = formatRoleLabel(user.role);

        authArea.innerHTML = `
            <div class="user-profile-widget" onclick="handleLogout()" title="Cerrar sesión">
                <div class="avatar">${initials}</div>
                <div class="user-info">
                    <span class="user-name">${user.nombre}</span>
                    <span class="user-role">${roleLabel}</span>
                </div>
            </div>`;
    } else {
        authArea.innerHTML = `<button class="btn btn-outline" onclick="navigateTo('login')">Iniciar sesión</button>`;
    }
}

function handleLogout() {
    clearSession();
    showToast('Sesión cerrada.', 'info');
    navigateTo('home');
}


// ===================== NAVIGATION CONTROLLER =====================
function navigateTo(pageId) {
    showLoader();

    setTimeout(() => {
        const pages = document.querySelectorAll('.page-section');
        pages.forEach(p => p.classList.remove('active'));

        const targetPage = document.getElementById(`page-${pageId}`);
        if (targetPage) {
            targetPage.classList.add('active');
        }

        const navLinks = document.querySelectorAll('.main-nav-link');
        navLinks.forEach(link => {
            if (link.getAttribute('data-page') === pageId) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });

        if (pageId === 'mapa') {
            renderMapPage();
        }

        hideLoader();
    }, 350);
}

// ===================== UI RENDERERS =====================
function renderDonations() {
    const featuredContainer = document.getElementById('featured-donations');
    const galleryContainer = document.getElementById('gallery-container');

    const cardsHTML = MOCK_DONATIONS.map(d => `
    <article class="donation-card">
      <div class="donation-img-wrapper">
        <img src="${d.image}" alt="${d.title}" loading="lazy">
      </div>
      <div class="donation-card-body">
        <h3>${d.title}</h3>
        <div class="donation-meta">
          <span>📦 ${d.entity}</span>
          <span>📍 ${d.location}</span>
          <span>📅 ${d.date}</span>
        </div>
      </div>
    </article>
  `).join('');

    if (featuredContainer) featuredContainer.innerHTML = MOCK_DONATIONS.slice(0, 3).map(d => `
    <article class="donation-card">
      <div class="donation-img-wrapper">
        <img src="${d.image}" alt="${d.title}" loading="lazy">
      </div>
      <div class="donation-card-body">
        <h3>${d.title}</h3>
        <div class="donation-meta">
          <span>📦 ${d.entity}</span>
          <span>📍 ${d.location}</span>
          <span>📅 ${d.date}</span>
        </div>
      </div>
    </article>
  `).join('');

    if (galleryContainer) galleryContainer.innerHTML = cardsHTML;
}

// ===================== MAPA ESTILIZADO =====================
let mapInstance = null;

function renderMapPage() {
    const mapContainer = document.getElementById('map');
    const cardsContainer = document.getElementById('map-cards-container');
    if (!mapContainer || typeof L === 'undefined') return;

    mapContainer.style.background = 'none';

    const oldLegend = mapContainer.querySelector('.map-legend, .leyenda');
    if (oldLegend) oldLegend.style.display = 'none';

    if (mapInstance) {
        mapInstance.remove();
    }

    // Inicializar mapa centrado en Buenos Aires
    mapInstance = L.map('map').setView([-34.6037, -58.4000], 12);

    // Servidor oficial y gratuito de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(mapInstance);

    // Renderizar marcadores con datos reales
    MOCK_DONATIONS.forEach(d => {
        const popupContent = `
            <div style="font-family: sans-serif; padding: 4px; min-width: 180px;">
                <h4 style="margin: 0 0 6px 0; color: #1e293b; font-size: 15px;">${d.mapTitle || d.title}</h4>
                <p style="margin: 3px 0; font-size: 13px; color: #475569;">📦 <strong>${d.entity}</strong></p>
                <p style="margin: 3px 0; font-size: 12px; color: #64748b;">📍 ${d.location}</p>
                <p style="margin: 3px 0; font-size: 12px; color: #64748b;">📅 ${d.date}</p>
            </div>
        `;

        const marker = L.marker([d.lat, d.lng]).addTo(mapInstance);
        marker.bindPopup(popupContent);

        marker.on('click', () => {
            if (cardsContainer) {
                const card = document.getElementById(`map-card-${d.id}`);
                if (card) {
                    card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                    card.style.outline = '2px solid #2563eb';
                    setTimeout(() => card.style.outline = 'none', 2000);
                }
            }
        });
    });

    if (cardsContainer) {
        cardsContainer.innerHTML = MOCK_DONATIONS.map(d => `
            <article class="mapa-donation-card" id="map-card-${d.id}" style="padding: 12px; margin-bottom: 10px; background: #fff; border-radius: 8px; border: 1px solid #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.04);">
                <h3 style="color: #2563eb; margin: 0 0 6px 0; font-size: 16px;">${d.mapTitle || d.title}</h3>
                <div class="donation-meta" style="display: flex; gap: 12px; font-size: 13px; color: #64748b; flex-wrap: wrap;">
                    <span>📦 ${d.entity}</span>
                    <span>📍 ${d.location}</span>
                    <span>📅 ${d.date}</span>
                </div>
            </article>
        `).join('');
    }

    setTimeout(() => {
        if (mapInstance) mapInstance.invalidateSize();
    }, 200);
}

// Función auxiliar para seleccionar y mostrar detalle de donación
function selectDonation(mapContainer, cardsContainer, d, marker) {
    // Cerrar popups anteriores
    mapContainer.querySelectorAll('.map-popup').forEach(el => el.remove());

    // Crear la tarjeta flotante sobre el marcador
    const popup = document.createElement('div');
    popup.className = 'map-popup';
    popup.style.position = 'absolute';
    popup.style.top = `calc(${d.mapPosition ? d.mapPosition.top : 50}% - 10px)`;
    popup.style.left = `${d.mapPosition ? d.mapPosition.left : 50}%`;
    popup.style.transform = 'translate(-50%, -100%)';
    popup.style.background = '#ffffff';
    popup.style.padding = '12px';
    popup.style.borderRadius = '8px';
    popup.style.boxShadow = '0 4px 15px rgba(0,0,0,0.15)';
    popup.style.zIndex = '10';
    popup.style.minWidth = '200px';

    popup.innerHTML = `
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
            <strong style="color: #1e293b; font-size: 14px;">${d.mapTitle || d.title}</strong>
            <button onclick="this.parentElement.parentElement.remove()" style="background:none; border:none; cursor:pointer; font-weight:bold; color:#94a3b8;">&times;</button>
        </div>
        <p style="margin:2px 0; font-size:12px; color:#475569;"><strong>Entidad:</strong> ${d.entity}</p>
        <p style="margin:2px 0; font-size:12px; color:#475569;"><strong>Ubicación:</strong> ${d.location}</p>
        <p style="margin:2px 0; font-size:12px; color:#475569;"><strong>Fecha:</strong> ${d.date}</p>
    `;

    mapContainer.appendChild(popup);

    // Desplazar hacia la tarjeta inferior si existe
    if (cardsContainer) {
        const card = document.getElementById(`map-card-${d.id}`);
        if (card) {
            card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            card.style.outline = '2px solid var(--blue, #2563eb)';
            setTimeout(() => card.style.outline = 'none', 2000);
        }
    }
}

function selectDonation(mapContainer, cardsContainer, donation, markerEl) {
    mapContainer.querySelectorAll('.map-marker').forEach(m => m.classList.remove('active'));
    cardsContainer.querySelectorAll('.mapa-donation-card').forEach(c => c.classList.remove('active'));

    markerEl.classList.add('active');

    const card = document.getElementById(`map-card-${donation.id}`);
    if (card) {
        card.classList.add('active');
        card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    showMapPopup(mapContainer, donation);
}

function showMapPopup(mapContainer, donation) {
    const prev = mapContainer.querySelector('.map-popup');
    if (prev) prev.remove();

    const popup = document.createElement('div');
    popup.className = 'map-popup';
    popup.innerHTML = `
        <button class="map-popup-close" onclick="this.parentElement.remove()" aria-label="Cerrar">&times;</button>
        <img src="${donation.image}" alt="${donation.title}">
        <div class="map-popup-body">
            <h4>${donation.mapTitle || donation.title}</h4>
            <p>📦 ${donation.entity}</p>
            <p>📍 ${donation.location}</p>
            <p>📅 ${donation.date}</p>
        </div>
    `;
    mapContainer.appendChild(popup);
}

// ===================== ROLE SWITCHERS =====================
function switchRegisterRole(role) {
    const btnDonante = document.getElementById('role-donante-btn');
    const btnEntidad = document.getElementById('role-entidad-btn');
    const formDonante = document.getElementById('form-registro');
    const formEntidad = document.getElementById('form-registro-entidad');

    if (!btnDonante || !btnEntidad || !formDonante || !formEntidad) return;

    if (role === 'entidad') {
        // Activar botón Entidad
        btnEntidad.classList.add('active');
        btnDonante.classList.remove('active');

        // Mostrar formulario de Entidad y ocultar Donante
        formEntidad.classList.remove('hidden');
        formDonante.classList.add('hidden');
    } else {
        // Activar botón Donante
        btnDonante.classList.add('active');
        btnEntidad.classList.remove('active');

        // Mostrar formulario de Donante y ocultar Entidad
        formDonante.classList.remove('hidden');
        formEntidad.classList.add('hidden');
    }
}

function switchLoginRole(role) {
    const roles = ['donante', 'entidad', 'admin'];
    roles.forEach(r => {
        const btn = document.getElementById(`login-${r}-btn`);
        if (btn) {
            if (r === role) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        }
    });
    const hiddenRoleInput = document.getElementById('login-selected-role');
    if (hiddenRoleInput) hiddenRoleInput.value = role;
}

// ===================== FORM HANDLERS =====================
function handleRegister(event) {
    event.preventDefault();

    // Obtener elementos directamente por su ID
    const nombreInput = document.getElementById('reg-nombre');
    const emailInput = document.getElementById('reg-email');
    const phoneInput = document.getElementById('reg-phone');
    const passInput = document.getElementById('reg-password');
    const confirmPassInput = document.getElementById('reg-confirm-password');
    const termsCheck = document.getElementById('reg-terms');

    // Obtener los valores reales al hacer click
    const passValue = passInput ? passInput.value : '';
    const confirmValue = confirmPassInput ? confirmPassInput.value : '';

    // --- REVISIÓN EN CONSOLA (Abre F12 para verificar qué lee el navegador) ---
    console.log("Contraseña leída:", passValue);
    console.log("Largo leído:", passValue.length);
    console.log("Confirmación leída:", confirmValue);

    // 1. Validar coincidencia de contraseñas
    if (passValue !== confirmValue) {
        showToast('Las contraseñas no coinciden. Por favor, verifícalas.', 'error');
        if (confirmPassInput) confirmPassInput.focus();
        return;
    }

    // 2. Validar longitud (mínimo 8 caracteres)
    if (passValue.length < 8) {
        showToast('La contraseña debe tener al menos 8 caracteres.', 'error');
        if (passInput) passInput.focus();
        return;
    }

    // 3. Validar teléfono
    const phoneValue = phoneInput ? phoneInput.value.trim() : '';
    const cleanPhone = phoneValue.replace(/[\s\-\(\)]/g, '');
    const phoneRegex = /^\+?[0-9]{7,15}$/;
    if (!phoneRegex.test(cleanPhone)) {
        showToast('Por favor, ingresa un número de teléfono válido.', 'error');
        if (phoneInput) phoneInput.focus();
        return;
    }

    // 4. Validar Términos
    if (termsCheck && !termsCheck.checked) {
        showToast('Debes aceptar los Términos y Condiciones para registrarte.', 'error');
        return;
    }

    // Si todo coincide y es >= 8 caracteres, se procesa el registro
    const nombreValue = nombreInput ? nombreInput.value.trim() : 'Micaela Monzon';
    const emailValue = emailInput ? emailInput.value.trim() : '';

    const user = { nombre: nombreValue, email: emailValue, phone: phoneValue, role: 'donante' };
    localStorage.setItem('registered_user', JSON.stringify(user));
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));

    updateAuthUI();
    showToast(`¡Bienvenida, ${nombreValue}! Registro exitoso.`, 'success');
    navigateTo('home');
}
function handleLogin(event) {
    event.preventDefault();

    const emailInput = document.getElementById('login-email');
    const emailVal = emailInput ? emailInput.value : '';
    const hiddenRoleInput = document.getElementById('login-selected-role');
    const role = hiddenRoleInput ? hiddenRoleInput.value : 'donante';

    // Buscar si el usuario ya se había registrado con ese nombre
    const savedUser = JSON.parse(localStorage.getItem('registered_user') || '{}');
    let nombre = 'Soledad Romero'; // Nombre fallback si no existe registro previo

    if (savedUser.email === emailVal && savedUser.nombre) {
        nombre = savedUser.nombre;
    }

    const rememberCheck = document.getElementById('login-remember');
    const rememberMe = rememberCheck ? rememberCheck.checked : false;

    const user = { nombre, email: emailVal, role };

    if (rememberMe) {
        localStorage.setItem(SESSION_KEY, JSON.stringify(user));
    } else {
        sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));
    }

    updateAuthUI();
    showToast(`Sesión iniciada correctamente.`, 'success');
    navigateTo('home');
}

function handleContactSubmit(event) {
    event.preventDefault();
    showLoader();
    setTimeout(() => {
        hideLoader();
        showToast('Mensaje enviado. Nos contactaremos a la brevedad.', 'success');
        const form = document.getElementById('contactForm');
        if (form) form.reset();
    }, 500);
}

// ===================== FEEDBACK HELPERS =====================
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerText = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3500);
}

function showLoader() {
    const loader = document.getElementById('global-loader');
    if (loader) {
        loader.classList.remove('hidden');
        loader.style.display = 'flex';
    }
}

function hideLoader() {
    const loader = document.getElementById('global-loader');
    if (loader) {
        loader.classList.add('hidden');
        loader.style.display = 'none';
    }
}

function renderFeaturedDonations() {
    const container = document.getElementById('featured-donations-container'); // O el ID que use tu contenedor
    if (!container) return;

    container.innerHTML = MOCK_DONATIONS.map(d => `
        <article class="card-donacion" style="background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05);">
            <div style="width: 100%; height: 180px; background: #e2e8f0; overflow: hidden;">
                ${d.image ? `
                    <img src="${d.image}" 
                         alt="${d.title}" 
                         style="width: 100%; height: 100%; object-fit: cover;" 
                         onerror="this.style.display='none'">
                ` : ''}
            </div>
            <div style="padding: 16px;">
                <h3 style="margin: 0 0 8px 0; font-size: 18px; color: #0f172a;">${d.title}</h3>
                <p style="margin: 4px 0; font-size: 14px; color: #64748b;">📦 ${d.entity}</p>
                <p style="margin: 4px 0; font-size: 14px; color: #64748b;">📍 ${d.location}</p>
                <p style="margin: 4px 0; font-size: 14px; color: #64748b;">📅 ${d.date}</p>
            </div>
        </article>
    `).join('');
}

// ===================== INITIALIZATION =====================
document.addEventListener('DOMContentLoaded', () => {
    renderDonations();
    updateAuthUI();
    hideLoader();

    const hash = window.location.hash.replace('#', '');
    if (hash) navigateTo(hash);
});