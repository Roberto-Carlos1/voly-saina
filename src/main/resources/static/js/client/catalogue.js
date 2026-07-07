fetch('/catalogue/machines/api/catalogue')
    .then(r => r.json())
    .then(machines => {
        let html = '';
        machines.forEach(m => {
            html += `
                <div style="border:1px solid #ccc;padding:10px;margin:10px;">
                    <h3>${m.nom}</h3>
                    <p>${m.description || ''}</p>
                    <p>Type: ${m.typeMachine}</p>
                    <p>État: ${m.etatMachine}</p>
                    <p>Prix: ${m.prixJour} MGA/jour</p>
                    <p>Disponible: ${m.disponible ? 'Oui' : 'Non'}</p>
                    <button onclick="reserver(${m.idMachine})" ${!m.disponible ? 'disabled' : ''}>
                        ${m.disponible ? 'Réserver' : 'Indisponible'}
                    </button>
                </div>
            `;
        });
        document.getElementById('listeMachines').innerHTML = html;
    })
    .catch(err => {
        document.getElementById('listeMachines').innerHTML = 'Erreur: ' + err.message;
    });

function reserver(id) {
    window.location.href = '/client/reservations/' + id + '/nouvelle?clientId=1';
}