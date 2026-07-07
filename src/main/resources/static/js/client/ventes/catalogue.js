(function () {
    function byId(id) { return document.getElementById(id); }

    function renderTableHtml(data) {
        // data = {produits: [...]} ; compatible avec la réponse JSON.
        const produits = data && data.produits ? data.produits : [];
        const tbody = byId('ventesTableBody');
        const empty = byId('ventesEmpty');

        if (!tbody) return;

        tbody.innerHTML = '';

        if (!produits.length) {
            if (empty) empty.style.display = '';
            return;
        }
        if (empty) empty.style.display = 'none';

        produits.forEach(function (p) {
            const estDispo = p.actif && p.disponible;
            const badgeClass = estDispo ? 'badge-green' : 'badge-gray';
            const badgeText = estDispo ? 'Disponible' : 'Indisponible';
            const catName = p.categorie && p.categorie.nom ? p.categorie.nom : '-';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div style="font-weight:700;">${p.nom ? p.nom : '-'}</div>
                    <div class="text-muted" style="font-size:12px;">${p.description ? p.description : ''}</div>
                </td>
                <td>${catName}</td>
                <td>${p.stock != null ? p.stock : '-'}</td>
                <td>${p.dateExpiration ? p.dateExpiration : '-'}</td>
                <td>
                    <span class="badge ${badgeClass}">${badgeText}</span>
                </td>
                <td>
                    <a class="btn btn-primary" href="/client/ventes/${p.idProduit}">Voir</a>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    async function fetchFilteredProducts(params) {
        // Construire l'URL avec les paramètres
        const url = '/catalogue/produits/api/catalogue?' + new URLSearchParams(params).toString();
        const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
        if (!res.ok) throw new Error('Erreur chargement catalogue');
        return res.json();
    }

    function bind() {
        const form = byId('catalogueFilterForm');
        if (!form) return;

        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            const params = {
                q: form.q && form.q.value ? form.q.value : '',
                categorie: form.categorie && form.categorie.value ? form.categorie.value : '',
                disponible: form.disponible && form.disponible.value ? form.disponible.value : ''
            };

            try {
                const data = await fetchFilteredProducts(params);
                renderTableHtml(data);
            } catch (err) {
                // on garde le fallback HTML si besoin
                console.error(err);
            }
        });
    }

    document.addEventListener('DOMContentLoaded', bind);
})();

