# Récapitulatif des modifications et Architecture des Routes

Ce document résume l'état actuel de toutes les routes backend du projet `voly-saina`, après le refactoring des URLs.

---

## 1. Pages Web (Vues Thymeleaf)

### Accès public / Auth
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/` | `IndexController` | `index` |
| GET | `/route` | `IndexController` | `index_true` |
| GET | `/connexion`, `/login` | `AuthPageController` | `auth/page01` |
| POST | `/inscription` | `AuthPageController` | redirect → `/connexion` |
| GET | `/signup` | `AuthController` | `auth/signup` |
| POST | `/signup` | `AuthController` | redirect → `/connexion` |
| GET | `/access-denied` | `AuthController` | `auth/access-denied` |
| GET | `/profil` | `ProfilPageController` | `profil/page02` |
| POST | `/profil/enregistrer` | `ProfilPageController` | redirect → `/profil` |

### Backoffice — Machines
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/machines/` | `MachineController` | `index` |
| GET | `/machines` | `MachineController` | `machines/list` |
| POST | `/machines/pages` | `MachineController` | redirect |
| GET | `/machines/ajouter` | `MachineController` | `machines/insert-machine` |
| POST | `/machines/ajouter` | `MachineController` | redirect |
| GET | `/machines/supprimer/{id}` | `MachineController` | redirect |
| GET | `/machines/modifier/{id}` | `MachineController` | `machines/modify-machine` |
| POST | `/machines/modifier/{id}` | `MachineController` | redirect |
| GET | `/machines/{id}` | `MachineController` | `machines/detail-machine` |
| POST | `/machines/api/filtre` | `MachineController` | JSON |

### Backoffice — Maintenances
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/maintenances-machine` | `MaintenanceMachineController` | `/machines/maintenance/list` |
| POST | `/maintenances-machine/pages` | `MaintenanceMachineController` | redirect |
| GET | `/maintenances-machine/ajouter` | `MaintenanceMachineController` | `/machines/maintenance/form` |
| POST | `/maintenances-machine/ajouter` | `MaintenanceMachineController` | redirect |
| GET | `/maintenances-machine/modifier/{id}` | `MaintenanceMachineController` | `/machines/maintenance/form` |
| POST | `/maintenances-machine/modifier/{id}` | `MaintenanceMachineController` | redirect |
| GET | `/maintenances-machine/supprimer/{id}` | `MaintenanceMachineController` | redirect |
| GET | `/maintenances-machine/valider/{id}` | `MaintenanceMachineController` | `/machines/maintenance/validation` |
| POST | `/maintenances-machine/valider` | `MaintenanceMachineController` | redirect |
| POST | `/maintenances-machine/api/filtre` | `MaintenanceMachineController` | JSON |

### Backoffice — Réservations (admin)
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/reservations-machine` | `ReservationMachineController` | `reservation/list` |
| GET | `/reservations-machine/calendrier` | `CalendarController` | `reservation/calendrier` |

### Backoffice — Factures & Paiements
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/factures` | `FactureController` | `facturation/list` |
| GET | `/factures/client/{id}` | `FactureController` | `facturation/list` |
| POST | `/factures/pages` | `FactureController` | redirect |
| GET | `/factures/{id}` | `FactureController` | `facturation/detail-facture` |
| POST | `/factures/filtre` | `FactureController` | JSON |
| GET | `/paiements` | `PaiementController` | `paiements/list` |
| GET | `/paiements/reste/{id}` | `PaiementController` | `paiements/form-reste` |
| POST | `/paiements/restePayee` | `PaiementController` | redirect |
| GET | `/paiements/facture/{id}` | `PaiementController` | `paiements/historique` |

### Backoffice — Import fichiers
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/files` | `FileUploadController` | `import/form/index` |

### Front-office — Espace client
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/client/accueil` | `ClientController` | `index` |
| GET | `/client/profil` | `ClientProfilController` | `client/profil/index` |
| GET | `/client/profil/informations` | `ClientProfilController` | `client/profil/informations` |
| POST | `/client/profil/informations` | `ClientProfilController` | redirect |
| GET | `/client/profil/parametres` | `ClientProfilController` | `client/profil/parametres` |
| POST | `/client/profil/mot-de-passe` | `ClientProfilController` | redirect |
| GET | `/client/statistiques` | `ClientStatistiqueController` | `client/statistiques/index` |

### Front-office — Factures client
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/client/factures` | `ClientFactureController` | `client/factures/list` |
| GET | `/client/factures/{idFacture}` | `ClientFactureController` | `client/factures/detail` |
| GET | `/client/factures/paiement` | `ClientFactureController` | `client/factures/paiement` |
| POST | `/client/factures/achat` | `ClientFactureController` | redirect |
| GET | `/client/factures/{idFacture}/pdf` | `ClientFactureController` | PDF download |

### Front-office — Catalogue machines
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/catalogue/machines/types` | `ClientMachineController` | `client/machines/types` |
| GET | `/catalogue/machines/catalogue` | `ClientMachineController` | `client/machines/catalogue` |
| GET | `/catalogue/machines/detail` | `ClientMachineController` | `client/machines/detail` |

### Front-office — Réservations
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/catalogue/reservations/{machineId}/nouvelle` | `ClientReservationController` | `client/reservations/form` |
| GET | `/catalogue/reservations/{id}/annuler` | `ClientReservationController` | `client/reservations/annuler-form` |
| GET | `/catalogue/reservations/mes-reservations` | `ClientReservationController` | `client/reservations/list` |
| GET | `/catalogue/reservations/{id}` | `ClientReservationController` | `client/reservations/detail` |
| GET | `/catalogue/reservations/facture/{factureId}` | `ClientReservationController` | `client/reservations/facture-detail` |

### Front-office — Retours
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/catalogue/retours/{reservationId}/nouveau` | `ClientRetourController` | `client/retours/form` |

### Front-office — Ventes / Produits
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/catalogue/produits/catalogue` | `VenteController` | `client/ventes/catalogue` |
| GET | `/catalogue/produits/disponibles` | `VenteController` | `client/ventes/disponibles` |
| GET | `/catalogue/produits/{id}` | `VenteController` | `client/ventes/detail` |

### Front-office — Panier
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/panier` | `PanierController` | `client/panier/commandePanier` |
| GET | `/panier/ajouter` | `PanierController` | redirect |
| GET | `/panier/details` | `PanierController` | `client/panier/commandePanier` |
| POST | `/panier/supprimer` | `PanierController` | redirect |
| POST | `/panier/quantite` | `PanierController` | redirect |
| POST | `/panier/valider` | `PanierController` | redirect |
| GET | `/panier/recap` | `PanierController` | `client/recu/recap-commande` |
| POST | `/panier/reservations/ajouter` | `PanierReservationController` | redirect |
| POST | `/panier/reservations/supprimer` | `PanierReservationController` | redirect |
| POST | `/panier/reservations/modifier-dates` | `PanierReservationController` | redirect |
| POST | `/panier/reservations/valider-tout` | `PanierReservationController` | redirect |
| POST | `/panier/reservations/vider` | `PanierReservationController` | redirect |

### Guide Plantation
| Méthode | URL | Controller | Vue |
|---------|-----|-----------|-----|
| GET | `/guide-plantation` | `GuidePlantationController` | `guide-plantation/cultures` |
| GET | `/guide-plantation/` | `GuidePlantationController` | `guide-plantation/cultures` |
| GET | `/guide-plantation/cultures` | `GuidePlantationController` | `guide-plantation/cultures` |
| GET | `/guide-plantation/cultures/{idCulture}` | `GuidePlantationController` | `guide-plantation/detail-culture` |
| GET | `/guide-plantation/cultures/{idCulture}/fiche` | `GuidePlantationController` | `guide-plantation/fiche-culture` |
| GET | `/guide-plantation/cultures/export/pdf` | `GuidePlantationController` | PDF download |
| GET | `/guide-plantation/cultures/export/excel` | `GuidePlantationController` | XLSX download |

---

## 2. APIs JSON (REST / @ResponseBody)

### 2.1 APIs REST pures (CRUD standards, prefixe `/api/`)
| Méthode | URL | Controller |
|---------|-----|-----------|
| GET/POST/PUT/DELETE | `/api/profils-utilisateur[/{id}]` | `ProfilUtilisateurController` |
| GET/POST/PUT/DELETE | `/api/utilisateurs[/{id}]` | `UtilisateurController` |
| GET/POST/PUT/DELETE | `/api/roles-utilisateur[/{id}]` | `RoleUtilisateurController` |
| GET/POST/PUT/DELETE | `/api/statuts-compte[/{id}]` | `StatutCompteController` |
| GET/POST/PUT/DELETE | `/api/statuts-commande[/{id}]` | `StatutCommandeController` |
| GET/POST/PUT/DELETE | `/api/statuts-facture[/{id}]` | `StatutFactureController` |
| GET/POST/PUT/DELETE | `/api/statuts-reservation[/{id}]` | `StatutReservationController` |
| GET/POST/PUT/DELETE | `/api/statuts-tache[/{id}]` | `StatutTacheController` |
| GET/POST/PUT/DELETE | `/api/statuts-maintenance[/{id}]` | `StatutMaintenanceController` |
| GET/POST/PUT/DELETE | `/api/statuts-pret[/{id}]` | `StatutPretController` |
| GET/POST/PUT/DELETE | `/api/cultures[/{id}]` | `CultureController` |
| GET/POST/PUT/DELETE | `/api/fiches-culture[/{id}]` | `FicheCultureController` |
| GET/POST/PUT/DELETE | `/api/produits[/{id}]` | `ProduitController` |
| GET/POST/PUT/DELETE | `/api/categories-produit[/{id}]` | `CategorieProduitController` |
| GET/POST/PUT/DELETE | `/api/mouvements-stock[/{id}]` | `MouvementStockController` |
| GET/POST/PUT/DELETE | `/api/types-mouvement-stock[/{id}]` | `TypeMouvementStockController` |
| GET/POST/PUT/DELETE | `/api/etats-machine[/{id}]` | `EtatMachineController` |
| GET/POST/PUT/DELETE | `/api/retours-machine[/{id}]` | `RetourMachineController` |
| GET/POST/PUT/DELETE | `/api/commandes[/{id}]` | `CommandeController` |
| GET/POST/PUT/DELETE | `/api/lignes-commande[/{id}]` | `LigneCommandeController` |
| GET/POST/PUT/DELETE | `/api/prets-bancaires[/{id}]` | `PretBancaireController` |
| GET/POST/PUT/DELETE | `/api/remboursements-pret[/{id}]` | `RemboursementPretController` |
| GET/POST/PUT/DELETE | `/api/rapports[/{id}]` | `RapportController` |
| GET/POST/PUT/DELETE | `/api/taches-employe[/{id}]` | `TacheEmployeController` |
| GET/POST/PUT/DELETE | `/api/notes-client[/{id}]` | `NoteClientController` |

### 2.2 APIs utilitaires (fichiers, imports)
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| GET | `/api/files/{filename}` | `FileUploadController` | Fichier |
| POST | `/api/files` | `FileUploadController` | redirect |
| GET | `/api/exel/template/{tableName}` | `ExelImportController` | XLSX |
| POST | `/api/exel/import` | `ExelImportController` | Texte |
| POST | `/api/pages` | `PageController` | JSON |

### 2.3 APIs Backoffice (AJAX filtres)
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| POST | `/machines/api/filtre` | `MachineController` | JSON (Page) |
| POST | `/maintenances-machine/api/filtre` | `MaintenanceMachineController` | JSON (Page) |
| POST | `/factures/filtre` | `FactureController` | JSON (Page) |
| GET | `/reservations-machine/calendrier/events` | `CalendarController` | JSON (List) |
| GET | `/reservations-machine/{id}` | `ReservationMachineController` | JSON |
| POST | `/reservations-machine` | `ReservationMachineController` | JSON |
| PUT | `/reservations-machine/{id}` | `ReservationMachineController` | JSON |
| DELETE | `/reservations-machine/{id}` | `ReservationMachineController` | JSON |
| GET | `/maintenances-machine/{id}` | `MaintenanceMachineController` | JSON |
| POST | `/maintenances-machine` | `MaintenanceMachineController` | JSON |
| PUT | `/maintenances-machine/{id}` | `MaintenanceMachineController` | JSON |
| DELETE | `/maintenances-machine/{id}` | `MaintenanceMachineController` | JSON |
| GET | `/factures/{id}` | `FactureController` | Vue (ou JSON avec Accept) |
| POST | `/factures` | `FactureController` | JSON |
| PUT | `/factures/{id}` | `FactureController` | JSON |
| DELETE | `/factures/{id}` | `FactureController` | JSON |
| GET | `/paiements/{id}` | `PaiementController` | JSON |
| POST | `/paiements` | `PaiementController` | JSON |
| PUT | `/paiements/{id}` | `PaiementController` | JSON |
| DELETE | `/paiements/{id}` | `PaiementController` | JSON |

### 2.4 APIs Front-office — Catalogue machines
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| GET | `/catalogue/machines/api/types` | `ClientMachineController` | JSON (List) |
| GET | `/catalogue/machines/api/catalogue` | `ClientMachineController` | JSON (List) |
| GET | `/catalogue/machines/api/disponibles` | `ClientMachineController` | JSON (List) |
| GET | `/catalogue/machines/api/type/{typeId}` | `ClientMachineController` | JSON (List) |
| GET | `/catalogue/machines/api/{id}` | `ClientMachineController` | JSON |

### 2.5 APIs Front-office — Réservations
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| GET | `/catalogue/reservations/api/client/{clientId}` | `ClientReservationController` | JSON (List) |
| GET | `/catalogue/reservations/api/client/{clientId}/statut/{statut}` | `ClientReservationController` | JSON (List) |
| GET | `/catalogue/reservations/api/client/{clientId}/active` | `ClientReservationController` | JSON (List) |
| GET | `/catalogue/reservations/api/{id}` | `ClientReservationController` | JSON |
| POST | `/catalogue/reservations/api` | `ClientReservationController` | JSON |
| POST | `/catalogue/reservations/api/{id}/facturer` | `ClientReservationController` | JSON |
| POST | `/catalogue/reservations/api/facture/{factureId}/payer` | `ClientReservationController` | JSON |
| GET | `/catalogue/reservations/api/facture/{factureId}/reservations` | `ClientReservationController` | JSON |
| PUT | `/catalogue/reservations/api/{id}/annuler` | `ClientReservationController` | JSON |
| PUT | `/catalogue/reservations/api/client/{clientId}/annuler-tout` | `ClientReservationController` | JSON |

### 2.6 APIs Front-office — Retours
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| GET | `/catalogue/retours/api/form/{reservationId}` | `ClientRetourController` | JSON |
| GET | `/catalogue/retours/api/reservation/{reservationId}` | `ClientRetourController` | JSON |
| GET | `/catalogue/retours/api/client/{clientId}` | `ClientRetourController` | JSON (List) |
| POST | `/catalogue/retours/api` | `ClientRetourController` | JSON |
| GET | `/catalogue/retours/api/penalite/{reservationId}` | `ClientRetourController` | JSON |

### 2.7 APIs Front-office — Ventes / Produits
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| GET | `/catalogue/produits/api/catalogue` | `VenteController` | JSON (List) |
| GET | `/catalogue/produits/api/produits` | `VenteController` | JSON (List) |
| GET | `/catalogue/produits/api/produits/{id}` | `VenteController` | JSON |
| GET | `/catalogue/produits/api/produits/disponibles` | `VenteController` | JSON (List) |

### 2.8 APIs Front-office — Panier / Réservations
| Méthode | URL | Controller | Retour |
|---------|-----|-----------|--------|
| POST | `/panier/reservations/api/ajouter` | `PanierReservationController` | JSON |
| GET | `/panier/reservations/api/liste` | `PanierReservationController` | Fragment HTML |

---
