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