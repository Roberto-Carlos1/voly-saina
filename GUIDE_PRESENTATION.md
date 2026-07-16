# Guide de présentation — VolySaina+

> Antisèche pour la soutenance : **« qu'est-ce qui gère ce qu'on voit à l'écran ? »**
> Chaque fonctionnalité visible est reliée à son contrôleur (reçoit la requête), son service (logique métier) et son template (affichage).

## Comment le code est organisé (à dire en 30 secondes)

Application **Spring Boot MVC + Thymeleaf + PostgreSQL**. Pour n'importe quel écran, le chemin est toujours le même :

```
URL du navigateur → Contrôleur (controller/…) → Service (service/…) → Repository (repository/…) → BDD
                                                                    ↘ Template Thymeleaf (templates/…) → HTML affiché
```

Règle pratique pendant les questions : **l'URL visible dans la barre d'adresse dit quel contrôleur regarde**. `/panier/...` → `PanierController`, `/client/factures/...` → `ClientFactureController`, `/client/profil` → `ClientProfilController`, etc.

---

## 1. Connexion / Inscription

| Ce qu'on voit | URL | Qui gère |
|---|---|---|
| Page de connexion | `/connexion` | `AuthPageController` → template `auth/login.html` |
| Soumission du login | POST `/connexion` | **Spring Security** (`config/SecurityConfig.java`) — champs `identifiant` / `motDePasse` |
| Page d'inscription | `/signup` | `auth/AuthController` → `auth/signup.html` |
| Création du compte | POST `/signup` | `AuthController` → `UtilisateurService` (rôle `client` + statut `actif` par défaut) |
| Déconnexion | `/deconnexion` | Spring Security (logout configuré dans `SecurityConfig`) |

Points à savoir :
- Les **rôles** (`client`, `gestionnaire`, `responsable`, `employe`) contrôlent l'accès aux URLs : `/client/**` réservé aux connectés, `/admin/**` au responsable — tout est déclaré dans `SecurityConfig.filterChain()`.
- Un compte désactivé (statut `inactif`) est **refusé à la connexion** : vérification dans `UtilisateurService` (`"actif".equalsIgnoreCase(...)`).

## 2. Catalogue machines et réservation

| Ce qu'on voit | URL | Qui gère |
|---|---|---|
| Catalogue des machines (filtres, cartes) | `/catalogue/machines/catalogue` | `client/machine/ClientMachineController` → `client/machines/catalogue.html` |
| Détail d'une machine | `/catalogue/machines/detail?id=…` | idem → `client/machines/detail.html` |
| Formulaire de réservation (dates, lieu) | `/catalogue/reservations/{machineId}/nouvelle` | `client/machine/ClientReservationController` → `client/reservations/form.html` |
| Le bouton « Réserver » du formulaire | JS `fetch('/catalogue/reservations/api')` puis `fetch('/panier/reservations/api/ajouter')` | `ClientReservationService.createReservation()` — calcule le **prix total** (prix/jour × nb jours), vérifie les **conflits de dates** (`ReservationMachineRepository.findConflictingReservations`) |
| Mes réservations (liste + statuts) | `/catalogue/reservations/mes-reservations` | `ClientReservationController` → `client/reservations/list.html` |
| Annuler une réservation | `/catalogue/reservations/{id}/annuler` | `ClientReservationService.annulerReservationByClient()` |
| Retour de machine (fin de location) | `/catalogue/retours/{reservationId}/nouveau` | `client/machine/ClientRetourController` — calcule la **pénalité de retard** |

Cycle de vie d'une réservation (visible via les badges de statut) : `en_attente` → `validee` → `en_cours` → `terminee` (ou `refusee` / `annulee`). Les statuts sont des lignes de la table `statut_reservation`.

## 3. Catalogue produits et panier

| Ce qu'on voit | URL | Qui gère |
|---|---|---|
| Catalogue produits (engrais, semences…) | `/catalogue/produits/catalogue` | `client/VenteController` → `client/ventes/catalogue.html` |
| Détail produit | `/catalogue/produits/{id}` | idem → `client/ventes/detail.html` |
| Bouton « Ajouter au panier » | `/panier/ajouter?produitId=…&quantite=…` | `client/PanierController` → `PanierService.ajouterAuPanier()` |
| Page panier (produits + réservations ensemble) | `/panier` | `PanierController` → `client/panier/commandePanier.html` |
| Changer la quantité | POST `/panier/quantite` | `PanierController` |
| Supprimer un article / une réservation | POST `/panier/supprimer` / `/panier/reservations/supprimer` | `PanierController` / `PanierReservationController` |
| **Valider le panier** | POST `/panier/valider` | `PanierService.cloturerPanier()` — **c'est ici que la facture naît** (voir §4) |

Particularité à mettre en avant : le panier est **mixte** — il peut contenir à la fois des produits (commande) et des locations de machines (réservations), et une seule facture couvre le tout.

## 4. Création de facture (la question qui tombera sûrement)

Quand le client clique **« Valider le panier »** :

1. `PanierController.cloturerPanier()` (POST `/panier/valider`) appelle `PanierService.cloturerPanier()` ;
2. qui appelle `FactureService.genererFactureProformat()` (`service/FactureService.java:140`) : création de la facture avec
   - **numéro** généré par `generateNumeroFacture()` → format `FAC-2026-<id>-<id sur 4 chiffres>`,
   - montant total = montant commandes + montant réservations du panier,
   - statut initial `en_attente`, montant payé 0, **date limite = +14 jours** ;
3. puis la commande est clôturée (`PanierCommandeService`) et les réservations du panier passent en `validee` ;
4. redirection visible vers **« Mes factures »** (`/client/factures`).

## 5. Factures et paiement

| Ce qu'on voit | URL | Qui gère |
|---|---|---|
| Mes factures (liste + filtre statut) | `/client/factures` | `client/ClientFactureController` → `ClientFactureService.listerFacturesClient()` → `client/factures/list.html` |
| Détail d'une facture (lignes, montants) | `/client/factures/{id}` | idem → `client/factures/detail.html` |
| Page de paiement (mode, montant) | `/client/factures/paiement` | idem → `client/factures/paiement.html` |
| Bouton « Payer » | POST `/client/factures/achat` | `PaiementService.payerFacture()` — valide montant/date, met à jour `montant_paye` et fait évoluer le statut (`payee` / `partiellement_payee`) |
| Télécharger le PDF | `/client/factures/{id}/pdf` | `ClientFactureService.exporterFacturePDF()` — génère le PDF **en Java** (librairie OpenPDF/iText : en-tête société, tableau des lignes, montants) |

Statuts de facture visibles en badges : `en_attente`, `payee`, `partiellement_payee`, `en_retard` (calculé : date limite dépassée et non soldée), `annulee`.

## 6. Page profil client (`/client/profil`) — les 6 onglets

Tout est servi par **une seule route** : `ClientProfilController.profilPrincipal()` charge l'utilisateur, ses listes et les stats, puis le template `client/profil/index.html` affiche le tout en onglets Bootstrap (aucun rechargement entre onglets).

| Onglet | Données affichées | D'où elles viennent |
|---|---|---|
| **Informations** | nom, téléphone, genre, âge, CSP, localisation | entités `Utilisateur` + `ProfilUtilisateur` ; enregistrement via POST `/client/profil/informations` → `ClientProfilService.updateUtilisateur()` / `updateProfilUtilisateur()` |
| **Réservations** | tableau machine / période / lieu / statut / prix | `ClientProfilService.listerReservationsClient()` (triées de la plus récente à la plus ancienne) |
| **Commandes** | référence, date, nb d'articles, statut, montant | `listerCommandesClient()` + `compterArticlesParCommande()` |
| **Factures** | numéro (cliquable → détail), date, statut, montant, bouton PDF | réutilise `ClientFactureService.listerFacturesClient()` |
| **Statistiques** | cartes + 4 graphiques (voir §7) | `ClientStatistiqueService.genererStatistiquesClient()` |
| **Paramètres** | changement de mot de passe, suppression de compte | POST `/client/profil/mot-de-passe` (vérifie l'ancien mdp) ; POST `/client/profil/supprimer` → **soft delete** : le compte passe au statut `inactif`, rien n'est effacé, la connexion est refusée ensuite |

Les 4 **cartes en haut** (Réservations / Commandes / Factures / Total dépensé) comptent **tout l'historique** et sont calculées par `ClientProfilService.calculerQuickStats()` à partir des mêmes listes que les onglets — les chiffres du haut correspondent donc toujours à ce qu'on voit en bas.

Le retour d'action (« Profil mis à jour avec succès », « Ancien mot de passe incorrect »…) passe par des **flash attributes** (`RedirectAttributes`) affichés en bandeau vert/rouge en haut de page.

## 7. Statistiques client — ce qui est calculé, et pourquoi c'est présenté comme ça

### 7.1 Le calcul (backend)

`ClientStatistiqueService.genererStatistiquesClient(idClient, dateDebut, dateFin)` produit un DTO unique (`StatistiquesClientDTO`) contenant :

- **compteurs et totaux** de la période : nb locations / commandes / factures, total dépensé (locations + achats) ;
- **dépenses par mois** : les factures de la période groupées par mois (`YearMonth`) ;
- **top 5 machines** : réservations groupées par machine, triées par nombre de locations décroissant ;
- **top 5 produits** : lignes de commande groupées par produit, triées par quantité décroissante.

Seuls les statuts « réels » comptent (réservations `validee`/`en_cours`/`terminee`, commandes `validee`→`livree`) : une réservation refusée ou une commande annulée **ne gonfle pas** les statistiques.

Les **filtres de période** (3/6/12 mois, année en cours, dates libres) sont résolus par `periodesDisponibles()` / `periodeDefaut()` (défaut : 12 derniers mois) et passés en paramètres d'URL (`?tab=stats&periode=3mois`).

### 7.2 Les choix de présentation (à défendre devant le jury)

**Intégration en onglet plutôt qu'en page séparée.** L'utilisateur consulte ses stats dans le contexte de son profil, avec les mêmes données que les autres onglets, sans navigation supplémentaire. L'ancienne URL `/client/statistiques` redirige vers l'onglet pour ne casser aucun lien.

**D'abord les chiffres, ensuite les graphiques.** La rangée de cartes (Total dépensé, Réservations, Commandes, Factures) donne la réponse immédiate — un chiffre se lit plus vite qu'un graphique. Les graphiques en dessous servent à voir les *tendances* et les *comparaisons*, pas à retrouver une valeur exacte (les valeurs exactes sont dans les infobulles au survol).

**Un type de graphique par question posée :**

| Question de l'utilisateur | Graphique choisi | Pourquoi |
|---|---|---|
| « Quand est-ce que je dépense ? » | **Barres verticales** (dépenses par mois) | le temps se lit de gauche à droite, la hauteur compare les mois d'un coup d'œil |
| « Mes dépenses vont plutôt en location ou en achats ? » | **Barre unique empilée** (répartition) | pour comparer *2 parts d'un tout*, une barre empilée se lit mieux qu'un camembert : l'œil compare des longueurs bien plus précisément que des angles |
| « Quelles machines je loue le plus ? » | **Barres horizontales** (top 5) | les noms de machines sont longs → à l'horizontale ils restent lisibles sans rotation ; tri décroissant = le « podium » se lit de haut en bas |
| « Quels produits j'achète le plus ? » | **Barres horizontales** (top 5) | même logique ; l'infobulle ajoute catégorie et montant sans surcharger le graphique |

**Une couleur = une signification, sur toute la page.** Vert foncé `#166534` = locations/machines, orange `#fd925b` = achats/produits — les mêmes couleurs dans la barre de répartition et dans les tops, donc une fois la légende lue, tout le reste se comprend sans la relire. La paire de couleurs a été **validée pour le daltonisme** (écart suffisant en vision protanope/deutéranope/tritanope) et les identités ne reposent jamais sur la couleur seule (les noms sont toujours écrits sur l'axe ou en légende).

**Pas de « rainbow charts »**. Un seul jeu de couleurs sobre, celui de la charte du site (vert VolySaina), plutôt qu'une couleur différente par barre qui n'apporterait aucune information.

**États vides prévus.** Si la période ne contient rien, on affiche un message explicite (« Aucune dépense sur cette période ») avec une icône — jamais un graphique vide ou cassé.

**Détail technique appréciable** : les graphiques (Chart.js) ne sont construits **qu'à l'ouverture de l'onglet** (événement `shown.bs.tab`) — pas de coût au chargement de la page, et les canvas se dimensionnent correctement puisqu'ils ne sont jamais dessinés dans un onglet masqué.

## 8. Guide de plantation (bonus si on vous interroge dessus)

| Ce qu'on voit | URL | Qui gère |
|---|---|---|
| Liste des cultures | `/guide-plantation` | `GuidePlantationController` → `guide-plantation/cultures.html` |
| Détail / fiche d'une culture | `/guide-plantation/cultures/{id}` et `…/fiche` | idem |
| Boutons export **PDF / Excel** | `…/export/pdf`, `…/export/excel` | génération côté serveur dans le contrôleur/service associé |

## 9. Côté admin (une phrase suffit)

Le tableau de bord responsable (`/admin/dashboard`, `DashboardController`) agrège les indicateurs globaux ; la gestion des machines, maintenances, stocks et paiements passe par les contrôleurs des packages `machine/`, `stock/`, `paiement/` — même schéma contrôleur → service → template que côté client.

---

## Questions éclair probables (réponses en une phrase)

- **« Qui génère le numéro de facture ? »** → `FactureService.generateNumeroFacture()`, format `FAC-<année>-<id>-<id sur 4 chiffres>`, au moment de la validation du panier.
- **« Où est calculé le prix d'une réservation ? »** → `ClientReservationService.createReservation()` : prix/jour de la machine × nombre de jours.
- **« Comment vous empêchez deux réservations sur les mêmes dates ? »** → requête `findConflictingReservations` dans `ReservationMachineRepository` (JPQL sur le chevauchement de périodes).
- **« Le PDF de facture, c'est quoi ? »** → généré en Java dans `ClientFactureService.exporterFacturePDF()` (OpenPDF) : en-tête société, lignes de la facture, montants.
- **« Supprimer un compte supprime les données ? »** → non, *soft delete* : statut du compte → `inactif`, l'historique (factures, réservations) est conservé, la connexion est bloquée.
- **« Pourquoi une barre empilée et pas un camembert pour la répartition ? »** → deux catégories seulement, et l'œil compare mieux des longueurs que des angles ; le camembert n'apporte rien ici.
- **« D'où viennent les chiffres des cartes en haut du profil ? »** → des mêmes listes que les onglets (`calculerQuickStats()`), donc toujours cohérents avec ce qui est affiché en dessous.
- **« Les statuts (réservation, facture…) sont codés en dur ? »** → non, ce sont des tables de référence (`statut_reservation`, `statut_facture`, …) reliées par clé étrangère ; les badges colorés du front se basent sur le champ `code`.
