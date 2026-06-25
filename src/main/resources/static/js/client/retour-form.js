const reservationId = getUrlParam('id') || 1;
const clientId = getUrlParam('clientId') || 1;

fetch('/client/retours/form/api/' + reservationId)
    .then(r => r.json())
    .then(data => {
        document.getElementById('infoReservation').innerHTML = `
            <div style="border:1px solid #ccc;padding:10px;margin:10px;">
                <h3>${data.machineNom}</h3>
                <p>Type: ${data.machineType}</p>
                <p>Période: ${data.dateDebut} au ${data.dateFin}</p>
                <p>Prix total: ${data.prixTotal} MGA</p>
                ${data.estRetard ? '<p style="color:red;">⚠️ Retard: ' + data.joursRetard + ' jour(s) - Pénalité: ' + data.penaliteRetard + ' MGA</p>' : ''}
            </div>
        `;
    })
    .catch(err => {
        document.getElementById('infoReservation').innerHTML = 'Erreur: ' + err.message;
    });

document.getElementById('formRetour').onsubmit = function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const data = {
        reservationId: reservationId,
        etatRetour: formData.get('etatRetour'),
        remarque: formData.get('remarque') || ''
    };

    fetch('/client/retours/api', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(data => {
        document.getElementById('message').innerHTML = `
            <div style="border:1px solid green;padding:10px;background:#d4edda;">
                <h3>✅ ${data.message}</h3>
                <p>Pénalité: ${data.retour.penalite || 0} MGA</p>
                <p>Total à payer: ${data.montantTotal} MGA</p>
                <button onclick="window.location.href='/client/reservations/mes-reservations?clientId=${clientId}'">
                    Voir mes réservations
                </button>
            </div>
        `;
        document.getElementById('formRetour').style.display = 'none';
    })
    .catch(err => {
        document.getElementById('message').innerHTML = 'Erreur: ' + err.message;
    });
};