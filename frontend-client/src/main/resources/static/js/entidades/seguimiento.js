/*
const locationHistoryData = [
    { address: "Av. Corrientes 1234", time: "14:32 hs" },
    { address: "Av. Santa Fe 2800", time: "14:15 hs" },
    { address: "Av. Libertador 5000", time: "13:45 hs" }
];

function renderLocationHistory() {
    const locationHistory = document.getElementById("locationHistory");
    locationHistory.innerHTML = locationHistoryData.map(loc => `
    <li>${loc.address}<span>${loc.time}</span></li>
  `).join("");
}

renderLocationHistory();
*/

// Dibuja el mapa de seguimiento con Leaflet, usando los datos que Thymeleaf
// dejó en cada tarjeta de camión (atributos data-*).
document.addEventListener('DOMContentLoaded', () => {
    const contenedor = document.getElementById('mapaSeguimiento');
    if (!contenedor || typeof L === 'undefined') return; // sin camiones o sin Leaflet

    const mapa = L.map(contenedor);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(mapa);

    const puntos = [];
    document.querySelectorAll('.camion-card').forEach(card => {
        const lat = parseFloat(card.dataset.lat);
        const lng = parseFloat(card.dataset.lng);
        const destLat = parseFloat(card.dataset.destinoLat);
        const destLng = parseFloat(card.dataset.destinoLng);

        if (!isNaN(lat) && !isNaN(lng)) {
            L.marker([lat, lng]).addTo(mapa)
                .bindPopup('Camión ' + card.dataset.patente);
            puntos.push([lat, lng]);
        }
        if (!isNaN(destLat) && !isNaN(destLng)) {
            L.circleMarker([destLat, destLng], { radius: 8, color: '#22c55e' }).addTo(mapa)
                .bindPopup('Destino: ' + card.dataset.destino);
            puntos.push([destLat, destLng]);
            if (!isNaN(lat) && !isNaN(lng)) {
                L.polyline([[lat, lng], [destLat, destLng]], { dashArray: '6 6' }).addTo(mapa);
            }
        }
    });

    // Encuadra el mapa para que se vean todos los camiones y destinos
    if (puntos.length > 0) {
        mapa.fitBounds(puntos, { padding: [40, 40] });
    } else {
        mapa.setView([-34.6037, -58.3816], 12); // Buenos Aires por defecto
    }
});