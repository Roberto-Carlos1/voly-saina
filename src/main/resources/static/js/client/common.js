// Fonctions communes
function getUrlParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

function showError(elementId, message) {
    document.getElementById(elementId).innerHTML = 
        '<p style="color:red;">❌ Erreur: ' + message + '</p>';
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('fr-FR');
}

function formatNumber(n) {
    return Number(n).toLocaleString('fr-FR');
}