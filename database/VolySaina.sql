DROP SCHEMA IF EXISTS voly_saina CASCADE;
CREATE SCHEMA voly_saina;
SET search_path TO voly_saina;

-- =========================
-- 1. Types fonctionnels
-- =========================
CREATE TYPE role_utilisateur AS ENUM ('client', 'gestionnaire', 'responsable', 'employe');
CREATE TYPE statut_compte AS ENUM ('actif', 'inactif', 'bloque');
CREATE TYPE etat_machine AS ENUM ('disponible', 'louee', 'maintenance', 'hors_service');
CREATE TYPE statut_reservation AS ENUM ('en_attente', 'validee', 'refusee', 'en_cours', 'terminee', 'annulee');
CREATE TYPE statut_commande AS ENUM ('en_attente', 'validee', 'preparee', 'en_livraison', 'livree', 'annulee');
CREATE TYPE statut_facture AS ENUM ('en_attente', 'payee', 'partiellement_payee', 'en_retard', 'annulee');
CREATE TYPE type_mouvement_stock AS ENUM ('entree', 'sortie', 'correction');
CREATE TYPE statut_maintenance AS ENUM ('prevue', 'en_cours', 'terminee', 'annulee');
CREATE TYPE statut_tache AS ENUM ('a_faire', 'en_cours', 'terminee', 'en_retard');
CREATE TYPE statut_pret AS ENUM ('en_cours', 'cloture', 'en_retard');

-- =========================
-- 2. Tables utilisateurs et profils
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    telephone VARCHAR(30),
    email VARCHAR(150) UNIQUE,
    mot_de_passe TEXT NOT NULL,
    role role_utilisateur NOT NULL DEFAULT 'client',
    statut statut_compte NOT NULL DEFAULT 'actif',
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profil_utilisateur (
    id_profil SERIAL PRIMARY KEY,
    id_utilisateur INT NOT NULL UNIQUE REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    genre VARCHAR(30),
    age INT CHECK (age IS NULL OR age >= 0),
    csp VARCHAR(80),
    localisation TEXT,
    niveau_connexion VARCHAR(50),
    date_mise_a_jour TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 3. Guide de plantation
-- =========================
CREATE TABLE culture (
    id_culture SERIAL PRIMARY KEY,
    nom VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    saison_recommandee VARCHAR(120),
    localisation_recommandee TEXT,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE fiche_culture (
    id_fiche SERIAL PRIMARY KEY,
    id_culture INT NOT NULL REFERENCES culture(id_culture) ON DELETE CASCADE,
    periode_plantation TEXT,
    duree_avant_recolte TEXT,
    preparation_sol TEXT,
    quantite_semence TEXT,
    engrais_recommandes TEXT,
    arrosage TEXT,
    maladies_courantes TEXT,
    conseils_pratiques TEXT,
    valide BOOLEAN NOT NULL DEFAULT FALSE,
    date_mise_a_jour TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 4. Machines, réservations et retours
-- =========================
CREATE TABLE machine (
    id_machine SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    type_machine VARCHAR(80) NOT NULL,
    description TEXT,
    prix_jour NUMERIC(12,2) NOT NULL CHECK (prix_jour >= 0),
    etat etat_machine NOT NULL DEFAULT 'disponible',
    localisation TEXT,
    kilometrage NUMERIC(12,2) DEFAULT 0 CHECK (kilometrage >= 0),
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservation_machine (
    id_reservation SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    id_machine INT NOT NULL REFERENCES machine(id_machine),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    lieu_livraison TEXT,
    prix_total NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (prix_total >= 0),
    statut statut_reservation NOT NULL DEFAULT 'en_attente',
    motif_refus TEXT,
    remarque TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (date_fin >= date_debut)
);

CREATE TABLE maintenance_machine (
    id_maintenance SERIAL PRIMARY KEY,
    id_machine INT NOT NULL REFERENCES machine(id_machine) ON DELETE CASCADE,
    date_debut DATE NOT NULL DEFAULT CURRENT_DATE,
    date_retour_prevue DATE,
    date_retour_reelle DATE,
    cout NUMERIC(12,2) DEFAULT 0 CHECK (cout >= 0),
    travaux TEXT,
    statut statut_maintenance NOT NULL DEFAULT 'prevue'
);

CREATE TABLE retour_machine (
    id_retour SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL UNIQUE REFERENCES reservation_machine(id_reservation) ON DELETE CASCADE,
    date_retour DATE NOT NULL DEFAULT CURRENT_DATE,
    etat_retour TEXT,
    remarque TEXT,
    penalite NUMERIC(12,2) DEFAULT 0 CHECK (penalite >= 0)
);

-- =========================
-- 5. Produits, commandes et stocks
-- =========================
CREATE TABLE categorie_produit (
    id_categorie SERIAL PRIMARY KEY,
    nom VARCHAR(120) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE produit (
    id_produit SERIAL PRIMARY KEY,
    id_categorie INT REFERENCES categorie_produit(id_categorie),
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    conseil_usage TEXT,
    prix_unitaire NUMERIC(12,2) NOT NULL CHECK (prix_unitaire >= 0),
    stock NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (stock >= 0),
    seuil_stock NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (seuil_stock >= 0),
    date_expiration DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mouvement_stock (
    id_mouvement SERIAL PRIMARY KEY,
    id_produit INT NOT NULL REFERENCES produit(id_produit) ON DELETE CASCADE,
    type_mouvement type_mouvement_stock NOT NULL,
    quantite NUMERIC(12,2) NOT NULL CHECK (quantite > 0),
    motif TEXT,
    date_mouvement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE commande (
    id_commande SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_commande TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresse_livraison TEXT,
    mode_paiement VARCHAR(80),
    statut statut_commande NOT NULL DEFAULT 'en_attente',
    montant_total NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (montant_total >= 0)
);

CREATE TABLE ligne_commande (
    id_ligne SERIAL PRIMARY KEY,
    id_commande INT NOT NULL REFERENCES commande(id_commande) ON DELETE CASCADE,
    id_produit INT NOT NULL REFERENCES produit(id_produit),
    quantite NUMERIC(12,2) NOT NULL CHECK (quantite > 0),
    prix_unitaire NUMERIC(12,2) NOT NULL CHECK (prix_unitaire >= 0),
    sous_total NUMERIC(12,2) NOT NULL CHECK (sous_total >= 0)
);

-- =========================
-- 6. Factures et paiements
-- =========================
CREATE TABLE facture (
    id_facture SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    type_operation VARCHAR(30) NOT NULL CHECK (type_operation IN ('location', 'commande')),
    id_operation INT NOT NULL,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_facture TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    montant_total NUMERIC(12,2) NOT NULL CHECK (montant_total >= 0),
    montant_paye NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (montant_paye >= 0),
    statut statut_facture NOT NULL DEFAULT 'en_attente',
    date_limite DATE
);

CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    montant NUMERIC(12,2) NOT NULL CHECK (montant > 0),
    date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(120),
    mode_paiement VARCHAR(80)
);

-- =========================
-- 7. Clients, employés, tâches, prêts et rapports
-- =========================
CREATE TABLE note_client (
    id_note SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    note TEXT NOT NULL,
    date_note TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tache_employe (
    id_tache SERIAL PRIMARY KEY,
    id_employe INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    description TEXT NOT NULL,
    date_limite DATE,
    statut statut_tache NOT NULL DEFAULT 'a_faire',
    id_reservation INT REFERENCES reservation_machine(id_reservation),
    id_commande INT REFERENCES commande(id_commande),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pret_bancaire (
    id_pret SERIAL PRIMARY KEY,
    banque VARCHAR(150) NOT NULL,
    montant NUMERIC(12,2) NOT NULL CHECK (montant > 0),
    duree_mois INT NOT NULL CHECK (duree_mois > 0),
    taux_interet NUMERIC(6,2) DEFAULT 0 CHECK (taux_interet >= 0),
    date_debut DATE NOT NULL,
    statut statut_pret NOT NULL DEFAULT 'en_cours'
);

CREATE TABLE remboursement_pret (
    id_remboursement SERIAL PRIMARY KEY,
    id_pret INT NOT NULL REFERENCES pret_bancaire(id_pret) ON DELETE CASCADE,
    montant NUMERIC(12,2) NOT NULL CHECK (montant > 0),
    date_remboursement DATE NOT NULL DEFAULT CURRENT_DATE,
    reference_bancaire VARCHAR(120),
    justificatif TEXT
);

CREATE TABLE rapport (
    id_rapport SERIAL PRIMARY KEY,
    type_rapport VARCHAR(80) NOT NULL,
    periode_debut DATE,
    periode_fin DATE,
    contenu JSONB,
    date_generation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 8. Index utiles
-- =========================
CREATE INDEX idx_reservation_machine_dates ON reservation_machine(id_machine, date_debut, date_fin);
CREATE INDEX idx_reservation_client ON reservation_machine(id_client);
CREATE INDEX idx_commande_client ON commande(id_client);
CREATE INDEX idx_ligne_commande_produit ON ligne_commande(id_produit);
CREATE INDEX idx_facture_client ON facture(id_client);
CREATE INDEX idx_produit_stock ON produit(stock, seuil_stock);
CREATE INDEX idx_machine_etat ON machine(etat, disponible);

-- =========================
-- 11. Données de départ simples
-- =========================
INSERT INTO categorie_produit(nom, description) VALUES
('Engrais', 'Engrais et intrants agricoles'),
('Produit entretien', 'Huile, filtre et produits utiles aux machines'),
('Produit agricole', 'Autres produits agricoles')
ON CONFLICT DO NOTHING;

INSERT INTO machine(nom, type_machine, description, prix_jour, etat, localisation, disponible) VALUES
('Tracteur standard', 'tracteur', 'Préparation des sols et travaux lourds', 150000, 'disponible', 'Zone pilote', TRUE),
('Motoculteur 18CV', 'motoculteur', 'Préparation des petites et moyennes surfaces', 80000, 'disponible', 'Zone pilote', TRUE),
('Pulvérisateur agricole', 'pulverisateur', 'Traitement des cultures', 30000, 'disponible', 'Zone pilote', TRUE),
('Remorque agricole', 'remorque', 'Transport des récoltes et intrants', 50000, 'disponible', 'Zone pilote', TRUE);

INSERT INTO produit(id_categorie, nom, description, conseil_usage, prix_unitaire, stock, seuil_stock) VALUES
(1, 'NPK', 'Engrais composé pour améliorer la croissance', 'Respecter la dose recommandée selon la culture.', 25000, 100, 10),
(1, 'Urée', 'Engrais azoté', 'Utiliser avec prudence et éviter le surdosage.', 22000, 80, 10),
(1, 'Compost', 'Fertilisant organique', 'Adapter la quantité à la surface cultivée.', 12000, 150, 20);
