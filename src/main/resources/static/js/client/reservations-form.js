const machineId = document.querySelector('input[name="machineId"]').value;
const clientId = document.querySelector('input[name="clientId"]').value;

fetch('/client/machines/api/' + machineId)
    .then(r => r.json())
    .then(m => {
        document.getElementById('machineInfo').innerHTML = `
            <div style="border:1px solid #ccc;padding:10px;margin:10px;">
                <h3>${m.nom}</h3>
                <p>${m.description || ''}</p>
                <p>Prix: ${m.prixJour} MGA/jour</p>
                <p>Disponible: ${m.disponible ? 'Oui' : 'Non'}</p>
            </div>
        `;
    });

document.getElementById('reservationForm').onsubmit = function(e) {
    e.preventDefault();
    
    const data = {
        clientId: clientId,
        machineId: machineId,
        dateDebut: document.getElementById('dateDebut').value,
        dateFin: document.getElementById('dateFin').value,
        lieuLivraison: document.getElementById('lieuLivraison').value
    };

    fetch('/client/reservations/api', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(data => {
        if (data.error) {
            document.getElementById('resultat').innerHTML = 'Erreur: ' + data.error;
        } else {
            document.getElementById('resultat').innerHTML = `
                <div style="border:1px solid green;padding:10px;background:#d4edda;">
                    <h3>✅ ${data.message}</h3>
                    <p>Machine: ${data.reservation.machineNom}</p>
                    <p>Période: ${data.reservation.dateDebut} au ${data.reservation.dateFin}</p>
                    <p>Prix total: ${data.reservation.prixTotal} MGA</p>
                    <button onclick="window.location.href='/client/reservations/mes-reservations?clientId=${clientId}'">
                        Voir mes réservations
                    </button>
                </div>
            `;
            document.getElementById('reservationForm').style.display = 'none';
        }
    })
    .catch(err => {
        document.getElementById('resultat').innerHTML = 'Erreur: ' + err.message;
    });
};