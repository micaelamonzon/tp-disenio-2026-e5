const registeredNeeds = [];
const needForm = document.getElementById("needForm");
const registeredNeedsList = document.getElementById("registeredNeedsList");
const sendNeedsBtn = document.getElementById("sendNeedsBtn");

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

function renderRegisteredNeeds() {
    if (!registeredNeedsList) return;

    if (registeredNeeds.length === 0) {
        registeredNeedsList.innerHTML = `<p class="empty-state">No hay necesidades registradas aún.</p>`;
        return;
    }

    registeredNeedsList.innerHTML = registeredNeeds.map(need => `
        <article class="need-item-card">
            <div class="need-item-header">
                <h3 class="need-item-title">${escapeHtml(need.category)}</h3>
                <button type="button" class="delete-btn" onclick="deleteNeed(${need.id})" title="Eliminar necesidad">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                </button>
            </div>
            <p class="need-item-desc">${escapeHtml(need.description)}</p>
            <div class="need-item-meta">
                <span>Cantidad: <b>${escapeHtml(String(need.quantity))} ${escapeHtml(need.unit)}</b></span>
                <span>Prioridad: <span class="badge-priority priority-${escapeHtml(need.priority.toLowerCase())}">${escapeHtml(need.priority)}</span></span>
            </div>
        </article>
    `).join("");
}

function deleteNeed(id) {
    const index = registeredNeeds.findIndex(n => n.id === id);
    if (index !== -1) {
        registeredNeeds.splice(index, 1);
        renderRegisteredNeeds();
    }
}

if (needForm) {
    needForm.addEventListener("submit", (e) => {
        e.preventDefault();

        registeredNeeds.unshift({
            id: Date.now(),
            category: document.getElementById("needCategory").value,
            description: document.getElementById("needDescription").value,
            quantity: document.getElementById("needQuantity").value,
            unit: document.getElementById("needUnit").value,
            priority: document.getElementById("needPriority").value
        });

        renderRegisteredNeeds();
        needForm.reset();
    });
}

if (sendNeedsBtn) {
    sendNeedsBtn.addEventListener("click", () => {
        if (registeredNeeds.length === 0) {
            alert("No hay necesidades registradas para enviar.");
            return;
        }
        alert(`Se enviaron ${registeredNeeds.length} necesidad(es) correctamente.`);
        registeredNeeds.splice(0, registeredNeeds.length);
        renderRegisteredNeeds();
    });
}

renderRegisteredNeeds();