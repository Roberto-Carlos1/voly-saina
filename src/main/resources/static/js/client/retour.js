// Récupérer les IDs depuis l'URL
const reservationId = getUrlParam('id') || 1;
const clientId = getUrlParam('clientId') || 1;

// Charger les informations de la réservation
fetch('/catalogue/retours/api/form/' + reservationId)
    .then(response => {
        if (!response.ok) throw new Error('Erreur HTTP ' + response.status);
        return response.json();
    })
    .then(data => {
        let html = `
        <div style="border:1px solid #ccc;padding:15px;margin:10px;">
            <h3>${data.machineNom}</h3>
            <p><strong>Type:</strong> ${data.machineType}</p>
            <p><strong>Période:</strong> ${formatDate(data.dateDebut)} au ${formatDate(data.dateFin)}</p>
            <p><strong>Prix total:</strong> ${formatNumber(data.prixTotal)} MGA</p>
            ${data.estRetard ? `<p style="color:red;"><strong>⚠️ Retard:</strong> ${data.joursRetard} jour(s) - Pénalité: ${formatNumber(data.penaliteRetard)} MGA</p>` : ''}
        </div>
    `;
    document.getElementById('infoReservation').innerHTML = html;
})
.catch(error => {
    document.getElementById('infoReservation').innerHTML = 
        '<p style="color:red;">❌ Erreur: ' + error.message + '</p>';
    console.error('Erreur:', error);
});

// Envoyer le formulaire de retour
document.getElementById('formRetour').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const data = {
        reservationId: reservationId,
        etatRetour: formData.get('etatRetour'),
        remarque: formData.get('remarque') || ''
    };

    fetch('/catalogue/retours/api', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) throw new Error('Erreur HTTP ' + response.status);
        return response.json();
    })
    .then(data => {
        document.getElementById('message').innerHTML = `
            <div style="border:1px solid green;padding:15px;background:#d4edda;border-radius:5px;">
                <h3 style="color:green;">✅ ${data.message}</h3>
                <p><strong>Pénalité:</strong> ${formatNumber(data.retour.penalite || 0)} MGA</p>
                <p><strong>Total à payer:</strong> ${formatNumber(data.montantTotal)} MGA</p>
                <br>
                <button onclick="window.location.href='/catalogue/reservations/mes-reservations?clientId=${clientId}'"
                        style="padding:8px 15px;background:#007bff;color:white;border:none;border-radius:4px;cursor:pointer;">
                    Voir mes réservations
                </button>
            </div>
        `;
        document.getElementById('formRetour').style.display = 'none';
    })
    .catch(error => {
        document.getElementById('message').innerHTML = 
            '<p style="color:red;">❌ Erreur: ' + error.message + '</p>';
        console.error('Erreur:', error);
    });
});