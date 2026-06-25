const machineId = getUrlParam('id');

if (!machineId) {
    document.getElementById('detailMachine').innerHTML = 
        '<p style="color:red;">Aucune machine sélectionnée.</p>';
} else {
    fetch('/client/machines/api/' + machineId)
        .then(r => {
            if (!r.ok) throw new Error('Erreur HTTP ' + r.status);
            return r.json();
        })
        .then(m => {
            document.getElementById('detailMachine').innerHTML = `
                <div style="border:1px solid #ccc;padding:20px;margin:10px;border-radius:5px;">
                    <h2>${m.nom}</h2>
                    <p><strong>Description:</strong> ${m.description || 'Aucune description'}</p>
                    <hr>
                    <p><strong>Type:</strong> ${m.typeMachine}</p>
                    <p><strong>État:</strong> ${m.etatMachine}</p>
                    <p><strong>Prix:</strong> ${m.prixJour} MGA/jour</p>
                    <p><strong>Localisation:</strong> ${m.localisation || 'Non spécifiée'}</p>
                    <p><strong>Disponible:</strong> ${m.disponible ? '✅ Oui' : '❌ Non'}</p>
                    <br>
                    <button onclick="reserver(${m.idMachine})" ${!m.disponible ? 'disabled' : ''}>
                        ${m.disponible ? '📅 Réserver' : 'Indisponible'}
                    </button>
                </div>
            `;
        })
        .catch(err => {
            document.getElementById('detailMachine').innerHTML = 
                '<p style="color:red;">❌ Erreur: ' + err.message + '</p>';
        });
}

function reserver(id) {
    window.location.href = '/client/reservations/' + id + '/nouvelle?clientId=1';
}