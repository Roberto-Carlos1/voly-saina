const reservationId = getUrlParam('id') || 1;

fetch('/catalogue/reservations/api/' + reservationId)
    .then(r => r.json())
    .then(r => {
        document.getElementById('detailReservation').innerHTML = `
            <div style="border:1px solid #ccc;padding:10px;margin:10px;">
                <h3>${r.machineNom}</h3>
                <p>Type: ${r.machineType}</p>
                <p>Période: ${r.dateDebut} au ${r.dateFin}</p>
                <p>Prix total: ${r.prixTotal} MGA</p>
                <p>Statut: ${r.statutLibelle}</p>
                <p>Lieu livraison: ${r.lieuLivraison || 'Non spécifié'}</p>
                ${r.motifRefus ? '<p>Motif refus: ' + r.motifRefus + '</p>' : ''}
            </div>
        `;
    })
    .catch(err => {
        document.getElementById('detailReservation').innerHTML = 'Erreur: ' + err.message;
    });