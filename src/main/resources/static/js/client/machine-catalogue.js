const typeId = getUrlParam('typeId');

if (!typeId) {
    document.getElementById('infoType').innerHTML = '<p style="color:red;">Aucun type sélectionné.</p>';
    document.getElementById('listeMachines').innerHTML = '';
} else {
    fetch('/catalogue/machines/api/type/' + typeId)
        .then(r => {
            if (!r.ok) throw new Error('Erreur HTTP ' + r.status);
            return r.json();
        })
        .then(machines => {
            let html = '';
            if (machines.length === 0) {
                html = '<p>Aucune machine disponible pour ce type.</p>';
            } else {
                document.getElementById('infoType').innerHTML = `
                    <div style="border:1px solid #ccc;padding:15px;margin:10px;">
                        <h3>Type: ${machines[0].typeMachine}</h3>
                        <p>${machines.length} machine(s) disponible(s)</p>
                    </div>
                `;

                machines.forEach(m => {
                    html += `
                        <div style="border:1px solid #ccc;padding:15px;margin:10px;border-radius:5px;">
                            <h3>${m.nom}</h3>
                            <p>${m.description || ''}</p>
                            <p><strong>État:</strong> ${m.etatMachine}</p>
                            <p><strong>Prix:</strong> ${m.prixJour} MGA/jour</p>
                            <p><strong>Localisation:</strong> ${m.localisation || 'Non spécifiée'}</p>
                            <p><strong>Disponible:</strong> ${m.disponible ? '✅ Oui' : '❌ Non'}</p>
                            <button onclick="voirDetail(${m.idMachine})">Voir détails</button>
                            <button onclick="reserver(${m.idMachine})" ${!m.disponible ? 'disabled' : ''}>
                                ${m.disponible ? 'reservation' : 'Indisponible'}
                            </button>
                        </div>
                    `;
                });
            }
            document.getElementById('listeMachines').innerHTML = html;
        })
        .catch(err => {
            document.getElementById('listeMachines').innerHTML = 
                '<p style="color:red;">❌ Erreur: ' + err.message + '</p>';
        });
}

function voirDetail(id) {
    window.location.href = '/catalogue/machines/detail?id=' + id;
}

function reserver(id) {
    window.location.href = '/catalogue/reservations/' + id + '/nouvelle?clientId=1';
}