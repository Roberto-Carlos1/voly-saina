# Récapitulatif des modifications et Architecture des Routes (Dév)

Ce document résume l'ensemble des travaux effectués sur le refactoring des routes (URLs) du projet `voly-saina`, et liste l'état actuel de toutes les APIs backend.

## 1. Modifications effectuées (Refactoring des Vues)

L'objectif principal était de séparer les routes des pages web (Thymeleaf) du préfixe `/api/` pour obtenir des URLs sémantiques et propres (de type e-commerce et gestion backoffice).

### Modifications Back-office :
* **Authentification & Profil :**
  * `/page01` ➡️ `/connexion` (Login)
  * `/page02` ➡️ `/profil` (Espace utilisateur)
  * Configuration de Spring Security (`SecurityConfig.java`) mise à jour.
* **Gestion des Machines :**
  * `/api/machines/...` ➡️ `/machines/...` (Liste, Ajouter, Modifier, Supprimer, Détails)
  * `/api/maintenances-machine/...` ➡️ `/maintenances-machine/...`
* **Facturation & Paiements :**
  * `/api/factures/...` ➡️ `/factures/...`
  * `/api/paiements/...` ➡️ `/paiements/...`

### Modifications Front-office (Client) :
* **Catalogue et E-commerce :**
  * `/client/machines` ➡️ `/catalogue/machines`
  * `/client/reservations` ➡️ `/catalogue/reservations`
  * `/client/retours` ➡️ `/catalogue/retours`
  * `/client/ventes` ➡️ `/catalogue/produits`
  * `/client/panier` ➡️ `/panier`

### Fixes associés :
Toutes les templates HTML (`th:href`, `th:action`) ont été mises à jour pour correspondre à ces chemins.
Les appels asynchrones en Javascript (`fetch()`) ont été corrigés pour repointer vers les nouvelles routes hybrides (ex: `/catalogue/machines/api/...`).

---

## 2. Architecture Actuelle des APIs (JSON / REST)

Voici la liste des APIs retournant du JSON (ResponseEntity, @ResponseBody) qui existent actuellement dans le code.

### A. Les vraies APIs (Pure REST Controllers)
Ces APIs sont principalement utilisées pour des opérations CRUD standard, ou pour une utilisation par d'autres systèmes, et elles commencent toutes par `/api/` :

* **Statuts de Commande** (`StatutCommandeController`)
  * `GET /api/statuts-commande`
  * `GET /api/statuts-commande/{id}`
  * `POST /api/statuts-commande`
  * `PUT /api/statuts-commande/{id}`
  * `DELETE /api/statuts-commande/{id}`
* **Statuts de Facture** (`StatutFactureController`)
  * `GET /api/statuts-facture`, `POST`, `PUT`, `DELETE`
* **Statuts de Tâche** (`StatutTacheController`)
  * `GET /api/statuts-tache`, `POST`, `PUT`, `DELETE`
* **Cultures** (`CultureController`)
  * `GET /api/cultures`, `POST`, `PUT`, `DELETE`
* **Divers**
  * `POST /api/exel/import` (Import Excel)
  * `GET /api/exel/template/{entity}` (Template Excel)

### B. Les APIs Hybrides Backoffice (AJAX Filtres)
Ces endpoints sont appelés via Javascript dans le backoffice pour filtrer des tableaux :
* `POST /machines/api/filtre` : Filtre les machines par type, nom, etc.
* `POST /maintenances-machine/api/filtre` : Filtre les maintenances.
* `GET /reservations-machine/calendrier/events` : Charge les événements pour FullCalendar.

### C. Les APIs Hybrides Client (E-commerce Frontend)
Ces APIs alimentent l'expérience utilisateur côté client (AJAX) depuis les pages du catalogue :
* **Machines :**
  * `GET /catalogue/machines/api/types`
  * `GET /catalogue/machines/api/catalogue`
  * `GET /catalogue/machines/api/disponibles`
  * `GET /catalogue/machines/api/type/{typeId}`
  * `GET /catalogue/machines/api/{id}`
* **Réservations :**
  * `GET /catalogue/reservations/api/client/{clientId}`
  * `POST /catalogue/reservations/api/{id}/annuler`
* **Retours :**
  * `GET /catalogue/retours/api/form/{reservationId}`
  * `POST /catalogue/retours/api`
* **Panier :**
  * `POST /panier/reservations/api/ajouter`
  * `POST /panier/reservations/api/modifier`
* **Ventes / Produits :**
  * `GET /catalogue/produits/api/catalogue`

*(Note : à l'avenir, les sections B et C pourront être extraites dans des `RestController` indépendants pour normaliser les URL en `/api/catalogue/...` si cela est requis pour un découplage total).*
