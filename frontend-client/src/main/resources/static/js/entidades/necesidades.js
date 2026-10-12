const registeredNeeds = [];
const needForm = document.getElementById("needForm");
const registeredNeedsList = document.getElementById("registeredNeedsList");
const sendNeedsBtn = document.getElementById("sendNeedsBtn");
const needType = document.getElementById("needType");
const periodField = document.getElementById("periodField");
const needPeriod = document.getElementById("needPeriod");
const quantityLabel = document.getElementById("quantityLabel");

const ENDPOINT = "/entidadBeneficiaria/necesidades";

function escapeHtml(text) {
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
}

function csrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    return token && header ? { [header]: token } : {};
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
            id: Date.now(),                       // id local, solo para el borrado en la lista
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

async function enviarNecesidad(need) {
    const { id, ...payload } = need;              // el id local no viaja
    const resp = await fetch(ENDPOINT, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...csrfHeaders() },
        body: JSON.stringify(payload)
    });
    if (!resp.ok) throw new Error("HTTP " + resp.status);
}

if (sendNeedsBtn) {
    sendNeedsBtn.addEventListener("click", async () => {
        if (registeredNeeds.length === 0) {
            alert("No hay necesidades registradas para enviar.");
            return;
        }

        sendNeedsBtn.disabled = true;
        let enviadas = 0;

        // Una por una: las que salen bien se quitan de la lista,
        // las que fallan quedan para reintentar.
        for (const need of [...registeredNeeds]) {
            try {
                await enviarNecesidad(need);
                deleteNeed(need.id);
                enviadas++;
            } catch (err) {
                console.error("Falló el envío de la necesidad", need, err);
            }
        }

        sendNeedsBtn.disabled = false;
        const fallidas = registeredNeeds.length;
        alert(fallidas === 0
            ? `Se enviaron ${enviadas} necesidad(es) correctamente.`
            : `Se enviaron ${enviadas}. ${fallidas} no se pudieron enviar, intentá de nuevo.`);
    });
}

renderRegisteredNeeds();