fetch('/client/machines/api/types')
    .then(r => {
        if (!r.ok) throw new Error('Erreur HTTP ' + r.status);
        return r.json();
    })
    .then(types => {
        let html = '';
        if (types.length === 0) {
            html = '<p>Aucun type de machine disponible.</p>';
        } else {
            types.forEach(t => {
                html += `
                    <div style="border:1px solid #ccc;padding:15px;margin:10px;border-radius:5px;">
                        <h3>${t.libelle}</h3>
                        <button onclick="voirCatalogue(${t.idTypeMachine})">Voir les machines</button>
                    </div>
                `;
            });
        }
        document.getElementById('listeTypes').innerHTML = html;
    })
    .catch(err => {
        document.getElementById('listeTypes').innerHTML = 
            '<p style="color:red;">❌ Erreur: ' + err.message + '</p>';
    });

function voirCatalogue(typeId) {
    window.location.href = '/client/machines/catalogue?typeId=' + typeId;
}