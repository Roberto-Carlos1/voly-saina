# Résumé du projet Voly Saina - Ayman Part

## Objectif du projet
Implémentation de deux nouvelles pages pour une application Spring Boot : "Mes factures" (My Invoices) et "Mes statistiques" (My Statistics) pour les clients de l'application Voly Saina.

## Technologies utilisées
- **Framework** : Spring Boot 4.0.6
- **Langage** : Java 21
- **Base de données** : PostgreSQL
- **Template Engine** : Thymeleaf
- **Génération PDF** : OpenPDF
- **Visualisation** : Chart.js
- **Architecture** : Architecture en couches (Entity, DTO, Repository, Service, Controller)

## Fonctionnalités implémentées

### 1. Page "Mes Factures" (/client/factures)

#### Fichiers créés/modifiés :
- **DTO** : `FactureClientDTO.java` - Contient toutes les informations d'une facture client
- **Repository** : `FactureRepository.java` - Méthodes de requête pour les factures client
- **Service** : `ClientFactureService.java` - Logique métier pour les factures
- **Controller** : `ClientFactureController.java` - Endpoints REST pour les factures
- **Templates** : 
  - `client/factures/list.html` - Liste des factures avec filtres
  - `client/factures/detail.html` - Détail d'une facture avec barre de progression

#### Fonctionnalités :
- Liste des factures avec filtre par statut
- Affichage détaillé d'une facture
- Export PDF des factures
- Calcul automatique du pourcentage de paiement
- Indicateur de retard de paiement
- Interface responsive (mobile-friendly)

### 2. Page "Mes Statistiques" (/client/statistiques)

#### Fichiers créés/modifiés :
- **DTOs** :
  - `StatistiquesClientDTO.java` - Statistiques globales du client
  - `TopMachineDTO.java` - Machines les plus utilisées
  - `TopProduitDTO.java` - Produits les plus commandés
  - `DepenseMensuelleDTO.java` - Dépenses mensuelles
- **Repositories** :
  - `ReservationMachineRepository.java` - Requêtes pour les réservations
  - `CommandeRepository.java` - Requêtes pour les commandes
  - `LigneCommandeRepository.java` - Requêtes pour les lignes de commande
- **Service** : `ClientStatistiqueService.java` - Calcul des statistiques
- **Controller** : `ClientStatistiqueController.java` - Endpoint REST
- **Template** : `client/statistiques/index.html` - Page avec graphiques Chart.js

#### Fonctionnalités :
- Visualisation des dépenses mensuelles
- Historique des locations et commandes
- Top des machines les plus utilisées
- Top des produits les plus commandés
- Filtrage par période
- Graphiques interactifs avec Chart.js

### 3. Page d'accueil client (/)

#### Fichiers créés :
- **Controller** : `IndexController.java` - Gestion de la racine
- **Template** : `index.html` - Page d'accueil avec liens vers les nouvelles pages
- **Template** : `client/accueil.html` - Page d'accueil client

### 4. Migration de base de données

#### Fichier créé :
- `database/VolySaina24-06-2026-22:15.sql` - Script de migration

#### Modifications :
- Ajout de la colonne `id_operation` à la table `facture`
- Insertion de données de test pour :
  - 8 factures (statuts variés : payée, en attente, partiellement payée, en retard)
  - 4 réservations de machines
  - 4 commandes
  - 8 lignes de commande

### 5. Gestion des erreurs

#### Fichier créé :
- `ResourceNotFoundException.java` - Exception personnalisée pour les ressources non trouvées

## Problèmes rencontrés et solutions

### Problème 1 : Violation de contraintes de clés étrangères
**Description** : Erreur lors de l'exécution du script SQL à cause de références à des clients et produits inexistants.

**Solution** : Modification du script pour n'utiliser que les entités existantes (id_client=1, id_produit=1,2,3).

### Problème 2 : HTTP 500 sur la page de détail des factures
**Description** : Erreur `HttpMessageNotWritableException` lors de l'affichage des détails d'une facture.

**Cause** : Expressions Thymeleaf complexes avec des opérations BigDecimal dans le template causant des erreurs de rendu.

**Solution** :
1. Ajout du champ `pourcentagePaye` dans `FactureClientDTO`
2. Calcul du pourcentage dans le service Java (`ClientFactureService.mapToDTO`)
3. Utilisation du pourcentage pré-calculé dans le template Thymeleaf
4. Gestion des valeurs null dans le service pour éviter les NullPointerException

### Problème 3 : Interface non responsive
**Description** : La page de détail des factures n'était pas adaptée aux mobiles.

**Solution** : Ajout de media queries CSS pour adapter l'affichage sur les petits écrans (max-width: 600px).

## Structure des URLs

- **Accueil** : `http://localhost:8080/`
- **Liste des factures** : `http://localhost:8080/client/factures?idClient={id}`
- **Détail facture** : `http://localhost:8080/client/factures/{idFacture}?idClient={id}`
- **Export PDF** : `http://localhost:8080/client/factures/{idFacture}/pdf?idClient={id}`
- **Statistiques** : `http://localhost:8080/client/statistiques?idClient={id}`

## Points techniques importants

### Architecture en couches
1. **Entity** : Représentation des tables de la base de données
2. **DTO** : Objets de transfert de données pour l'API
3. **Repository** : Interface d'accès aux données (JPA)
4. **Service** : Logique métier et calculs
5. **Controller** : Gestion des requêtes HTTP et réponses

### Bonnes pratiques appliquées
- Utilisation de Lombok pour réduire le code boilerplate
- Gestion des exceptions personnalisées
- Séparation des responsabilités
- Injection de dépendances avec Spring
- Templates Thymeleaf avec expressions sécurisées
- Calculs côté serveur pour éviter les erreurs de rendu

### Sécurité
- TODO : Remplacement des paramètres `idClient` par l'utilisateur connecté via Spring Security

## Conclusion

L'implémentation des pages "Mes Factures" et "Mes Statistiques" est terminée et fonctionnelle. Toutes les fonctionnalités demandées ont été réalisées :
- Affichage et gestion des factures
- Visualisation des statistiques avec graphiques
- Export PDF
- Interface responsive
- Données de test pour les tests

L'application est accessible sur `http://localhost:8080` avec le serveur PostgreSQL sur le port 5433.
