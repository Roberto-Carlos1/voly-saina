// Récupérer les valeurs
const machineId = document.querySelector('input[name="machineId"]').value;
const clientId = document.querySelector('input[name="clientId"]').value;

console.log('Machine ID:', machineId);
console.log('Client ID:', clientId);

// Charger la machine
function loadMachineDetails(machineId) {
    console.log(' Appel API: /catalogue/machines/api/' + machineId);

    fetch('/catalogue/machines/api/' + machineId)
        .then(r => {
            console.log(' Status:', r.status);
            if (!r.ok) throw new Error('HTTP ' + r.status);
            return r.json();
        })
        .then(m => {
            console.log(' Machine reçue:', m);
            document.getElementById('machineInfo').innerHTML = `
                <div style="border:1px solid #28a745;padding:15px;margin:10px;border-radius:5px;background:#d4edda;">
                    <h3>${m.nom}</h3>
                    <p>${m.description || ''}</p>
                    <p><strong>Prix:</strong> ${m.prixJour} MGA/jour</p>
                    <p><strong>Type:</strong> ${m.typeMachine}</p>
                    <p><strong>État:</strong> ${m.etatMachine}</p>
                    <p><strong>Disponible:</strong> ${m.disponible ? ' Oui' : ' Non'}</p>
                </div>
            `;
        })
        .catch(err => {
            console.error(' Erreur:', err);
            document.getElementById('machineInfo').innerHTML = `
                <div style="border:1px solid red;padding:15px;margin:10px;border-radius:5px;background:#f8d7da;">
                    <h3> Erreur</h3>
                    <p>${err.message}</p>
                    <button onclick="chargerMachine()">🔄 Réessayer</button>
                </div>
            `;
        });
}

// Lancer le chargement
chargerMachine();

// Fonction pour afficher le résultat
function afficherResultat(message, estErreur = false) {
    const style = estErreur 
        ? 'border:1px solid red;padding:15px;background:#f8d7da;border-radius:5px;color:red;'
        : 'border:1px solid green;padding:15px;background:#d4edda;border-radius:5px;';
    
    document.getElementById('resultat').innerHTML = `<div style="${style}">${message}</div>`;
}

// Fonction pour soumettre la réservation
function soumettreReservation(ajouterAuPanier = false) {
    const dateDebut = document.getElementById('dateDebut').value;
    const dateFin = document.getElementById('dateFin').value;

    if (!dateDebut || !dateFin) {
        afficherResultat('Veuillez remplir toutes les dates', true);
        return;
    }

    const data = {
        clientId: Number(clientId),
        machineId: Number(machineId),
        dateDebut: dateDebut,
        dateFin: dateFin,
        lieuLivraison: document.getElementById('lieuLivraison').value || ''
    };

    // URL selon le bouton cliqué
    const url = isModification 
        ? '/panier/reservations/api/modifier' 
        : '/panier/reservations/api/ajouter';

    console.log(' Envoi' + (ajouterAuPanier ? ' au panier' : '') + ':', data);

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => {
            console.log(' Status POST:', r.status);
            return r.json();
        })
        .then(response => {
            console.log(' Réponse:', response);
            if (response.error) {
                afficherResultat(response.error, true);
            } else {
                if (ajouterAuPanier) {
                    // Réponse du panier
                    afficherResultat(`
                        <h3 style="color:green;">Ajouté au panier</h3>
                        <p><strong>Machine:</strong> ${response.reservation.machineNom}</p>
                        <p><strong>Période:</strong> ${response.reservation.dateDebut} au ${response.reservation.dateFin}</p>
                        <p><strong>Prix total:</strong> ${response.reservation.prixTotal} MGA</p>
                        <p><em>Statut: en attente de validation (après paiement)</em></p>
                        <br>
                        <button onclick="window.location.href='/client/panier?clientId=${clientId}'"
                                style="padding:8px 15px;background:#007bff;color:white;border:none;border-radius:4px;cursor:pointer;">
                            Voir mon panier
                        </button>
                    `);
                } else {
                    // Réponse de la réservation normale
                    afficherResultat(`
                        <h3 style="color:green;">Réservation créée avec succès</h3>
                        <p><strong>Machine:</strong> ${response.reservation.machineNom}</p>
                        <p><strong>Période:</strong> ${response.reservation.dateDebut} au ${response.reservation.dateFin}</p>
                        <p><strong>Prix total:</strong> ${response.reservation.prixTotal} MGA</p>
                        <br>
                        <button onclick="window.location.href='/client/reservations/mes-reservations?clientId=${clientId}'"
                                style="padding:8px 15px;background:#007bff;color:white;border:none;border-radius:4px;cursor:pointer;">
                            Voir mes réservations
                        </button>
                    `);
                }
                document.getElementById('reservationForm').style.display = 'none';
            }
        })
        .catch(err => {
            console.error('Erreur POST:', err);
            afficherResultat('Erreur: ' + err.message, true);
        });
}

// Gestion du formulaire - distinguer les deux boutons
document.getElementById('reservationForm').addEventListener('submit', function (e) {
    const boutonClique = document.activeElement;
    
    if (boutonClique && boutonClique.id === 'ajouterPanier') {
        e.preventDefault();
        soumettreReservation(true);
    } else {
        e.preventDefault();
        soumettreReservation(false);
    }
});