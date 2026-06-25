const clientId = getUrlParam('clientId') || 1;

console.log('Chargement réservations pour client:', clientId);

fetch('/client/reservations/api/client/' + clientId)
    .then(r => {
        console.log('📡 Status:', r.status);
        if (!r.ok) throw new Error('Erreur HTTP ' + r.status);
        return r.json();
    })
    .then(data => {
        console.log('📦 Données reçues:', data);

        let reservations = data;
        if (!Array.isArray(data)) {
            console.warn('La réponse n\'est pas un tableau, conversion...');
            if (data && data.content) {
                reservations = data.content;
            } else if (data && data.reservations) {
                reservations = data.reservations;
            } else {
                reservations = [];
            }
        }
        
        let html = '';
        if (reservations.length === 0) {
            html = '<p>Aucune réservation trouvée.</p>';
        } else {
            reservations.forEach(r => {
                html += `
                    <div style="border:1px solid #ccc;padding:10px;margin:10px;border-radius:5px;">
                        <h3>${r.machineNom || 'Machine inconnue'}</h3>
                        <p><strong>Type:</strong> ${r.machineType || 'Non défini'}</p>
                        <p><strong>Période:</strong> ${r.dateDebut || ''} au ${r.dateFin || ''}</p>
                        <p><strong>Prix:</strong> ${r.prixTotal || 0} MGA</p>
                        <p><strong>Statut:</strong> ${r.statutLibelle || r.statut || 'Inconnu'}</p>
                        <button onclick="voirDetail(${r.idReservation})">Détails</button>
                        ${r.peutAnnuler ? `<button onclick="annuler(${r.idReservation})" style="color:white;background:red;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;">Annuler</button>` : ''}
                        ${r.peutRetourner ? `<button onclick="retourner(${r.idReservation})" style="color:white;background:orange;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;">Retourner</button>` : ''}
                    </div>
                `;
            });
        }
        document.getElementById('listeReservations').innerHTML = html;
    })
    .catch(err => {
        console.error('Erreur:', err);
        document.getElementById('listeReservations').innerHTML = `
            <div style="border:1px solid red;padding:15px;margin:10px;color:red;">
                Erreur: ${err.message}
                <br><br>
                <button onclick="location.reload()">🔄 Réessayer</button>
            </div>
        `;
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
            alert(data.message || 'Réservation annulée');
            location.reload();
        })
        .catch(err => alert('Erreur: ' + err.message));
    }
}

function retourner(id) {
    window.location.href = '/client/retours/' + id + '/nouveau?clientId=' + clientId;
}