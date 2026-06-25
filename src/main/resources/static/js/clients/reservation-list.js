const clientId = getUrlParam('clientId') || 1;

fetch('/client/reservations/api/client/' + clientId)
    .then(r => r.json())
    .then(reservations => {
        let html = '';
        if (reservations.length === 0) {
            html = '<p>Aucune réservation.</p>';
        } else {
            reservations.forEach(r => {
                html += `
                    <div style="border:1px solid #ccc;padding:10px;margin:10px;">
                        <h3>${r.machineNom}</h3>
                        <p>Type: ${r.machineType}</p>
                        <p>Période: ${r.dateDebut} au ${r.dateFin}</p>
                        <p>Prix: ${r.prixTotal} MGA</p>
                        <p>Statut: ${r.statutLibelle}</p>
                        <button onclick="voirDetail(${r.idReservation})">Détails</button>
                        ${r.peutAnnuler ? `<button onclick="annuler(${r.idReservation})">Annuler</button>` : ''}
                        ${r.peutRetourner ? `<button onclick="retourner(${r.idReservation})">Retourner</button>` : ''}
                    </div>
                `;
            });
        }
        document.getElementById('listeReservations').innerHTML = html;
    })
    .catch(err => {
        document.getElementById('listeReservations').innerHTML = 'Erreur: ' + err.message;
    });

function voirDetail(id) {
    window.location.href = '/client/reservations/' + id + '?clientId=' + clientId;
}

function annuler(id) {
    if (confirm('Annuler cette réservation ?')) {
        fetch('/client/reservations/api/' + id + '/annuler', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ motif: 'Annulé par le client' })
        })
        .then(r => r.json())
        .then(data => {
            alert(data.message);
            location.reload();
        })
        .catch(err => alert('Erreur: ' + err.message));
    }
}

function retourner(id) {
    window.location.href = '/client/retours/' + id + '/nouveau?clientId=' + clientId;
}