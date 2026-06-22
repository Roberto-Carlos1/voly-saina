### Où se trouvent les fonctions

Chaque groupe de fonctionnalités correspond à un ensemble de fichiers dans les couches `controller`, `service` et `repository`.

#### 🔐 Authentification — `auth/`
| Fonction | Description |
|---|---|
| `creerCompte` | Inscription d'un nouvel utilisateur avec son rôle |
| `connecterUtilisateur` | Connexion et retour du profil utilisateur |

#### 👤 Profil utilisateur — `profil/`
| Fonction | Description |
|---|---|
| `enregistrerProfil` | Enregistre les informations du profil (âge, genre, localisation…) |
| `determinerProfil` | Détermine le type de recommandations à afficher selon le profil |

#### 🌾 Guide de plantation — `culture/`
| Fonction | Description |
|---|---|
| `afficherCultures` | Liste les cultures adaptées à une localisation et une saison |
| `consulterFicheCulture` | Retourne la fiche complète d'une culture |
| `afficherFicheCulture` | Affiche les conseils adaptés (sol, localisation) |
| `suggererOutils` | Suggère les machines à louer selon la culture |
| `suggererProduits` | Suggère les engrais et produits selon la culture |

#### 🚜 Machines et réservations — `machine/` et `reservation/`
| Fonction | Description |
|---|---|
| `listerMachines` | Liste les machines disponibles avec filtres |
| `voirDetailMachine` | Retourne la fiche d'une machine |
| `verifierDisponibilite` | Vérifie si une machine est libre sur une période donnée |
| `calculerPrixLocation` | Calcule le montant selon la machine et les dates |
| `reserverMachine` | Crée une demande de réservation |
| `consulterMesLocations` | Retourne les locations d'un client |
| `validerReservation` | Le gestionnaire approuve ou refuse une réservation |
| `enregistrerRetour` | Enregistre le retour d'une machine après location |
| `enregistrerMaintenance` | Planifie ou enregistre une maintenance |

#### 🧴 Produits et commandes — `produit/` et `commande/`
| Fonction | Description |
|---|---|
| `listerProduits` | Liste les engrais et produits par catégorie |
| `verifierStock` | Vérifie si la quantité demandée est disponible |
| `ajouterAuPanier` | Ajoute un produit au panier du client |
| `calculerTotalPanier` | Calcule le montant total du panier |
| `validerCommande` | Confirme la commande et déclenche la mise à jour du stock |
| `ajouterProduit` | Ajoute un nouveau produit au catalogue (gestionnaire) |
| `mettreAJourStock` | Enregistre une entrée ou sortie de stock |
| `listerAlertesStock` | Retourne les produits dont le stock est sous le seuil |

#### 🧾 Facturation et paiements — `facture/` et `paiement/`
| Fonction | Description |
|---|---|
| `genererFacture` | Génère une facture après une location ou commande validée |
| `enregistrerPaiement` | Enregistre un paiement sur une facture |
| `listerFacturesImpayees` | Liste les factures non réglées |
| `listerFactures` | Retourne les factures d'un client |
| `telechargerFacture` | Exporte une facture en PDF |

#### 👥 Clients et employés — `client/` et `employe/`
| Fonction | Description |
|---|---|
| `rechercherClient` | Recherche un client par mot-clé |
| `consulterHistoriqueClient` | Retourne les commandes, locations et factures d'un client |
| `activerClient` | Active ou désactive un compte client |
| `ajouterEmploye` | Crée un compte employé avec son rôle |
| `assignerTache` | Assigne une tâche à un employé avec date limite |
| `suivreTaches` | Liste les tâches selon leur statut |

#### 🏦 Prêts bancaires — `pret/`
| Fonction | Description |
|---|---|
| `enregistrerPret` | Enregistre un prêt bancaire |
| `enregistrerRemboursement` | Enregistre un remboursement avec justificatif |
| `afficherEcheances` | Affiche le calendrier de remboursement d'un prêt |

#### 📊 Rapports — `rapport/`
| Fonction | Description |
|---|---|
| `afficherDashboard` | Résumé de l'activité pour le tableau de bord gestionnaire |
| `genererRapportVentes` | Rapport des ventes sur une période |
| `genererRapportLocations` | Rapport des locations sur une période |
| `exporterRapport` | Exporte un rapport en PDF ou Excel |
| `calculerChiffreAffaires` | Calcule le CA total sur une période |

---
