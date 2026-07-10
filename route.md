# Liste des routes — Voly Saina+

> Généré depuis les contrôleurs de `src/main/java/com/voly_saina/controller/`.
> Branche : `flottemachines-frontend-admin-dev`.
> Dernière vérification : 2026-07-10.

## Règles de sécurité (rappel — `SecurityConfig`)

| Pattern | Accès |
|---------|-------|
| `/connexion`, `/login`, `/inscription`, `/signup`, `/access-denied`, `/css/**`, `/js/**`, `/images/**`, `/webjars/**`, `/catalogue/**` | **public** (`permitAll`) |
| `/profil`, `/profil/**` | **authentifié** (n'importe quel rôle connecté) |
| `/client/**`, `/panier/**` | rôles `CLIENT`, `GESTIONNAIRE`, `RESPONSABLE`, `EMPLOYE` |
| `/admin/**` | rôle **`RESPONSABLE`** uniquement |
| tout le reste (`/`, `/route`, `/guide-plantation/**`, `/api/**`, ...) | **public** (`anyRequest().permitAll()`) |

> ⚠️ Conséquence : toutes les API `/api/**`, le hub `/route` et les catalogues `/catalogue/**` sont **publics**. Seul `/admin/**` est réellement protégé.

Login : `POST /connexion` — paramètres `identifiant` (email ou téléphone) + `motDePasse`. Déconnexion : `POST /deconnexion`.

---

## 1. Pages générales & authentification

| Méthode | Route | Contrôleur | Description |
|---------|-------|-----------|-------------|
| GET | `/` | IndexController | Page d'accueil publique (`index`) |
| GET | `/route` | IndexController | Hub back-office (`index_true`) |
| GET | `/connexion`, `/login` | AuthPageController | Formulaire de connexion |
| POST | `/connexion` | (Spring Security) | Traitement du login |
| POST | `/inscription` | AuthPageController | Traitement de l'inscription |
| GET | `/signup` | AuthController | Formulaire d'inscription |
| POST | `/signup` | AuthController | Création de compte |
| GET | `/access-denied` | AuthController | Page accès refusé |
| POST | `/deconnexion` | (Spring Security) | Déconnexion |
| GET | `/profil` | ProfilPageController | Profil utilisateur |
| POST | `/profil/enregistrer` | ProfilPageController | Enregistrer le profil |

---

## 2. Espace Client

### Accueil / profil / statistiques
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/client/accueil` | ClientController |
| GET | `/client/profil` | ClientProfilController |
| GET | `/client/profil/informations` | ClientProfilController |
| POST | `/client/profil/informations` | ClientProfilController |
| GET | `/client/profil/parametres` | ClientProfilController |
| POST | `/client/profil/mot-de-passe` | ClientProfilController |
| GET | `/client/statistiques` | ClientStatistiqueController |

### Factures client
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/client/factures` | ClientFactureController |
| GET | `/client/factures/{idFacture}` | ClientFactureController |
| GET | `/client/factures/paiement` | ClientFactureController |
| POST | `/client/factures/achat` | ClientFactureController |
| GET | `/client/factures/{idFacture}/pdf` | ClientFactureController |

### Catalogue machines (`/catalogue/machines`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/catalogue/machines/types` | ClientMachineController |
| GET | `/catalogue/machines/catalogue` | ClientMachineController |
| GET | `/catalogue/machines/detail` | ClientMachineController |
| GET | `/catalogue/machines/api/types` | ClientMachineController |
| GET | `/catalogue/machines/api/catalogue` | ClientMachineController |
| GET | `/catalogue/machines/api/disponibles` | ClientMachineController |
| GET | `/catalogue/machines/api/type/{typeId}` | ClientMachineController |
| GET | `/catalogue/machines/api/{id}` | ClientMachineController |

### Catalogue produits / engrais (`/catalogue/produits`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/catalogue/produits/catalogue` | VenteController |
| GET | `/catalogue/produits/disponibles` | VenteController |
| GET | `/catalogue/produits/{id}` | VenteController |
| GET | `/catalogue/produits/api/catalogue` | VenteController |
| GET | `/catalogue/produits/api/produits` | VenteController |
| GET | `/catalogue/produits/api/produits/{id}` | VenteController |
| GET | `/catalogue/produits/api/produits/disponibles` | VenteController |

### Réservations client (`/catalogue/reservations`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/catalogue/reservations/{machineId}/nouvelle` | ClientReservationController |
| GET | `/catalogue/reservations/{id}/annuler` | ClientReservationController |
| GET | `/catalogue/reservations/mes-reservations` | ClientReservationController |
| GET | `/catalogue/reservations/{id}` | ClientReservationController |
| GET | `/catalogue/reservations/facture/{factureId}` | ClientReservationController |
| GET | `/catalogue/reservations/api/client/{clientId}` | ClientReservationController |
| GET | `/catalogue/reservations/api/client/{clientId}/statut/{statut}` | ClientReservationController |
| GET | `/catalogue/reservations/api/{id}` | ClientReservationController |
| GET | `/catalogue/reservations/api/client/{clientId}/active` | ClientReservationController |
| POST | `/catalogue/reservations/api` | ClientReservationController |
| POST | `/catalogue/reservations/api/{id}/facturer` | ClientReservationController |
| POST | `/catalogue/reservations/api/facture/{factureId}/payer` | ClientReservationController |
| GET | `/catalogue/reservations/api/facture/{factureId}/reservations` | ClientReservationController |
| PUT | `/catalogue/reservations/api/{id}/annuler` | ClientReservationController |
| PUT | `/catalogue/reservations/api/client/{clientId}/annuler-tout` | ClientReservationController |

### Retours machines (`/catalogue/retours`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/catalogue/retours/{reservationId}/nouveau` | ClientRetourController |
| GET | `/catalogue/retours/api/form/{reservationId}` | ClientRetourController |
| GET | `/catalogue/retours/api/reservation/{reservationId}` | ClientRetourController |
| GET | `/catalogue/retours/api/client/{clientId}` | ClientRetourController |
| POST | `/catalogue/retours/api` | ClientRetourController |
| GET | `/catalogue/retours/api/penalite/{reservationId}` | ClientRetourController |

### Panier (`/panier`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/panier/ajouter` | PanierController |
| GET | `/panier` | PanierController |
| GET | `/panier/details` | PanierController |
| POST | `/panier/supprimer` | PanierController |
| POST | `/panier/quantite` | PanierController |
| POST | `/panier/valider` | PanierController |
| GET | `/panier/recap` | PanierController |

### Panier réservations (`/panier/reservations`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| POST | `/panier/reservations/ajouter` | PanierReservationController |
| POST | `/panier/reservations/api/ajouter` | PanierReservationController |
| POST | `/panier/reservations/supprimer` | PanierReservationController |
| POST | `/panier/reservations/modifier-dates` | PanierReservationController |
| POST | `/panier/reservations/valider-tout` | PanierReservationController |
| POST | `/panier/reservations/vider` | PanierReservationController |
| GET | `/panier/reservations/api/liste` | PanierReservationController |

---

## 3. Guide de plantation (`/guide-plantation`) — public

| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/guide-plantation`, `/guide-plantation/`, `/guide-plantation/cultures` | GuidePlantationController |
| GET | `/guide-plantation/cultures/export/pdf` | GuidePlantationController |
| GET | `/guide-plantation/cultures/export/excel` | GuidePlantationController |
| GET | `/guide-plantation/cultures/{idCulture}` | GuidePlantationController |
| GET | `/guide-plantation/cultures/{idCulture}/fiche` | GuidePlantationController |
| GET | `/guide-plantation/cultures/{idCulture}/fiche/export/pdf` | GuidePlantationController |

---

## 4. Espace Back-office (`/admin/**`) — rôle RESPONSABLE

### Tableau de bord (`/admin/dashboard`)
| Méthode | Route | Contrôleur | Description |
|---------|-------|-----------|-------------|
| GET | `/admin/dashboard` | DashboardController | Vue d'ensemble analytique (`admin/dashboard`) |

### Machines (`/admin/machines`)
| Méthode | Route | Contrôleur | Vue |
|---------|-------|-----------|-----|
| GET | `/admin/machines`, `/admin/machines/` | MachineController | `machines/list` (liste de la flotte) |
| POST | `/admin/machines/pages` | MachineController | — (config. nb/page, redirige) |
| GET | `/admin/machines/ajouter` | MachineController | `machines/insert-machine` |
| POST | `/admin/machines/ajouter` | MachineController | — (création, redirige) |
| GET | `/admin/machines/modifier/{id}` | MachineController | `machines/modify-machine` |
| POST | `/admin/machines/modifier/{id}` | MachineController | — (modification, redirige) |
| GET | `/admin/machines/supprimer/{id}` | MachineController | — (suppression, redirige) |
| GET | `/admin/machines/{id}` | MachineController | `machines/detail-machine` (fiche détail redessinée) |
| POST | `/admin/machines/api/filtre` | MachineController | JSON `Page<Machine>` (filtre AJAX) |

### Maintenances (`/admin/maintenances-machine`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/admin/maintenances-machine` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine/pages` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine/api/filtre` | MaintenanceMachineController |
| GET | `/admin/maintenances-machine/ajouter` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine/ajouter` | MaintenanceMachineController |
| GET | `/admin/maintenances-machine/modifier/{id}` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine/modifier/{id}` | MaintenanceMachineController |
| GET | `/admin/maintenances-machine/supprimer/{id}` | MaintenanceMachineController |
| GET | `/admin/maintenances-machine/{id}` | MaintenanceMachineController |
| GET | `/admin/maintenances-machine/valider/{id}` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine/valider` | MaintenanceMachineController |
| POST | `/admin/maintenances-machine` | MaintenanceMachineController |
| PUT | `/admin/maintenances-machine/{id}` | MaintenanceMachineController |
| DELETE | `/admin/maintenances-machine/{id}` | MaintenanceMachineController |

### Réservations (`/admin/reservations-machine`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/admin/reservations-machine` | ReservationMachineController |
| GET | `/admin/reservations-machine/{id}` | ReservationMachineController |
| POST | `/admin/reservations-machine` | ReservationMachineController |
| PUT | `/admin/reservations-machine/{id}` | ReservationMachineController |
| DELETE | `/admin/reservations-machine/{id}` | ReservationMachineController |
| GET | `/admin/reservations-machine/calendrier` | CalendarController |
| GET | `/admin/reservations-machine/calendrier/events` | CalendarController |

### Factures (`/admin/factures`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/admin/factures` | FactureController |
| GET | `/admin/factures/client/{id}` | FactureController |
| POST | `/admin/factures/pages` | FactureController |
| GET | `/admin/factures/{id}` | FactureController |
| POST | `/admin/factures` | FactureController |
| PUT | `/admin/factures/{id}` | FactureController |
| DELETE | `/admin/factures/{id}` | FactureController |
| POST | `/admin/factures/filtre` | FactureController |
| GET | `/admin/factures/exportFacture/{idFacture}/{idClient}` | FactureController |

### Paiements (`/admin/paiements`)
| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/admin/paiements` | PaiementController |
| GET | `/admin/paiements/{id}` | PaiementController |
| GET | `/admin/paiements/reste/{id}` | PaiementController |
| POST | `/admin/paiements/restePayee` | PaiementController |
| POST | `/admin/paiements` | PaiementController |
| PUT | `/admin/paiements/{id}` | PaiementController |
| DELETE | `/admin/paiements/{id}` | PaiementController |
| GET | `/admin/paiements/facture/{id}` | PaiementController |

### Fichiers
| Méthode | Route | Contrôleur | Accès |
|---------|-------|-----------|-------|
| GET | `/admin/files` | FileUploadController | RESPONSABLE |
| GET | `/api/files/{filename}` | FileUploadController | public |
| POST | `/api/files` | FileUploadController | public |

---

## 5. Import Excel (`/api/exel`)

| Méthode | Route | Contrôleur |
|---------|-------|-----------|
| GET | `/api/exel/template/{tableName}` | ExelImportController |
| POST | `/api/exel/import` | ExelImportController |

---

## 6. API REST CRUD (`/api/**`) — publiques

Chacun de ces contrôleurs expose le même schéma CRUD :
`GET /` (liste) · `GET /{id}` · `POST /` (créer) · `PUT /{id}` (modifier) · `DELETE /{id}` (supprimer).
*(Sauf mention contraire ci-dessous.)*

| Base | Contrôleur | Particularités |
|------|-----------|----------------|
| `/api/utilisateurs` | UtilisateurController | CRUD complet |
| `/api/profils-utilisateur` | ProfilUtilisateurController | CRUD complet |
| `/api/roles-utilisateur` | RoleUtilisateurController | CRUD complet |
| `/api/statuts-compte` | StatutCompteController | CRUD complet |
| `/api/produits` | ProduitController | CRUD complet |
| `/api/categories-produit` | CategorieProduitController | CRUD complet |
| `/api/mouvements-stock` | MouvementStockController | CRUD complet |
| `/api/types-mouvement-stock` | TypeMouvementStockController | CRUD complet |
| `/api/commandes` | CommandeController | pas de `GET /{id}` |
| `/api/lignes-commande` | LigneCommandeController | CRUD complet |
| `/api/statuts-commande` | StatutCommandeController | CRUD complet |
| `/api/etats-machine` | EtatMachineController | pas de `GET /{id}` |
| `/api/retours-machine` | RetourMachineController | CRUD complet |
| `/api/statuts-maintenance` | StatutMaintenanceController | CRUD complet |
| `/api/statuts-reservation` | StatutReservationController | CRUD complet |
| `/api/cultures` | CultureController | CRUD complet |
| `/api/fiches-culture` | FicheCultureController | CRUD complet |
| `/api/statuts-facture` | StatutFactureController | CRUD complet |
| `/api/notes-client` | NoteClientController | CRUD complet |
| `/api/taches-employe` | TacheEmployeController | CRUD complet |
| `/api/statuts-tache` | StatutTacheController | CRUD complet |
| `/api/prets-bancaires` | PretBancaireController | CRUD complet |
| `/api/remboursements-pret` | RemboursementPretController | CRUD complet |
| `/api/statuts-pret` | StatutPretController | CRUD complet |
| `/api/rapports` | RapportController | CRUD complet |
| `/` (POST) | PageController | `POST /` — création d'un enregistrement `Pages` (pas de base path) |
