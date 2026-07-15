# VolySaina+ — Documentation de la Logique Métier

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Authentification & Autorisation](#2-authentification--autorisation)
3. [Gestion des Utilisateurs & Profils](#3-gestion-des-utilisateurs--profils)
4. [Catalogue de Machines](#4-catalogue-de-machines)
5. [Réservation de Machines](#5-réservation-de-machines)
6. [Panier (Commandes + Réservations)](#6-panier-commandes--réservations)
7. [Facturation](#7-facturation)
8. [Paiements](#8-paiements)
9. [Retour de Machines](#9-retour-de-machines)
10. [Maintenance des Machines](#10-maintenance-des-machines)
11. [Gestion des Produits & Stock](#11-gestion-des-produits--stock)
12. [Guide de Plantation](#12-guide-de-plantation)
13. [Statistiques Client](#13-statistiques-client)
14. [Dashboard Admin](#14-dashboard-admin)
15. [Import Excel](#15-import-excel)
16. [Transitions de Statut](#16-transitions-de-statut)
17. [Formules de Calcul](#17-formules-de-calcul)

---

## 1. Vue d'ensemble

VolySaina+ est une plateforme de location de machines agricoles et de vente de produits agricoles. Elle gère le cycle complet : catalogue, réservation, facturation, paiement, retour, et maintenance.

**Rôles utilisateur :**
| Rôle | Accès |
|------|-------|
| `client` | Catalogue, réservations, panier, factures, retours, profil |
| `employe` | Tâches (pas d'accès paiement) |
| `gestionnaire` | Accès complet admin |
| `responsable` | Accès complet admin |

---

## 2. Authentification & Autorisation

**Service :** `UtilisateurService`, `AuthController`, `AuthPageController`

### Inscription
- Champs obligatoires : nom, email, mot de passe
- Email trim + lowercase, doit être unique
- Mot de passe encodé via `PasswordEncoder`
- Nouveau compte → statut `actif`, rôle `client`

### Connexion
- Identifiant = email OU téléphone
- Vérification : compte existe, statut `actif`, mot de passe correspond
- Login via Spring Security

### Contrôle d'accès (RBAC)
```
responsable / gestionnaire → accès tout
client → accès ressources commençant par "client"
employe → accès sauf "paiement"
autre → refusé
```

---

## 3. Gestion des Utilisateurs & Profils

**Services :** `UtilisateurService`, `ProfilUtilisateurService`, `ClientProfilService`

### Profil Utilisateur
- Création lazy : si le profil n'existe pas, il est créé automatiquement
- Champs : genre, age, csp, localisation, niveau_connexion
- **Email immutable** lors de la mise à jour du profil

### Changement de mot de passe
- Vérification de l'ancien mot de passe obligatoire
- Encodage du nouveau via `passwordEncoder.matches()`

### Suppression de compte
- **Soft delete** : statutCompte → `INACTIF` (pas de suppression physique)

### Recommandations par connectivité
| Niveau | Contenu recommandé |
|--------|-------------------|
| `aucune` | Ultra-léger (texte seul) |
| `faible` | Léger (texte + images optimisées) |
| `bonne` | Complet (images HD, fiches détaillées) |

---

## 4. Catalogue de Machines

**Services :** `MachineService`, `ClientMachineService`, `TypeMachineService`

### Données de référence
| Type | Machines d'exemple |
|------|-------------------|
| Tracteur | Tracteur standard — 150 000 Ar/jour |
| Motoculteur | Motoculteur 18CV — 80 000 Ar/jour |
| Pulvérisateur | Pulvérisateur agricole — 30 000 Ar/jour |
| Remorque | Remorque agricole — 50 000 Ar/jour |

### Filtrage client
- Par type de machine
- Par disponibilité (`disponible = true`)
- Par prix maximum
- Pagination : taille fixe de 9 éléments

### Requête eager loading
- `findById` utilise une requête custom `findByIdWithRelations` pour charger les relations
- `findByPage` fait un deux-étapes : récupère les IDs, puis batch-load les relations

---

## 5. Réservation de Machines

**Services :** `ReservationMachineService`, `ClientReservationService`

### Cycle de vie d'une réservation

```
en_attente ──→ validee ──→ en_cours ──→ terminee
    │              │            │
    │              │            └──→ annulee (retour enregistré)
    │              │
    │              └──→ annulee (annulation client)
    │
    └──→ annulee (annulation client)
```

### Création d'une réservation
1. Vérifier que la machine existe et est disponible (`disponible = true`)
2. Vérifier qu'il n'y a pas de conflit de dates avec des réservations existantes
3. Calculer le prix : `prixJour × max(jours, 1)` (jour même = 1 jour)
4. Statut initial : `en_attente`

### Détection de conflits
```sql
-- Réservations qui se chevauchent :
date_debut < dateFin_demandee AND date_fin > dateDebut_demandee
```
Validation : machineId > 0, dates non nulles, dateFin >= dateDebut

### Création de facture (passage à "validée")
- La réservation passe de `en_attente` → `validee`
- Une facture est créée avec :
  - `dateLimite = now + 14 jours`
  - `montantTotal = prixTotal de la réservation`
  - `montantPaye = 0`
  - Statut facture = `en_attente` (ID=1)

### Paiement (passage à "en cours")
- Après paiement complet : `validee` → `en_cours`
- Toutes les réservations liées à la facture changent de statut

### Annulation
- **Règle** : on ne peut pas annuler une réservation `terminee` ou `annulee`
- Si une facture est liée, la facture passe à statut `annulee` (ID=5)
- `annulerTout` : annule toutes les réservations actives d'un client

### Flags DTO
| Flag | Conditions |
|------|-----------|
| `peutAnnuler` | `en_attente` ou `validee` |
| `peutRetourner` | `en_cours` ou `validee` |
| `estTerminee` | `terminee` ou `annulee` |

---

## 6. Panier (Commandes + Réservations)

**Services :** `PanierService`, `PanierCommandeService`, `PanierReservationService`, `PanierDetailsService`

### Règles du panier
- **Un seul panier actif** par client
- Nouveau panier créé automatiquement s'il n'en existe pas

### Produits (Commandes dans le panier)
- **Quantité min** : si null ou < 1, defaultValue = 1
- **Produit existant** : si le même produit est déjà dans la commande en attente, les quantités s'additionnent
- **Suppression cascade** : supprimer la dernière ligne → supprime la ligne, le PanierDetails, et la commande

### Réservations dans le panier
- Vérification de disponibilité à l'ajout ET à la validation
- `prixTotal = prixJour × max(days, 1)`
- Seules les réservations `en_attente` sont modifiables/supprimables
- Conflit exclut la réservation elle-même lors de la modification de dates

### Validation du panier (`cloturerPanier`)
1. Générer une facture proformat
2. Valider les commandes : statut `en_attente` → `preparee`
3. Recalculer les sous-totaux de chaque ligne
4. Décrémenter le stock avec création de mouvements de stock (`type = sortie`)
5. Valider les réservations : vérifier conflits, lier à la facture
6. Fermer le panier

---

## 7. Facturation

**Services :** `FactureService`, `ClientFactureService`

### Numérotage
Format : `FAC-{année}-{id_auto}-{id_padding_4}`
Exemple : `FAC-2026-5-0005`

### Génération proformat
- Statut : `en_attente` (ID=1)
- `dateLimite = now + 14 jours`
- `montantTotal = montantCommande + montantReservation`
- `montantPaye = 0`
- Type d'opération : `location`, `commande`, ou `commande-reservation`

### Statut dynamique (calculé au DTO)
| Condition | Statut affiché |
|-----------|---------------|
| `montantPaye >= montantTotal` | `payee` |
| `dateLimite < aujourd'hui` ET pas entièrement payé | `en_retard` |
| `montantPaye == 0` | `en_attente` |
| Sinon | `partiellement_payee` |

### Export PDF
- Gestion des factures avec panier (commandes + réservations)
- Gestion des factures de réservation directe (sans panier)
- En-tête/footer VolySaina+ via iText

---

## 8. Paiements

**Services :** `PaiementService`, `ModePaiementService`

### Modes de paiement
| ID | Libellé |
|----|---------|
| 1 | Espèces |
| 2 | Carte bancaire |

### Règles de validation
1. **Montant négatif** → `PaiementException(MONTANT_NEGATIF)`
2. **Date antérieure** à la facture → `PaiementException(DATE_ANTERIEUR)`
3. Après paiement : `montantPaye += montant`
4. Si `montantPaye == montantTotal` → statut facture = `payee` (ID=2)

### Paiement partiel
- Le montant payé est cumulé
- Le statut de la facture peut devenir `partiellement_payee`

---

## 9. Retour de Machines

**Services :** `RetourMachineService`, `ClientRetourService`

### Pénalités par état de retour
| État | Code | Pénalité |
|------|------|----------|
| Bon état | `bon` | 0 |
| Usure normale | `use` | 2 × prixJour |
| Endommagé | `endommage` | 5 × prixJour |
| Cassé | `casse` | 15 × prixJour |
| Perdu | `perdu` | 30 × prixJour |

### Pénalité de retard
```
joursRetard = dateRetour - dateFin_réservation
si joursRetard > 0 :
    penalite = prixJour × joursRetard × 1.5
```

### Processus de retour
1. Vérifier qu'aucun retour n'existe déjà pour cette réservation
2. Calculer la pénalité (retard + état)
3. Enregistrer le retour
4. Mettre à jour la réservation → `terminee`
5. Remettre la machine → `disponible`

### Coût total
```
montantTotal = reservation.prixTotal + penalite
```

---

## 10. Maintenance des Machines

**Service :** `MaintenanceMachineService`

### Cycle de vie
```
prevue ──→ en_cours ──→ terminee
   │                       │
   └──→ annulee            └──→ machine → disponible
```

### Création
- Statut maintenance : `prevue`
- État machine : `maintenance`
- Date retour prévue : `dateCreation + 7 jours`
- Création d'un enregistrement `StatutMachine` pour l'historique

### Validation (retour de maintenance)
- Statut maintenance : `terminee`
- État machine : `disponible`
- Définir la date de retour réelle

---

## 11. Gestion des Produits & Stock

**Services :** `ProduitService`, `CommandeClientService`, `MouvementStockService`

### Catégories de produits
| Catégorie | Description |
|-----------|-------------|
| Engrais | Engrais et intrants agricoles |
| Produit entretien | Huile, filtre et produits utiles aux machines |
| Produit agricole | Autres produits agricoles |

### Décrémentation de stock
1. **Vérification** : chaque ligne de commande doit avoir un stock suffisant
2. Si `stock < quantite` → `IllegalStateException`
3. **Décrémentation** : `produit.stock -= quantite`
4. **Sécurité** : vérifier que le stock ne devient pas négatif
5. **Traçabilité** : créer un `MouvementStock` avec type `sortie` et motif contenant l'ID commande

### Mouvements de stock
| Type | Description |
|------|-------------|
| `entree` | Entrée de stock |
| `sortie` | Sortie de stock (vente/location) |
| `correction` | Correction manuelle |

---

## 12. Guide de Plantation

**Services :** `GuidePlantationService`, `GuidePlantationExportService`, `CultureService`, `FicheCultureService`

### Fonctionnalités
- **Listing cultures** : filtrage par mot-clé, localisation, saison
- **Fiche culture** : détails complets (plantation, récolte, engrais, maladies)
- **Suggestions** : machines et produits recommandés pour chaque culture
- **Export** : PDF et Excel avec en-tête VolySaina+

### Règles
- Seules les cultures actives (`actif = true`) sont listées
- Images résolues depuis `static/img/cultures` (normalisation des noms)
- Pagination : page >= 0, taille entre 1 et 100

---

## 13. Statistiques Client

**Service :** `ClientStatistiqueService`

### Indicateurs générés
- Nombre de factures
- Dépenses en locations
- Dépenses en commandes
- Top 5 machines (par nombre de locations puis montant total)
- Top 5 produits (par quantité puis montant)
- Dépenses mensuelles

### Filtres temporels
| Période | Défaut |
|---------|--------|
| Derniers 3 mois | |
| Derniers 6 mois | |
| Derniers 12 mois | ✅ Par défaut |
| Année en cours | |

### Statuts inclus dans les stats
- Factures : `payee`, `partiellement_payee`, `en_retard`, `en_attente`
- Réservations : `validee`, `en_cours`, `terminee`
- Commandes : `validee`, `preparee`, `en_livraison`, `livree`

---

## 14. Dashboard Admin

**Controller :** `DashboardController`

### Données affichées
- Statistiques de la flotte (nombre total, disponibles, en maintenance)
- Statistiques des réservations
- Chiffre d'affaires (CA)
- Alertes de maintenance
- Informations de stock
- Nombre de clients

---

## 15. Import Excel

**Service :** `ExelImport`

### Convention de mapping
- Les en-têtes de colonnes suivent le format `{entitePrefixe}_{nomChamp}`
- Le préfixe est résolu en classe `com.voly_saina.entity.{Prefixe}`
- Champs ignorés : `@GeneratedValue`, `@CreationTimestamp`
- Types supportés : String, BigDecimal, Boolean, Integer, Long
- Entités JPA étrangères résolues via `EntityManager.find`

### Génération de template
- Génère un fichier Excel vierge avec les colonnes pré-remplies pour chaque type d'entité

---

## 16. Transitions de Statut

### Réservation Machine
```
en_attente ──→ validee         (création facture)
en_attente ──→ annulee         (annulation client)
validee    ──→ en_cours        (paiement)
validee    ──→ annulee         (annulation client)
en_cours   ──→ terminee        (retour enregistré)
en_cours   ──→ annulee         (annulation client)
```

### Facture
```
en_attente (ID=1) ──→ payee (ID=2)           (paiement complet)
en_attente (ID=1) ──→ annulee (ID=5)         (réservation annulée)
en_attente ──→ partiellement_payee            (paiement partiel)
partiellement_payee ──→ payee                 (solde complet)
```

### Commande
```
en_attente ──→ preparee     (validation panier)
```

### Maintenance
```
prevue ──→ en_cours ──→ terminee
prevue ──→ annulee
```

### Machine (état)
```
disponible ──→ maintenance    (création maintenance)
maintenance ──→ disponible    (retour maintenance)
disponible ──→ disponible     (retour de location)
```

---

## 17. Formules de Calcul

### Prix de réservation
```
prixTotal = prixJour × max(jours, 1)
jours = ChronoUnit.DAYS.between(dateDebut, dateFin)
```

### Pénalité de retard
```
joursRetard = dateRetour - dateFin_reservation
penaliteRetard = prixJour × joursRetard × 1.5
```

### Pénalité état retour
```
bon       → 0
use       → 2 × prixJour
endommage → 5 × prixJour
casse     → 15 × prixJour
perdu     → 30 × prixJour
```

### Coût total retour
```
montantTotal = reservation.prixTotal + penaliteRetard + penaliteEtat
```

### Sous-total ligne de commande
```
sousTotal = quantite × prixUnitaire
```

### Total commande
```
montantTotal = Σ(sousTotal de chaque ligne)
```

### Numéro de facture
```
numero = "FAC-" + année + "-" + id_auto + "-" + padding(id, 4)
```

---

## Annexe : Tables de la base de données

| Table | Description |
|-------|-------------|
| `role_utilisateur` | Rôles : client, gestionnaire, responsable, employe |
| `statut_compte` | État du compte : actif, inactif, bloque |
| `etat_machine` | État machine : disponible, louee, maintenance, hors_service |
| `statut_reservation` | Statuts : en_attente, validee, refusee, en_cours, terminee, annulee |
| `statut_commande` | Statuts : en_attente, validee, preparee, en_livraison, livree, annulee |
| `statut_facture` | Statuts : en_attente, payee, partiellement_payee, en_retard, annulee |
| `type_mouvement_stock` | Types : entree, sortie, correction |
| `statut_maintenance` | Statuts : prevue, en_cours, terminee, annulee |
| `statut_tache` | Statuts : a_faire, en_cours, terminee, en_retard |
| `statut_pret` | Statuts : en_cours, cloture, en_retard |
| `utilisateur` | Utilisateurs du système |
| `profil_utilisateur` | Profils démographiques |
| `culture` | Guide de plantation — cultures |
| `fiche_culture` | Fiches détaillées par culture |
| `type_machine` | Types de machines |
| `machine` | Machines agricoles |
| `statut_machine` | Historique des états de machine |
| `panier` | Paniers clients |
| `panier_details` | Lignes du panier (liens vers commandes/réservations) |
| `facture` | Factures |
| `reservation_machine` | Réservations de machines |
| `retour_machine` | Retours de machines |
| `maintenance_machine` | Maintenances |
| `categorie_produit` | Catégories de produits |
| `produit` | Produits agricoles |
| `mouvement_stock` | Mouvements de stock |
| `mode_paiement` | Modes de paiement |
| `commande` | Commandes |
| `ligne_commande` | Lignes de commande |
| `operation_machine` | Opérations machine (liées à facture) |
| `operation_produit` | Opérations produit (liées à facture) |
| `paiement` | Paiements |
| `note_client` | Notes/avis clients |
| `tache_employe` | Tâches employés |
| `pret_bancaire` | Prêts bancaires |
| `remboursement_pret` | Remboursements |
| `rapport` | Rapports générés |
| `pages` | Configuration du site |
