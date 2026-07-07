// clientId est maintenant défini dans le template HTML via Thymeleaf
// Si clientId n'est pas défini (page chargée sans le template), on utilise 1 par défaut
const currentClientId = (typeof clientId !== 'undefined') ? clientId : 1;

console.log('🔍 Client ID:', currentClientId);

fetch('/catalogue/reservations/api/client/' + currentClientId)
    .then(r => r.json())
    .then(data => {
        console.log('📦 Données reçues:', data);
        
        let reservations = data;
        if (!Array.isArray(data)) {
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
            reservations.forEach((r, index) => {
                // ✅ Vérifier si le retour est possible
                const peutRetourner = r.peutRetourner || 
                    (r.statut && (r.statut === 'en_cours' || r.statut === 'validee')) ||
                    (r.statutCode && (r.statutCode === 'en_cours' || r.statutCode === 'validee'));
                
                const peutAnnuler = r.peutAnnuler || 
                    (r.statut && (r.statut === 'en_attente' || r.statut === 'validee')) ||
                    (r.statutCode && (r.statutCode === 'en_attente' || r.statutCode === 'validee'));
                
                // ✅ Vérifier si un retour existe déjà
                const aRetour = r.idRetour || r.retourId || false;
                
                html += `
                    <div style="border:1px solid #ccc;padding:10px;margin:10px;border-radius:5px;">
                        <h3>${r.machineNom || 'Machine inconnue'}</h3>
                        <p><strong>Type:</strong> ${r.machineType || 'Non défini'}</p>
                        <p><strong>Période:</strong> ${r.dateDebut || ''} au ${r.dateFin || ''}</p>
                        <p><strong>Prix:</strong> ${r.prixTotal || 0} MGA</p>
                        <p><strong>Statut:</strong> ${r.statutLibelle || r.statut || 'Inconnu'}</p>
                        <button onclick="voirDetail(${r.idReservation})">Détails</button>
                        ${peutAnnuler ? `<button onclick="annuler(${r.idReservation})" style="color:white;background:red;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;">Annuler</button>` : ''}
                        ${peutRetourner && !aRetour ? `<button onclick="retourner(${r.idReservation})" style="color:white;background:orange;border:none;padding:5px 10px;border-radius:4px;cursor:pointer;">Retourner</button>` : ''}
                        ${aRetour ? `<span style="color:green;">✅ Retour déjà effectué</span>` : ''}
                    </div>
                `;
            });
        }
        document.getElementById('listeReservations').innerHTML = html;
    })
    .catch(err => {
        console.error('❌ Erreur:', err);
        document.getElementById('listeReservations').innerHTML = 
            '<p style="color:red;">❌ Erreur: ' + err.message + '</p>';
    });

function voirDetail(id) {
    window.location.href = '/client/reservations/' + id + '?clientId=' + currentClientId;
}

function annuler(id) {
    if (confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
        fetch('/catalogue/reservations/api/' + id + '/annuler', {
            method: 'POST',
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
    // ✅ L'ID est passé correctement ici
    console.log('🔍 Retour pour la réservation ID:', id);
    window.location.href = '/client/retours/' + id + '/nouveau?clientId=' + currentClientId;
}
