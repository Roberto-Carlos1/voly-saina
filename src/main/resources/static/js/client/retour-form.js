function getUrlParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// ✅ Récupérer l'ID depuis le chemin de l'URL
function getReservationIdFromPath() {
    const path = window.location.pathname;
    // /client/retours/2/nouveau -> on prend le 3ème élément (index 3)
    const parts = path.split('/');
    // parts = ["", "client", "retours", "2", "nouveau"]
    return parts[3] || null;
}

// ✅ Utiliser l'ID du chemin
const reservationId = getReservationIdFromPath() || getUrlParam('id') || 1;
const clientId = getUrlParam('clientId') || 1;

console.log('📍 Chemin:', window.location.pathname);
console.log('🔍 ID depuis chemin:', getReservationIdFromPath());
console.log('🔍 ID final:', reservationId);
console.log('🔍 Client ID:', clientId);

// Charger les informations
// Vérifier si le formulaire a déjà été soumis pour cette réservation
fetch('/catalogue/retours/api/form/' + reservationId)
    .then(r => {
        console.log('📡 Appel API:', '/catalogue/retours/api/form/' + reservationId);
        if (!r.ok) throw new Error('Erreur HTTP ' + r.status);
        return r.json();
    })
    .then(data => {
        console.log('📦 Données reçues:', data);
        
        if (data.error) {
            document.getElementById('infoReservation').innerHTML = '❌ ' + data.error;
            return;
        }
        
        document.getElementById('infoReservation').innerHTML = `
            <div style="border:1px solid #ccc;padding:15px;margin:10px;border-radius:5px;">
                <h3>${data.machineNom}</h3>
                <p><strong>Type:</strong> ${data.machineType}</p>
                <p><strong>Période:</strong> ${data.dateDebut} au ${data.dateFin}</p>
                <p><strong>Prix total:</strong> ${data.prixTotal} MGA</p>
                <p><strong>Réservation ID:</strong> ${data.reservationId}</p>
                ${data.estRetard ? '<p style="color:red;">⚠️ Retard: ' + data.joursRetard + ' jour(s) - Pénalité: ' + data.penaliteRetard + ' MGA</p>' : ''}
            </div>
        `;
    })
    .catch(err => {
        console.error('❌ Erreur:', err);
        document.getElementById('infoReservation').innerHTML = `
            <div style="border:1px solid red;padding:15px;margin:10px;">
                ❌ Erreur: ${err.message}
                <br><br>
                <button onclick="location.reload()">🔄 Réessayer</button>
            </div>
        `;
    });

// Envoyer le retour
document.getElementById('formRetour').onsubmit = function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const data = {
        reservationId: Number(reservationId),
        etatRetour: formData.get('etatRetour'),
        remarque: formData.get('remarque') || ''
    };

    console.log('📤 Envoi:', data);

    fetch('/catalogue/retours/api', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(r => {
        console.log('📡 Status POST:', r.status);
        return r.json();
    })
    .then(data => {
        console.log('📦 Réponse POST:', data);
        
        if (data.error) {
            document.getElementById('message').innerHTML = '❌ ' + data.error;
            return;
        }
        
        const penalite = data.retour ? data.retour.penalite : (data.penalite || 0);
        const montantTotal = data.montantTotal || 0;
        
        document.getElementById('message').innerHTML = `
            <div style="border:1px solid green;padding:15px;background:#d4edda;border-radius:5px;">
                <h3 style="color:green;">✅ ${data.message}</h3>
                <p><strong>Pénalité:</strong> ${penalite} MGA</p>
                <p><strong>Total à payer:</strong> ${montantTotal} MGA</p>
                <br>
                <button onclick="window.location.href='/catalogue/reservations/mes-reservations?clientId=${clientId}'" 
                        style="padding:8px 15px;background:#007bff;color:white;border:none;border-radius:4px;cursor:pointer;">
                    Voir mes réservations
                </button>
            </div>
        `;
        document.getElementById('formRetour').style.display = 'none';
    })
    .catch(err => {
        console.error('❌ Erreur POST:', err);
        document.getElementById('message').innerHTML = '❌ Erreur: ' + err.message;
    });
};