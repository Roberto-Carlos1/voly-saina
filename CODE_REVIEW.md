# 🌱 Revue de code — Voly Saina +

> **Date de la revue :** 11 juillet 2026
> **Périmètre :** relecture complète du codebase (configuration, entités, services, repositories, ~50 contrôleurs, vues Thymeleaf, docs internes).
> **Objectif :** inventaire des fonctionnalités, description des user flows, et note sur *comment* chaque feature a été construite.

---

## 1. En bref

Plateforme web agricole pour Madagascar, avec **3 piliers métier** + un **back-office gestionnaire** :

1. **Informer** — guide de plantation (cultures, fiches, engrais/outils recommandés)
2. **Équiper** — location de machines agricoles
3. **Approvisionner** — boutique d'engrais/produits avec gestion de stock

C'est un monolithe **Spring MVC + Thymeleaf** (rendu serveur), avec par endroits des **endpoints JSON** consommés en **JS vanilla** (catalogue client, calendrier, filtres). Projet d'équipe (voir `Repartition.md`), échelle « zone pilote ».

---

## 2. Stack & architecture

| Couche | Choix |
|---|---|
| Runtime | **Java 21**, **Spring Boot 4.0.6** (nouveaux starters `spring-boot-starter-webmvc`…) |
| Données | **PostgreSQL** + Spring Data JPA / Hibernate (`ddl-auto=none`, schéma SQL fourni dans `database/`) |
| Vue | **Thymeleaf** + CSS pur (`/css/admin.css` côté admin) + JS vanilla |
| Sécurité | Spring Security (form login, BCrypt) |
| Excel | **Apache POI 5.2.5** (`ExelImport.java`) |
| PDF | **OpenPDF 1.3.30** (export factures / fiches culture) |
| Boilerplate | Lombok (`@Data`) sur la plupart des entités |

**Architecture en couches classique**, très régulière :

```
Controller (web/REST)  →  Service (@Service)  →  Repository (JpaRepository)  →  Entity (@Entity)
```

Deux conventions structurantes que tout le code réutilise :

- **Tables de référence** `Statut*` / `Etat*` / `Type*` / `Mode*` : toutes ont `code` (technique, ex. `en_attente`) + `libelle` (affichable). Le `code` pilote même le **CSS** (`badge--<code>`).
- **Historique d'état machine** : une `Machine` n'a pas de colonne « état » ; son état vit dans une liste `StatutMachine` (machine + etatMachine + date), l'état courant = le dernier statut.

---

## 3. Modèle de données (~40 entités)

Regroupées par domaine :

| Domaine | Entités clés |
|---|---|
| **Utilisateurs** | `Utilisateur` (nom, tel, email, mdp), `RoleUtilisateur`, `StatutCompte`, `ProfilUtilisateur` |
| **Machines** | `Machine`, `TypeMachine`, `EtatMachine`, `StatutMachine` (historique), `ReservationMachine`, `MaintenanceMachine`, `RetourMachine` |
| **Produits/stock** | `Produit`, `CategorieProduit`, `MouvementStock`, `TypeMouvementStock` |
| **Commandes** | `Commande`, `LigneCommande`, `StatutCommande` |
| **Panier** | `Panier`, `PanierDetails` (produits) + panier de réservations (en session) |
| **Facturation** | `Facture` (numero, montantTotal, montantPaye, statut), `Paiement`, `ModePaiement`, `StatutFacture`, `OperationMachine`/`OperationProduit` (lignes de facture) |
| **Prêts** | `PretBancaire`, `RemboursementPret`, `StatutPret` |
| **Interne** | `TacheEmploye`/`StatutTache`, `NoteClient`, `Rapport`, `Pages` (config pagination) |
| **Guide** | `Culture`, `FicheCulture` |

Relations centrales : `ReservationMachine` → (client `Utilisateur`, `Machine`, `Facture?`, `StatutReservation`, `RetourMachine?`). `Facture` → client + lignes d'opérations + `StatutFacture`.

---

## 4. Sécurité, rôles & point d'entrée

`SecurityConfig.java` — form login sur `/connexion` (param `identifiant` = **email OU téléphone**, `motDePasse`), succès → `/profil`.

| Zone | Accès |
|---|---|
| `/connexion`, `/signup`, `/css`, `/js`, **`/catalogue/**`** | public |
| `/profil/**` | authentifié |
| `/client/**`, `/panier/**` | CLIENT, GESTIONNAIRE, RESPONSABLE, EMPLOYE |
| `/admin/**` | **RESPONSABLE** uniquement |
| **tout le reste (dont `/api/**`)** | **public** (`anyRequest().permitAll()`) |

`CustomUserDetailsService` : charge par email/tel, **refuse les comptes non `actif`**, mappe `role.code` → `ROLE_<CODE>`. L'encodeur accepte le **BCrypt ET l'ancien clair** (migration douce des comptes existants).

---

## 5. User flows

### 🧑‍🌾 Client (agriculteur)

```
/ (accueil public)
  → /connexion  (email ou tel + mdp)  → /profil
  → Guide: /guide-plantation/cultures → fiche culture → (suggère machines & produits)
  → Louer: /catalogue/machines/catalogue → detail → /catalogue/reservations/{machineId}/nouvelle
        → POST /catalogue/reservations/api : vérifie conflits de dates, calcule prix = prixJour × nb jours,
          crée la réservation en statut « en_attente »
        → /catalogue/reservations/mes-reservations  → facturer → payer
  → Acheter: /catalogue/produits/catalogue → /panier/ajouter → /panier → valider → facture → paiement
```

Le calcul prix + vérif dispo est dans `ClientReservationController` (`findConfList` → conflits, `ChronoUnit.DAYS.between` → durée). La **facturation** (`/api/{id}/facturer`) crée une `Facture` (`typeOperation=location`, échéance +14j), passe la réservation en « validee ».

### 🛠️ Gestionnaire (RESPONSABLE) — `/admin/**`

```
/admin/dashboard  (KPIs flotte, réservations, finances 6 mois, alertes maintenance, stock, clients)
  → /admin/machines           (liste flotte + filtres + stats)  → /{id} fiche machine
  → /admin/reservations-machine (liste + tri + détail)  → /{id} fiche + calendrier
  → /admin/maintenances-machine (planif. maintenance)
  → /admin/factures, /admin/paiements  (facturation, encaissements)
  → /admin/files + /api/exel  (import Excel en masse)
```

Le `DashboardController` est représentatif : il agrège tout **en mémoire** dans le contrôleur (compte par statut, CA par mois `YearMonth`, taux d'utilisation, alertes) et passe des `Map` prêtes à afficher aux vues.

---

## 6. Comment chaque grande feature est construite

- **Auth / inscription** — `AuthController` crée l'`Utilisateur` (rôle + statut par défaut créés si absents), BCrypt à l'encodage. Login = Spring Security standard.
- **Guide de plantation** — CRUD `Culture`/`FicheCulture` + **export PDF/Excel** (OpenPDF/POI) via `GuidePlantationExportService`. Public.
- **Location de machines** — cœur du domaine : dispo par requête de conflit de dates (`MachineRepository.findConflictingReservations`), état machine via historique `StatutMachine`, réservation → facture → paiement.
- **Calendrier** — `CalendarController` : page Thymeleaf + endpoint `/calendrier/events` renvoyant du **JSON allégé** (pas d'entité brute) consommé par un calendrier **JS vanilla** maison.
- **Boutique / stock** — `Produit` avec `stock`/`seuilStock` ; panier produits en base (`Panier`/`PanierDetails`) ; validation commande → mouvement de stock (`MouvementStock`). Alertes stock faible calculées au dashboard.
- **Facturation & paiements** — `Facture` porte `montantTotal`/`montantPaye` ; paiements partiels supportés ; export PDF facture.
- **Import Excel générique** — `ExelImport.java` : très astucieux — génère un **template multi-entités par réflexion** (colonnes = champs JPA, en excluant `@GeneratedValue`/`@CreationTimestamp`) et réimporte en repeuplant les entités via réflexion + `EntityManager`.
- **Prêts / tâches / rapports / notes client** — CRUD standards (surtout via l'API REST publique `/api/**`).

---

## 7. Observations de revue

### ✅ Points forts

- Découpage en couches **très cohérent** et prévisible ; facile à naviguer pour des étudiants.
- Bonnes idées : import Excel par réflexion, calendrier JSON+vanilla, encodeur mdp rétro-compatible, `route.md` maintenu.
- Le back-office redessiné (design system CSS pur + fragments) est propre et homogène.

### ⚠️ À corriger / risques (par priorité)

1. 🔴 **`/api/**` entièrement public** (`anyRequest().permitAll()`). Les contrôleurs CRUD REST exposent **toutes** les entités sans auth — dont `/api/utilisateurs` (données de comptes). À restreindre. `/catalogue/**` public est plus défendable (vitrine).
2. 🔴 **CSRF désactivé globalement** alors qu'il y a des sessions + formulaires POST. À réactiver (au moins hors API).
3. 🟠 **`resolveClientId(...)` retombe sur `1L`** dans les contrôleurs panier si non authentifié → un visiteur peut agir « au nom du client 1 ». À sécuriser.
4. 🟠 **Logique métier dans les contrôleurs** (génération de facture, calcul prix, agrégations dashboard) plutôt qu'en service → peu réutilisable/testable.
5. 🟠 **`ReservationMachineController.update` (PUT)** fait `save()` d'une entité issue du `@RequestBody` → **écrase les associations non transmises** (pattern à éviter). De même, mutations d'état par **liens GET** (`/supprimer/{id}`, et les futurs `/valider`,`/refuser`) — non idempotent.
6. 🟡 **Incohérence « état courant machine »** : le dashboard utilise `findCurrentByMachineId`, mais `Machine.getEtatMachine()` prend `statuts.get(0)` trié par `dateCreation` (granularité **jour**) → résultats potentiellement différents si plusieurs statuts le même jour. Et `MachineRepository.countMachinesByEtat()` **compte tout l'historique** (joint tous les `statuts`), donc surcompte — ne pas l'utiliser pour des compteurs « courant ».
7. 🟡 **N+1** fréquents (`findAll()` puis accès lazy en boucle : dashboard, réservations, détail). OK à l'échelle pilote, à surveiller.
8. 🟡 **Résidus** : dossier `templates/Admin/` = maquettes Stitch **non câblées** ; deux `index`/`index_true` ; `ClientController.accueil` renvoie `"index"` ; typo `ExelImport`. Mélange FR/EN dans le nommage.

---

*Revue générée le 11 juillet 2026 — Voly Saina +.*
