document.addEventListener('click', (e) => {

    // Ver / ocultar detalle
    const toggleBtn = e.target.closest('[data-role="togglePhotos"]');
    if (toggleBtn) {
        const card = toggleBtn.closest('.donation-card');
        const panel = card.querySelector('.photos-panel');
        const abierto = panel.classList.toggle('open');
        toggleBtn.textContent = abierto ? 'Ocultar detalle' : 'Ver detalle y fotos';
        toggleBtn.setAttribute('aria-expanded', abierto);
        return;
    }

    // Cancelar: cierra el panel
    const cancelBtn = e.target.closest('[data-role="cancel"]');
    if (cancelBtn) {
        const card = cancelBtn.closest('.donation-card');
        card.querySelector('.photos-panel').classList.remove('open');
        card.querySelector('[data-role="togglePhotos"]').textContent = 'Ver detalle y fotos';
        return;
    }

    // Agrandar foto
    const foto = e.target.closest('.photo-gallery img, .upload-preview img');
    if (foto) {
        document.getElementById('lightboxImg').src = foto.src;
        document.getElementById('photoLightbox').hidden = false;
        return;
    }

    // Cerrar lightbox (botón X o click en el fondo)
    if (e.target.id === 'lightboxClose' || e.target.id === 'photoLightbox') {
        cerrarLightbox();
    }
});

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') cerrarLightbox();
});

function cerrarLightbox() {
    document.getElementById('photoLightbox').hidden = true;
    document.getElementById('lightboxImg').src = '';
}

/* ---------- SUBIDA DE FOTOS Y CÁMARA ---------- */
const MAX_FOTO_BYTES = 5 * 1024 * 1024;
const fotosPorTarjeta = new WeakMap();

function getFotos(card) {
    if (!fotosPorTarjeta.has(card)) fotosPorTarjeta.set(card, []);
    return fotosPorTarjeta.get(card);
}

function agregarFotos(card, files) {
    const fotos = getFotos(card);
    for (const file of files) {
        if (!file.type.startsWith('image/')) {
            alert(`"${file.name}" no es una imagen.`);
            continue;
        }
        if (file.size > MAX_FOTO_BYTES) {
            alert(`"${file.name}" supera los 5MB.`);
            continue;
        }
        fotos.push(file);
    }
    renderPreview(card);
}

function renderPreview(card) {
    const preview = card.querySelector('[data-role="preview"]');
    preview.innerHTML = '';
    getFotos(card).forEach((file, i) => {
        const thumb = document.createElement('div');
        thumb.className = 'thumb';

        const img = document.createElement('img');
        img.src = URL.createObjectURL(file);
        img.alt = 'Foto';

        const del = document.createElement('button');
        del.type = 'button';
        del.className = 'thumb-remove';
        del.dataset.index = i;
        del.textContent = '×';

        thumb.append(img, del);
        preview.appendChild(thumb);
    });
}

/* Webcam para notebook */
async function abrirWebcam(card) {
    let stream;
    try {
        stream = await navigator.mediaDevices.getUserMedia({ video: true });
    } catch (err) {
        alert('No se pudo acceder a la cámara. Revisá los permisos del navegador.');
        return;
    }

    const modal = document.createElement('div');
    modal.className = 'camera-modal';
    modal.innerHTML = `
            <div class="camera-box">
                <video autoplay playsinline muted></video>
                <div class="camera-actions">
                    <button type="button" class="btn btn-outline" data-cam="cancel">Cancelar</button>
                    <button type="button" class="btn btn-submit" data-cam="shot">Capturar</button>
                </div>
            </div>`;
    document.body.appendChild(modal);

    const video = modal.querySelector('video');
    video.srcObject = stream;

    const cerrar = () => {
        stream.getTracks().forEach(t => t.stop());
        modal.remove();
    };

    modal.addEventListener('click', (e) => {
        const accion = e.target.dataset.cam;
        if (accion === 'cancel') cerrar();
        if (accion === 'shot') {
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            canvas.getContext('2d').drawImage(video, 0, 0);
            canvas.toBlob((blob) => {
                const file = new File([blob], `foto-${Date.now()}.jpg`, { type: 'image/jpeg' });
                agregarFotos(card, [file]);
                cerrar();
            }, 'image/jpeg', 0.9);
        }
    });
}

document.addEventListener('click', (e) => {
    // Botón "Tomar foto"
    const camBtn = e.target.closest('[data-role="cameraBtn"]');
    if (camBtn) {
        const card = camBtn.closest('.donation-card');
        const esCelular = window.matchMedia('(pointer: coarse)').matches;
        if (esCelular || !navigator.mediaDevices?.getUserMedia) {
            card.querySelector('[data-role="cameraInput"]').click();
        } else {
            abrirWebcam(card);
        }
        return;
    }

    // Quitar una miniatura
    const del = e.target.closest('.thumb-remove');
    if (del) {
        const card = del.closest('.donation-card');
        getFotos(card).splice(Number(del.dataset.index), 1);
        renderPreview(card);
        return;
    }

    // Click en el recuadro: abrir selector de archivos
    const dropzone = e.target.closest('[data-role="dropzone"]');
    if (dropzone && !e.target.closest('input, .thumb')) {
        dropzone.querySelector('[data-role="fileInput"]').click();
    }
});

// Archivos elegidos (selector o cámara del celular)
document.addEventListener('change', (e) => {
    const input = e.target.closest('[data-role="fileInput"], [data-role="cameraInput"]');
    if (!input) return;
    const card = input.closest('.donation-card');
    agregarFotos(card, Array.from(input.files));
    input.value = ''; // permite volver a elegir el mismo archivo
});

// Arrastrar y soltar sobre el recuadro
document.addEventListener('dragover', (e) => {
    if (e.target.closest('[data-role="dropzone"]')) e.preventDefault();
});
document.addEventListener('drop', (e) => {
    const dropzone = e.target.closest('[data-role="dropzone"]');
    if (!dropzone) return;
    e.preventDefault();
    agregarFotos(dropzone.closest('.donation-card'), Array.from(e.dataTransfer.files));
});
