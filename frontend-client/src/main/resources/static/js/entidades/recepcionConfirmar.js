// Envía la confirmación de recepción con las fotos elegidas.
// Las fotos las guarda recepcion.js en memoria (getFotos), no en el formulario,
// por eso las agregamos acá a mano antes de enviar.
document.addEventListener('click', async (e) => {
    const boton = e.target.closest('[data-role="confirm"]');
    if (!boton) return;

    const card = boton.closest('.donation-card');
    const form = card.querySelector('form.form-confirmar');
    const fotos = (typeof getFotos === 'function') ? getFotos(card) : [];

    if (fotos.length === 0) {
        alert('Adjuntá al menos una foto de la donación recibida.');
        return;
    }

    // FormData(form) incluye el token CSRF que agrega Thymeleaf, si está activo
    const datos = new FormData(form);
    fotos.forEach(foto => datos.append('fotos', foto, foto.name));

    // Indicador de carga mientras se suben las fotos
    boton.disabled = true;
    boton.textContent = 'Confirmando...';

    try {
        // redirect: 'manual' para que el mensaje de éxito/error se muestre
        // al recargar la página, y no se pierda en la redirección del fetch
        await fetch(form.action, { method: 'POST', body: datos, redirect: 'manual' });
    } finally {
        window.location.href = form.dataset.volver;
    }
});