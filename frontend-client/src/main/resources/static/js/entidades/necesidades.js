const registeredNeeds = [];
const needForm = document.getElementById("needForm");
const registeredNeedsList = document.getElementById("registeredNeedsList");
const sendNeedsForm = document.getElementById("sendNeedsForm");
const hiddenInputs = document.getElementById("hiddenInputs");const needType = document.getElementById("needType");
const periodField = document.getElementById("periodField");
const needPeriod = document.getElementById("needPeriod");
const quantityLabel = document.getElementById("quantityLabel");


function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}


const TIPO_LABEL = { RECURRENTE: "Recurrente", EXTRAORDINARIA: "Extraordinaria" };
const PERIODO_LABEL = { DIAS: "días", SEMANAS: "semanas", MESES: "meses" };

function renderRegisteredNeeds() {
    if (!registeredNeedsList) return;

    if (registeredNeeds.length === 0) {
        registeredNeedsList.innerHTML = `<p class="empty-state">No hay necesidades registradas aún.</p>`;
        return;
    }

    registeredNeedsList.innerHTML = registeredNeeds.map(need => `
        <article class="need-item-card">
            <div class="need-item-header">
                <h3 class="need-item-title">${escapeHtml(need.subcategoria)}</h3>
                <button type="button" class="delete-btn" onclick="deleteNeed(${need.id})" title="Eliminar necesidad">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                </button>
            </div>
            <p class="need-item-desc">${escapeHtml(need.descripcion)}</p>
            <div class="need-item-meta">
                <span>Cantidad: <b>${need.cantidad}${need.periodo ? " cada período de " + PERIODO_LABEL[need.periodo] : ""}</b></span>
                <span>Tipo: <span class="badge-priority priority-${need.tipo.toLowerCase()}">${TIPO_LABEL[need.tipo]}</span></span>
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

// Mostrar/ocultar período según el tipo
if (needType) {
    needType.addEventListener("change", () => {
        const recurrente = needType.value === "RECURRENTE";
        periodField.hidden = !recurrente;
        needPeriod.required = recurrente;
        if (!recurrente) needPeriod.value = "";
        quantityLabel.textContent = recurrente ? "Cantidad por período" : "Cantidad estimada";
    });
}

if (needForm) {
    needForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const tipo = needType.value;
        registeredNeeds.unshift({
            id: Date.now(),
            tipo,
            subcategoria: document.getElementById("needCategory").value,
            descripcion: document.getElementById("needDescription").value.trim(),
            cantidad: parseInt(document.getElementById("needQuantity").value, 10),
            periodo: tipo === "RECURRENTE" ? needPeriod.value : null
        });

        renderRegisteredNeeds();
        needForm.reset();
        periodField.hidden = true;
        needPeriod.required = false;
        quantityLabel.textContent = "Cantidad estimada";
    });
}
if (sendNeedsForm) {
    sendNeedsForm.addEventListener("submit", (e) => {
        if (registeredNeeds.length === 0) {
            e.preventDefault();
            showModal("Sin necesidades", "No hay necesidades registradas para enviar.", "warning");
            return;
        }

        hiddenInputs.innerHTML = "";
        registeredNeeds.forEach((need, i) => {
            ["tipo", "subcategoria", "descripcion", "cantidad", "periodo"].forEach(campo => {
                const input = document.createElement("input");
                input.type = "hidden";
                input.name = `necesidades[${i}].${campo}`;
                input.value = need[campo] ?? "";
                hiddenInputs.appendChild(input);
            });
        });

    });
}



const modal = document.getElementById("messageModal");
const modalIcon = document.getElementById("modalIcon");
const modalTitle = document.getElementById("modalTitle");
const modalMessage = document.getElementById("modalMessage");

const MODAL_ICONS = { success: "✓", error: "✕", warning: "!" };

function showModal(title, message, type = "success") {
    modalIcon.className = "modal-icon " + type;
    modalIcon.textContent = MODAL_ICONS[type];
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modal.showModal();
}

document.getElementById("modalClose").addEventListener("click", () => modal.close());


modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.close();
});

renderRegisteredNeeds();