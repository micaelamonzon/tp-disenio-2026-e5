// Datos de ejemplo: se reemplazan cuando se integre con la API de entidades beneficiarias
const entities = [
  { name: "Comedor Los Girasoles", category: "Alimentación", location: "Villa Devoto, CABA", received: 34, following: true, desc: "Brinda almuerzo y merienda diaria a más de 120 familias del barrio." },
  { name: "Fundación Esperanza", category: "Alimentación / Insumos", location: "Lanús, Buenos Aires", received: 51, following: true, desc: "Distribuye alimentos no perecederos a comedores comunitarios de la zona sur." },
  { name: "Centro Comunitario San Martín", category: "Mobiliario / Vivienda", location: "San Martín, Buenos Aires", received: 12, following: true, desc: "Equipa espacios comunitarios y apoya a familias en situación de calle." },
  { name: "Casa del Niño", category: "Vestimenta / Niñez", location: "Quilmes, Buenos Aires", received: 27, following: false, desc: "Acompaña a niños y niñas en situación de vulnerabilidad social." },
  { name: "Hogar de Ancianos San José", category: "Cuidado de adultos mayores", location: "Morón, Buenos Aires", received: 19, following: false, desc: "Residencia para adultos mayores sin cobertura social ni familia cercana." },
  { name: "Ropero Comunitario Norte", category: "Vestimenta", location: "Vicente López, Buenos Aires", received: 40, following: false, desc: "Organiza y entrega ropa en buen estado a familias de la zona norte." },
];

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
