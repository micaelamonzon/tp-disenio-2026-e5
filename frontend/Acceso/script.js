// ===================== MOCK DATA =====================
const ZONE_COLORS = {
    Centro: 'var(--blue)',
    Sur: 'var(--blue-dark)',
    Oeste: 'var(--purple)'
};

const MOCK_DONATIONS = [
    {
        id: 1,
        title: "200 viandas entregadas",
        entity: "Comedor Barrio Almagro",
        location: "Almagro, CABA",
        date: "15 de Abril, 2026",
        lat: -34.6080,
        lng: -58.4170,
        zone: "Centro",
        mapPosition: { top: 10, left: 54.9 },
        image: "https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 2,
        title: "150 abrigos y frazadas",
        entity: "Hogar de Niños San José",
        location: "Caballito, CABA",
        date: "10 de Abril, 2026",
        lat: -34.6180,
        lng: -58.4370,
        zone: "Centro",
        mapPosition: { top: 15.3, left: 35.4 },
        image: "https://images.unsplash.com/photo-1593113598332-cd288d649433?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 3,
        title: "80 kg de alimentos no perecederos",
        entity: "Hospital Gandulfo",
        location: "Lomas de Zamora, Buenos Aires",
        date: "5 de Abril, 2026",
        lat: -34.7600,
        lng: -58.4000,
        zone: "Sur",
        mapPosition: { top: 90, left: 71.5 },
        image: "https://images.unsplash.com/photo-1593113646773-028c64a8f1b8?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 4,
        title: "Ropa y calzado para comunidad",
        mapTitle: "300 prendas de ropa y calzado",
        entity: "Centro Comunitario La Esperanza",
        location: "Villa Lugano, CABA",
        date: "1 de Abril, 2026",
        lat: -34.6800,
        lng: -58.4600,
        zone: "Oeste",
        mapPosition: { top: 47.9, left: 12.9 },
        image: "https://images.unsplash.com/photo-1559027615-cd4628902d4a?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 5,
        title: "Útiles escolares para escuela",
        mapTitle: "Útiles escolares para 120 estudiantes",
        entity: "Escuela Primaria N°42",
        location: "Flores, CABA",
        date: "28 de Marzo, 2026",
        lat: -34.6300,
        lng: -58.4630,
        zone: "Oeste",
        mapPosition: { top: 21.6, left: 10 },
        image: "https://images.unsplash.com/photo-1452860606245-08befc0ff44b?auto=format&fit=crop&w=600&q=80"
    },
    {
        id: 6,
        title: "Colchones y muebles",
        entity: "Refugio Noche Segura",
        location: "Constitución, CABA",
        date: "22 de Marzo, 2026",
        lat: -34.6280,
        lng: -58.3810,
        zone: "Sur",
        mapPosition: { top: 20.5, left: 90 },
        image: "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=600&q=80"
    }
];

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
function renderMapPage() {
    const mapContainer = document.getElementById('map');
    const cardsContainer = document.getElementById('map-cards-container');
    if (!mapContainer || !cardsContainer) return;

    mapContainer.querySelectorAll('.map-marker, .map-tag').forEach(el => el.remove());

    MOCK_DONATIONS.forEach(d => {
        const marker = document.createElement('div');
        marker.className = 'map-marker';
        marker.style.top = d.mapPosition.top + '%';
        marker.style.left = d.mapPosition.left + '%';
        marker.style.background = ZONE_COLORS[d.zone] || 'var(--blue)';
        marker.setAttribute('role', 'button');
        marker.setAttribute('tabindex', '0');
        marker.setAttribute('aria-label', `${d.mapTitle || d.title} — ${d.entity}`);
        marker.addEventListener('click', () => showMapTag(mapContainer, d));
        marker.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') showMapTag(mapContainer, d);
        });
        mapContainer.appendChild(marker);
    });

    cardsContainer.innerHTML = MOCK_DONATIONS.map(d => `
    <article class="mapa-donation-card">
      <h3 style="color:${ZONE_COLORS[d.zone] || 'var(--blue)'}">${d.mapTitle || d.title}</h3>
      <div class="donation-meta">
        <span>📦 ${d.entity}</span>
        <span>📍 ${d.location}</span>
        <span>📅 ${d.date}</span>
      </div>
    </article>
  `).join('');
}

function showMapTag(mapContainer, donation) {
    const prev = mapContainer.querySelector('.map-tag');
    if (prev) prev.remove();

    const tag = document.createElement('div');
    tag.className = 'map-tag';
    tag.textContent = `${donation.mapTitle || donation.title} — ${donation.location}`;
    mapContainer.appendChild(tag);
}

// ===================== ROLE SWITCHERS =====================
function switchRegisterRole(role) {
    const btnDonante = document.getElementById('role-donante-btn');
    const btnEntidad = document.getElementById('role-entidad-btn');
    const formDonante = document.getElementById('form-registro-donante');
    const formEntidad = document.getElementById('form-registro-entidad');

    if (!btnDonante || !btnEntidad || !formDonante || !formEntidad) return;

    if (role === 'donante') {
        btnDonante.classList.add('active');
        btnEntidad.classList.remove('active');
        formDonante.classList.remove('hidden');
        formEntidad.classList.add('hidden');
    } else {
        btnEntidad.classList.add('active');
        btnDonante.classList.remove('active');
        formEntidad.classList.remove('hidden');
        formDonante.classList.add('hidden');
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
function handleRegister(event, type) {
    event.preventDefault();

    const form = event.target;
    const passwordInputs = form.querySelectorAll('input[type="password"]');
    if (passwordInputs.length === 2 && passwordInputs[0].value !== passwordInputs[1].value) {
        showToast('Las contraseñas no coinciden.', 'error');
        return;
    }

    showLoader();
    setTimeout(() => {
        hideLoader();
        showToast(`¡Registro de ${type === 'donante' ? 'Donante' : 'Entidad'} exitoso! Ya puedes iniciar sesión.`, 'success');
        navigateTo('login');
    }, 600);
}

function handleLogin(event) {
    event.preventDefault();
    const hiddenRoleInput = document.getElementById('login-selected-role');
    const role = hiddenRoleInput ? hiddenRoleInput.value : 'donante';
    showLoader();
    setTimeout(() => {
        hideLoader();
        showToast(`Sesión iniciada correctamente como ${role.toUpperCase()}.`, 'success');
        navigateTo('home');
    }, 600);
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

// ===================== INITIALIZATION =====================
document.addEventListener('DOMContentLoaded', () => {
    renderDonations();
    hideLoader(); // Aseguramos que el loader esté oculto al iniciar

    const hash = window.location.hash.replace('#', '');
    if (hash) {
        navigateTo(hash);
    }
});