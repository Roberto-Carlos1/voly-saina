## Boucle

<tr th:each="produit : ${produits}">
    <td th:text="${produit.nom}"></td>
    <td th:text="${produit.prix}"></td>
</tr>

${produits} : liste envoyée depuis le controller
produit : variable temporaire

## Condition
<p th:if="${produit.prix > 1000}">Cher</p>
<p th:unless="${produit.prix > 1000}">Pas cher</p>

## Afficher les donnees
    remplacer le contenu de la balise
    <p th:text="${produit.nom}"></p>

## Lien
    <a th:href="@{/produits}">Voir</a>
    <a th:href="@{/produit/{id}(id=${produit.id})}">Détail</a>

## Formulaire
    <form th:action="@{/produits}" th:object="${produit}" method="post">

    <input type="text" th:field="*{nom}" />
    <input type="number" th:field="*{prix}" />