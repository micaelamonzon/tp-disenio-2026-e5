// Datos de ejemplo: se reemplazan cuando se integre con incentivos-service
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
