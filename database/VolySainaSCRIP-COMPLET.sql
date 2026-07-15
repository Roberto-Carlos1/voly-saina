-- Active: 1773080411410@@127.0.0.1@5432@voly_saina@voly_saina
DROP SCHEMA IF EXISTS voly_saina CASCADE;
CREATE SCHEMA voly_saina;
SET search_path TO voly_saina;

-- =========================
-- 1. Tables de référence (anciennement types ENUM)
-- =========================

CREATE TABLE role_utilisateur (
    id_role SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO role_utilisateur(code, libelle) VALUES
('client', 'Client'),
('gestionnaire', 'Gestionnaire'),
('responsable', 'Responsable'),
('employe', 'Employé');

CREATE TABLE statut_compte (
    id_statut_compte SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_compte(code, libelle) VALUES
('actif', 'Actif'),
('inactif', 'Inactif'),
('bloque', 'Bloqué');

CREATE TABLE etat_machine (
    id_etat_machine SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO etat_machine(code, libelle) VALUES
('disponible', 'Disponible'),
('louee', 'Louée'),
('maintenance', 'En maintenance'),
('hors_service', 'Hors service');

CREATE TABLE statut_reservation (
    id_statut_reservation SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_reservation(code, libelle) VALUES
('en_attente', 'En attente'),
('validee', 'Validée'),
('refusee', 'Refusée'),
('en_cours', 'En cours'),
('terminee', 'Terminée'),
('annulee', 'Annulée');

CREATE TABLE statut_commande (
    id_statut_commande SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_commande(code, libelle) VALUES
('en_attente', 'En attente'),
('validee', 'Validée'),
('preparee', 'Préparée'),
('en_livraison', 'En livraison'),
('livree', 'Livrée'),
('annulee', 'Annulée');

CREATE TABLE statut_facture (
    id_statut_facture SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_facture(code, libelle) VALUES
('en_attente', 'En attente'),
('payee', 'Payée'),
('partiellement_payee', 'Partiellement payée'),
('en_retard', 'En retard'),
('annulee', 'Annulée');

CREATE TABLE type_mouvement_stock (
    id_type_mouvement SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO type_mouvement_stock(code, libelle) VALUES
('entree', 'Entrée'),
('sortie', 'Sortie'),
('correction', 'Correction');

CREATE TABLE statut_maintenance (
    id_statut_maintenance SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_maintenance(code, libelle) VALUES
('prevue', 'Prévue'),
('en_cours', 'En cours'),
('terminee', 'Terminée'),
('annulee', 'Annulée');

CREATE TABLE statut_tache (
    id_statut_tache SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_tache(code, libelle) VALUES
('a_faire', 'À faire'),
('en_cours', 'En cours'),
('terminee', 'Terminée'),
('en_retard', 'En retard');

CREATE TABLE statut_pret (
    id_statut_pret SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(80)
);
INSERT INTO statut_pret(code, libelle) VALUES
('en_cours', 'En cours'),
('cloture', 'Clôturé'),
('en_retard', 'En retard');

-- =========================
-- 2. Tables utilisateurs et profils
-- =========================

CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    telephone VARCHAR(30),
    email VARCHAR(150) UNIQUE,
    mot_de_passe TEXT NOT NULL,
    id_role INT NOT NULL REFERENCES role_utilisateur(id_role),
    id_statut_compte INT NOT NULL REFERENCES statut_compte(id_statut_compte),
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
create table type_machine(
    id_type_machine SERIAL PRIMARY KEY,
    libelle VARCHAR(80)
);

CREATE TABLE machine (
    id_machine SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    id_type_machine INT NOT NULL REFERENCES type_machine(id_type_machine),
    description TEXT,
    prix_jour NUMERIC(12,2) NOT NULL,
    localisation TEXT,
    kilometrage NUMERIC(12,2) DEFAULT 0,
    disponible BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statut_machine (
    id SERIAL PRIMARY KEY,
    id_machine INT NOT NULL REFERENCES machine(id_machine) ON DELETE CASCADE,
    id_etat_machine INT NOT NULL REFERENCES etat_machine(id_etat_machine),
    date_creation DATE DEFAULT CURRENT_DATE,
    foreign key (id_etat_machine) references etat_machine(id_etat_machine) on delete cascade
);

create table panier(
    id_panier SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    actif boolean,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE facture (
    id_facture SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    type_operation VARCHAR(30) NOT NULL CHECK (type_operation IN ('location', 'commande', 'commande-reservation')),
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_facture TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    montant_total NUMERIC(12,2) NOT NULL CHECK (montant_total >= 0),
    montant_paye NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (montant_paye >= 0),
    id_statut_facture INT NOT NULL REFERENCES statut_facture(id_statut_facture),
    id_panier INT REFERENCES panier(id_panier),
    date_limite DATE
);

CREATE TABLE reservation_machine (
    id_reservation SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    id_machine INT NOT NULL REFERENCES machine(id_machine),
    id_facture INT REFERENCES facture(id_facture) ON DELETE SET NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    lieu_livraison TEXT,
    prix_total NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (prix_total >= 0),
    id_statut_reservation INT NOT NULL REFERENCES statut_reservation(id_statut_reservation),
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
    id_statut_maintenance INT NOT NULL REFERENCES statut_maintenance(id_statut_maintenance)
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
    id_type_mouvement INT NOT NULL REFERENCES type_mouvement_stock(id_type_mouvement),
    quantite NUMERIC(12,2) NOT NULL CHECK (quantite > 0),
    motif TEXT,
    date_mouvement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mode_paiement(
    id_mode_paiement SERIAL PRIMARY KEY,
    libelle VARCHAR(80)
);

CREATE TABLE commande (
    id_commande SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur),
    date_commande TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    adresse_livraison TEXT,
    id_mode_paiement INT REFERENCES mode_paiement(id_mode_paiement),
    id_statut_commande INT NOT NULL REFERENCES statut_commande(id_statut_commande),
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

create table panier_details(
    id_panier_details SERIAL PRIMARY KEY,
    id_panier  INT REFERENCES panier(id_panier) ON DELETE CASCADE,
    id_commande INT REFERENCES commande(id_commande) ON DELETE CASCADE,
    id_reservation_machine INT REFERENCES reservation_machine(id_reservation) ON DELETE CASCADE
);

-- =========================
-- 6. Factures et paiements
-- =========================

CREATE TABLE operation_machine(
    id_operation SERIAL PRIMARY KEY,
    id_machine INT REFERENCES machine(id_machine) ON DELETE CASCADE,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    quantite INT NOT NULL
);

CREATE TABLE operation_produit(
    id_operation SERIAL PRIMARY KEY,
    id_produit INT REFERENCES produit(id_produit) ON DELETE CASCADE,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    quantite INT NOT NULL
);

CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    montant NUMERIC(12,2) NOT NULL CHECK (montant > 0),
    date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(120),
    id_mode_paiement INT NOT NULL REFERENCES mode_paiement(id_mode_paiement)
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
    id_statut_tache INT NOT NULL REFERENCES statut_tache(id_statut_tache),
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
    id_statut_pret INT NOT NULL REFERENCES statut_pret(id_statut_pret)
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

CREATE TABLE pages(
    id_page SERIAL PRIMARY KEY,
    nombre int
);

INSERT INTO pages(nombre) values (5);

-- =========================
-- 8. Données de départ simples
-- =========================
INSERT INTO categorie_produit(nom, description) VALUES
('Engrais', 'Engrais et intrants agricoles'),
('Produit entretien', 'Huile, filtre et produits utiles aux machines'),
('Produit agricole', 'Autres produits agricoles')
ON CONFLICT DO NOTHING;

insert into type_machine(libelle) values
('tracteur'),
('motoculteur'),
('pulverisateur'),
('remorque');

INSERT INTO mode_paiement(libelle) VALUES
('Espèces'),
('Carte bancaire');

INSERT INTO machine(nom, id_type_machine, description, prix_jour, localisation, disponible,date_creation) VALUES
('Tracteur standard', 1, 'Préparation des sols et travaux lourds', 150000,  'Zone pilote', TRUE, '2026-06-21'),
('Motoculteur 18CV', 2, 'Préparation des petites et moyennes surfaces', 80000,  'Zone pilote', TRUE, '2026-06-21'),
('Pulvérisateur agricole', 3, 'Traitement des cultures', 30000,  'Zone pilote', TRUE, '2026-06-21'),
('Remorque agricole', 4, 'Transport des récoltes et intrants', 50000,  'Zone pilote', TRUE, '2026-06-21');

INSERT INTO statut_machine(id_machine, id_etat_machine,date_creation) VALUES
(1, 1, '2026-06-21'),
(2, 1, '2026-06-21'),
(3, 1, '2026-06-21'),
(4, 1, '2026-06-21');

INSERT INTO produit(id_categorie, nom, description, conseil_usage, prix_unitaire, stock, seuil_stock) VALUES
(1, 'NPK', 'Engrais composé pour améliorer la croissance', 'Respecter la dose recommandée selon la culture.', 25000, 100, 10),
(1, 'Urée', 'Engrais azoté', 'Utiliser avec prudence et éviter le surdosage.', 22000, 80, 10),
(1, 'Compost', 'Fertilisant organique', 'Adapter la quantité à la surface cultivée.', 12000, 150, 20);

INSERT INTO utilisateur(nom, mot_de_passe,email, id_role, id_statut_compte) VALUES
('admin', ' ', 'a@local', 3, 1),
('Rakoto', ' ', 'r@local', 1, 1);


CREATE INDEX idx_panier ON panier_details(id_panier);

CREATE OR REPLACE VIEW voly_saina.v_facture_fille AS
SELECT
    CONCAT('CMD-', c.id_commande) AS id_operation_key,
    f.id_facture,
    f.numero,
    'commande' AS type_operation,
    c.id_commande AS id_operation,
    c.date_commande AS date_operation,
    
    c.montant_total,
    sc.code AS statut
FROM voly_saina.facture f
JOIN voly_saina.panier pa ON f.id_panier = pa.id_panier
JOIN voly_saina.panier_details pd ON pa.id_panier = pd.id_panier
JOIN voly_saina.commande c ON pd.id_commande = c.id_commande
JOIN voly_saina.statut_commande sc ON sc.id_statut_commande = c.id_statut_commande

UNION ALL

SELECT
    CONCAT('RES-', rm.id_reservation) AS id_operation_key,
    f.id_facture,
    f.numero,
    'reservation' AS type_operation,
    rm.id_reservation AS id_operation,
    rm.date_creation AS date_operation,
    rm.prix_total AS montant_total,
    sr.code AS statut
FROM voly_saina.facture f
JOIN voly_saina.panier pa ON f.id_panier = pa.id_panier
JOIN voly_saina.panier_details pd ON pa.id_panier = pd.id_panier
JOIN voly_saina.reservation_machine rm ON pd.id_reservation_machine = rm.id_reservation
JOIN voly_saina.statut_reservation sr ON sr.id_statut_reservation = rm.id_statut_reservation;

-- ===========================
-- cluture donne
-- ===========================
-- Donnees completes cultures Madagascar - genere pour voly_saina
SET search_path TO voly_saina;

INSERT INTO culture(nom, description, saison_recommandee, localisation_recommandee, actif) VALUES
('Riz irrigue', 'Riz de bas-fonds et rizieres irriguees, base alimentaire de Madagascar.', 'Novembre a fevrier', 'Alaotra-Mangoro, Sofia, Vakinankaratra, Itasy, Analamanga', TRUE),
('Riz pluvial ameliore', 'Riz cultive sans submersion permanente, adapte aux tanety humides.', 'Novembre a janvier', 'Hautes terres: Analamanga, Vakinankaratra, Amoron i Mania, Haute Matsiatra', TRUE),
('Mais', 'Cereale vivriere importante, souvent associee au haricot ou au manioc.', 'Octobre a decembre', 'Itasy, Vakinankaratra, Bongolava, Menabe, Atsimo-Andrefana, Androy', TRUE),
('Manioc', 'Tubercule rustique tres cultive en zones seches et comme reserve alimentaire.', 'Debut saison des pluies', 'Atsimo-Andrefana, Androy, Anosy, Menabe, Boeny, Betsiboka', TRUE),
('Patate douce', 'Tubercule de cycle court, utile en soudure alimentaire.', 'Octobre a mars', 'Vakinankaratra, Itasy, Amoron i Mania, Haute Matsiatra, Atsimo-Andrefana', TRUE),
('Pomme de terre', 'Culture maraichere et vivriere des hautes terres fraiches.', 'Mars a aout selon altitude', 'Vakinankaratra, Itasy, Analamanga, Amoron i Mania, Haute Matsiatra', TRUE),
('Haricot sec', 'Legumineuse courte pour alimentation et fertilite du sol.', 'Mars a juin ou octobre a decembre', 'Analamanga, Itasy, Vakinankaratra, Bongolava, Haute Matsiatra', TRUE),
('Arachide', 'Oleagineux adapte aux sols sableux et zones chaudes.', 'Novembre a janvier', 'Menabe, Boeny, Sofia, Atsimo-Andrefana, Melaky, Bongolava', TRUE),
('Soja', 'Legumineuse proteique pour alimentation et transformation.', 'Novembre a janvier', 'Vakinankaratra, Itasy, Bongolava, Alaotra-Mangoro', TRUE),
('Oignon', 'Culture maraichere de saison fraiche et zones irrigables.', 'Avril a aout', 'Vakinankaratra, Itasy, Analamanga, Boeny, Menabe', TRUE),
('Tomate', 'Maraichage courant autour des villes et zones irriguees.', 'Avril a octobre ou toute saison sous irrigation', 'Analamanga, Itasy, Vakinankaratra, Atsinanana, Boeny', TRUE),
('Carotte', 'Legume racine des zones fraiches des hautes terres.', 'Mars a septembre', 'Vakinankaratra, Analamanga, Itasy, Amoron i Mania', TRUE),
('Laitue', 'Legume feuille de cycle court pour marche local.', 'Toute saison fraiche ou sous ombrage', 'Analamanga, Vakinankaratra, Itasy, Atsinanana', TRUE),
('Concombre', 'Cucurbitacee de cycle court pour marche frais.', 'Septembre a mars', 'Analamanga, Boeny, Atsinanana, Atsimo-Andrefana irrigue', TRUE),
('Courgette', 'Legume fruit proche du concombre, productif en maraichage.', 'Septembre a mars', 'Analamanga, Vakinankaratra, Boeny, Atsinanana', TRUE),
('Piment', 'Culture condimentaire chaude, pluviale ou irriguee.', 'Septembre a janvier', 'Atsinanana, Analanjirofo, Boeny, Atsimo-Andrefana, Analamanga', TRUE),
('Gingembre', 'Rhizome d epice pour zones humides et mi-ombrage.', 'Octobre a decembre', 'Atsinanana, Analanjirofo, SAVA, Vatovavy', TRUE),
('Vanille', 'Culture d exportation en agroforesterie, avec pollinisation manuelle.', 'Bouturage surtout novembre a mars', 'SAVA: Sambava, Antalaha, Andapa, Vohémar; Analanjirofo; Atsinanana', TRUE),
('Girofle', 'Arbre a clous de girofle important sur la cote est.', 'Novembre a mars', 'Analanjirofo, Atsinanana, SAVA, Vatovavy', TRUE),
('Cacao', 'Culture perenne de la vallee du Sambirano et zones humides chaudes.', 'Novembre a mars', 'Diana: Ambanja, vallee du Sambirano; Nosy Be; quelques zones SAVA', TRUE),
('Poivre noir', 'Liane d epice souvent associee aux systemes agroforestiers humides.', 'Novembre a mars', 'SAVA, Analanjirofo, Atsinanana, Diana', TRUE),
('Cannelle', 'Arbre aromatique de la cote est, recolte de l ecorce.', 'Novembre a mars', 'Atsinanana, Analanjirofo, Vatovavy, SAVA', TRUE),
('Litchi', 'Arbre fruitier majeur de la cote est.', 'Plantation novembre a mars', 'Atsinanana: Toamasina; Analanjirofo; Vatovavy; Fitovinany', TRUE),
('Banane', 'Fruit alimentaire et commercial, souvent en association.', 'Toute saison humide', 'SAVA, Analanjirofo, Atsinanana, Vatovavy, Boeny, Diana', TRUE),
('Ananas', 'Fruit tropical adapte aux sols legers acides.', 'Novembre a mars', 'SAVA, Analanjirofo, Atsinanana, Boeny, Diana', TRUE),
('Canne a sucre', 'Culture industrielle et artisanale, aime chaleur et eau.', 'Novembre a fevrier', 'Atsinanana, Boeny, Diana, SAVA, Atsimo-Andrefana irrigue', TRUE),
('Sesame', 'Oleagineux de zones chaudes et seches.', 'Novembre a janvier', 'Menabe, Boeny, Atsimo-Andrefana, Sofia, Melaky', TRUE),
('Tournesol', 'Oleagineux possible en zones ensoleillees et sols profonds.', 'Novembre a janvier', 'Itasy, Vakinankaratra, Bongolava, Menabe', TRUE),
('Orange', 'Agrume de verger, marche frais local.', 'Novembre a mars', 'Itasy, Analamanga, Vakinankaratra, Diana, Boeny', TRUE),
('Avocat', 'Fruitier adapte aux hautes terres et zones tropicales selon variete.', 'Novembre a mars', 'Analamanga, Itasy, Vakinankaratra, Haute Matsiatra, Diana', TRUE),
('Mangue', 'Fruitier de zones chaudes et seches a saison marquee.', 'Novembre a fevrier', 'Boeny, Diana, Sofia, Menabe, Atsimo-Andrefana', TRUE),
('Papaye', 'Fruitier de cycle rapide en zones chaudes.', 'Toute saison avec eau', 'Boeny, Diana, Atsinanana, Analanjirofo, Atsimo-Andrefana irrigue', TRUE),
('Pastèque', 'Cucurbitacee de saison chaude et zones sableuses.', 'Septembre a janvier', 'Atsimo-Andrefana, Menabe, Boeny, Sofia, Analamanga irrigue', TRUE),
('Melon', 'Fruit de saison chaude pour zones irriguees.', 'Septembre a janvier', 'Boeny, Menabe, Atsimo-Andrefana, Analamanga, Itasy', TRUE)
ON CONFLICT (nom) DO NOTHING;

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a fevrier',
    '120 a 160 jours',
    'Nettoyer la riziere, labourer, mettre en boue, niveler puis repiquer des plants jeunes.',
    '30 a 50 kg de semences/ha en pepiniere puis repiquage',
    'Fumier/compost 5 a 10 t/ha, NPK au repiquage, uree en tallage si disponible',
    'Riziere maintenue avec 2 a 5 cm d eau; renouveler l eau selon besoin, pas vraiment en nombre d arrosages/jour.',
    'Pyriculariose, bacteriose, foreurs de tiges, mauvaises herbes, rats',
    'Garder les diguettes, utiliser des semences saines, sarcler 2 fois et vidanger avant recolte.',
    TRUE
FROM culture c
WHERE c.nom = 'Riz irrigue'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '110 a 140 jours',
    'Labour profond, hersage, lignes espacees pour faciliter le sarclage.',
    '50 a 80 kg/ha',
    'Compost 3 a 5 t/ha, NPK localise si possible',
    '1 arrosage leger le matin pendant la levee si pas de pluie; ensuite depend surtout des pluies.',
    'Pyriculariose, secheresse, vers blancs, adventices',
    'Choisir une parcelle non inondable mais humide et eviter les semis trop tardifs.',
    TRUE
FROM culture c
WHERE c.nom = 'Riz pluvial ameliore'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a decembre',
    '90 a 120 jours',
    'Labourer puis faire des poquets ou lignes sur sol bien draine.',
    '20 a 25 kg/ha',
    'Compost 5 t/ha, NPK au semis, uree au stade 4 a 6 feuilles',
    '1 arrosage le matin tous les 2 jours en absence de pluie; 1 a 2 arrosages/jour en pepiniere ou jardin tres sec.',
    'Chenille legionnaire, charbon, rouille, secheresse',
    'Semer apres les premieres vraies pluies et butter au premier sarclage.',
    TRUE
FROM culture c
WHERE c.nom = 'Mais'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '8 a 18 mois',
    'Sol meuble, billons ou buttes; planter des boutures saines inclinees.',
    '10 000 a 12 500 boutures/ha',
    'Compost au trou; cendre ou fumier bien decompose si sol pauvre',
    'Arroser 1 fois/jour pendant 2 a 3 semaines si plantation hors pluie, puis seulement en longue secheresse.',
    'Mosaique du manioc, cochenilles, pourriture racinaire',
    'Eviter les boutures malades et laisser assez d espace car la plante devient grande.',
    TRUE
FROM culture c
WHERE c.nom = 'Manioc'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a mars',
    '3 a 5 mois',
    'Former des buttes ou billons, sol leger et propre.',
    '30 000 a 40 000 boutures/ha',
    'Compost mur; eviter trop d azote qui favorise les feuilles',
    '1 arrosage/jour pendant 10 jours apres plantation si pas de pluie; ensuite 2 a 3 fois/semaine.',
    'Charancons, pourritures, viroses',
    'Planter des lianes saines et renouveler les boutures a chaque cycle.',
    TRUE
FROM culture c
WHERE c.nom = 'Patate douce'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout; possible contre-saison irriguee',
    '90 a 120 jours',
    'Sol profond, ameubli, billons; eviter parcelles avec solanacees recentes.',
    '1,5 a 2,5 t de plants/ha',
    'Fumier 10 a 20 t/ha, NPK riche en potassium',
    '1 arrosage/jour leger apres plantation si sec; puis 2 a 3 arrosages/semaine sans mouiller les feuilles.',
    'Mildiou, fletrissement bacterien, teigne, gale',
    'Utiliser des plants sains, butter deux fois et enlever les plants malades.',
    TRUE
FROM culture c
WHERE c.nom = 'Pomme de terre'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a juin; octobre a decembre',
    '70 a 90 jours',
    'Sol leger bien draine, lignes espacees, eviter exces d eau.',
    '50 a 80 kg/ha',
    'Compost, un peu de phosphate; eviter beaucoup d uree',
    '1 arrosage/jour pendant levee si sec; puis tous les 2 a 3 jours, surtout floraison.',
    'Anthracnose, rouille, pucerons, fontes de semis',
    'Rotation avec cereales; ne pas arroser le soir sur feuilles.',
    TRUE
FROM culture c
WHERE c.nom = 'Haricot sec'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '90 a 130 jours',
    'Sol sableux, meuble, sans grosses mottes; semis en lignes.',
    '60 a 100 kg de graines/ha',
    'Compost leger, phosphate; calcium si disponible',
    '1 arrosage tous les 2 jours a la levee si sec; ensuite pluie suffisante, eviter l eau stagnante.',
    'Rosette, cercosporiose, termites, aflatoxines au stockage',
    'Recolter quand les feuilles jaunissent et bien secher les gousses.',
    TRUE
FROM culture c
WHERE c.nom = 'Arachide'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '90 a 120 jours',
    'Lit de semence fin, sol bien draine et propre.',
    '60 a 80 kg/ha',
    'Compost, phosphate; inoculation rhizobium si disponible',
    '1 arrosage/jour jusqu a levee si sec; ensuite 2 fois/semaine avant floraison.',
    'Rouille, chenilles, punaises, fonte des semis',
    'Eviter exces d azote et recolter quand les gousses brunissent.',
    TRUE
FROM culture c
WHERE c.nom = 'Soja'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a aout',
    '90 a 150 jours',
    'Pepiniere fine puis repiquage sur planches surelevees.',
    '4 a 6 kg de semences/ha en pepiniere',
    'Compost bien decompose, NPK, apport potassique',
    'Pepiniere: 1 a 2 arrosages/jour leger; apres repiquage: 1 arrosage/jour puis 2 a 3 fois/semaine.',
    'Thrips, mildiou, pourriture du collet',
    'Arreter l arrosage 10 a 15 jours avant recolte pour mieux conserver les bulbes.',
    TRUE
FROM culture c
WHERE c.nom = 'Oignon'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a octobre; possible toute saison avec irrigation',
    '90 a 120 jours',
    'Pepiniere, repiquage sur billons, tuteurage obligatoire.',
    '150 a 250 g de semences/ha',
    'Compost 10 a 20 t/ha, NPK, apport calcium si possible',
    'Pepiniere: 1 a 2 arrosages/jour; champ: 1 arrosage/jour le matin en saison seche.',
    'Mildiou, fletrissement bacterien, aleurodes, noctuelles',
    'Pailler, tuteurer, supprimer feuilles malades et eviter arrosage sur feuillage.',
    TRUE
FROM culture c
WHERE c.nom = 'Tomate'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre',
    '90 a 110 jours',
    'Sol tres fin, profond, sans cailloux; semis direct en lignes.',
    '3 a 5 kg/ha',
    'Compost tres mur; eviter fumier frais',
    '1 arrosage fin/jour jusqu a levee; ensuite 2 a 3 fois/semaine.',
    'Alternariose, nematodes, pourritures',
    'Eclaircir tot pour obtenir des racines droites.',
    TRUE
FROM culture c
WHERE c.nom = 'Carotte'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a octobre; toute saison avec ombrage',
    '30 a 50 jours',
    'Planche fine, pepiniere ou semis direct, sol riche.',
    '300 a 500 g/ha',
    'Compost tamise, apport leger de NPK',
    '1 a 2 arrosages/jour legers, matin et fin d apres-midi, sans detremper.',
    'Fonte des semis, pucerons, pourriture basale',
    'Utiliser paillage propre et recolter tot le matin.',
    TRUE
FROM culture c
WHERE c.nom = 'Laitue'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a mars',
    '45 a 70 jours',
    'Buttes ou poquets enrichis, sol chaud et draine.',
    '1 a 2 kg/ha',
    'Compost au poquet, NPK leger',
    '1 arrosage/jour en saison seche; 2 petits arrosages/jour pendant fructification si forte chaleur.',
    'Oidium, mildiou, mouche des fruits, pucerons',
    'Pailler et eviter de mouiller les feuilles le soir.',
    TRUE
FROM culture c
WHERE c.nom = 'Concombre'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a mars',
    '45 a 65 jours',
    'Poquets sur sol meuble enrichi en compost.',
    '3 a 5 kg/ha',
    'Compost abondant, NPK equilibre',
    '1 arrosage/jour au pied; 2 si chaleur forte et sol sableux.',
    'Oidium, viroses, pucerons',
    'Recolter jeune tous les 2 a 3 jours pour stimuler la production.',
    TRUE
FROM culture c
WHERE c.nom = 'Courgette'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier',
    '90 a 150 jours',
    'Pepiniere puis repiquage, sol riche et draine.',
    '300 a 600 g/ha',
    'Compost, NPK, apport potassique en floraison',
    'Pepiniere: 1 a 2 fois/jour; champ: 1 fois/jour en saison seche au pied.',
    'Anthracnose, viroses, pucerons, acariens',
    'Eviter exces d eau, pailler et recolter les fruits murs regulierement.',
    TRUE
FROM culture c
WHERE c.nom = 'Piment'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a decembre',
    '8 a 10 mois',
    'Sol profond, leger, riche, buttes sous ombrage leger.',
    '1,5 a 2,5 t de rhizomes/ha',
    'Compost 10 a 20 t/ha, paillage epais',
    '1 arrosage/jour au demarrage si pas de pluie; ensuite garder humide sans stagnation.',
    'Pourriture rhizome, bacteriose, nematodes',
    'Pailler fortement et utiliser seulement des rhizomes sains.',
    TRUE
FROM culture c
WHERE c.nom = 'Gingembre'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '3 ans avant premieres gousses; 8 a 9 mois apres pollinisation',
    'Installer tuteurs vivants, ombrage 50 pourcent, paillage, bon drainage.',
    '2 000 a 2 500 boutures/ha selon espacement',
    'Compost, feuilles mortes, mulch; eviter engrais chimique fort',
    'Pas d arrosage quotidien en saison humide; en pepiniere/arret de pluie: brumiser 1 fois/jour sans detremper.',
    'Fusariose, pourriture racinaire, anthracnose',
    'Controler l ombrage, polliniser le matin, limiter le nombre de gousses par liane.',
    TRUE
FROM culture c
WHERE c.nom = 'Vanille'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '5 a 7 ans avant production',
    'Pepiniere puis trou large, sol profond et humide, exposition mi-ombrage jeune.',
    '150 a 200 plants/ha',
    'Compost au trou, paillage, apport organique annuel',
    'Jeunes plants: 1 arrosage/jour pendant saison seche; arbres adultes: seulement en secheresse.',
    'Chancre, dessèchement, attaques de foreurs',
    'Proteger les jeunes plants du soleil direct et recolter les boutons avant ouverture.',
    TRUE
FROM culture c
WHERE c.nom = 'Girofle'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '3 a 4 ans avant recolte',
    'Pepiniere ombragee, plantation sous arbres d ombrage, sol profond humide.',
    '800 a 1 100 plants/ha',
    'Compost au trou, mulch, apports organiques annuels',
    'Jeunes plants: 1 arrosage/jour si saison seche; adultes: maintenir paillage, irriguer seulement si secheresse.',
    'Pourriture brune des cabosses, mirides, champignons',
    'Maintenir ombrage 40 a 60 pourcent et recolter uniquement les cabosses mures.',
    TRUE
FROM culture c
WHERE c.nom = 'Cacao'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '2 a 3 ans avant recolte',
    'Planter boutures au pied de tuteurs vivants, sol riche et draine.',
    '1 100 a 1 600 boutures/ha',
    'Compost, paillage, apport potassique organique',
    '1 arrosage/jour au demarrage en saison seche; ensuite garder humidite par paillage.',
    'Pourriture du collet, anthracnose, cochenilles',
    'Eviter stagnation d eau et tailler les tuteurs pour garder bonne lumiere.',
    TRUE
FROM culture c
WHERE c.nom = 'Poivre noir'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '2 a 3 ans avant premiere coupe',
    'Pepiniere puis plantation en sol humide bien draine.',
    '2 500 a 5 000 plants/ha selon systeme',
    'Compost au trou, paillage annuel',
    'Jeunes plants: 1 arrosage/jour si sec; adultes: pluie suffisante.',
    'Taches foliaires, termites, dessèchement',
    'Recéper pour produire des tiges droites et recolter l ecorce en saison humide.',
    TRUE
FROM culture c
WHERE c.nom = 'Cannelle'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '4 a 6 ans avant production',
    'Trou large, sol profond, protection contre vents, paillage.',
    '100 a 200 plants/ha',
    'Compost au trou, fumure organique annuelle',
    'Jeunes plants: 1 arrosage/jour en saison seche; adultes: irrigation ponctuelle avant floraison si sec.',
    'Mouches des fruits, anthracnose, cochenilles',
    'Eviter taille severe; proteger les jeunes fruits et recolter vite a maturite.',
    TRUE
FROM culture c
WHERE c.nom = 'Litchi'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison avec eau; mieux novembre a mars',
    '9 a 15 mois',
    'Trou profond avec compost, rejets sains, paillage.',
    '1 000 a 1 600 rejets/ha',
    'Compost/fumier abondant, cendre, potassium',
    '1 arrosage/jour pour jeunes plants en saison seche; adultes: 2 a 3 fois/semaine si sec.',
    'Sigatoka, charancon du bananier, nematodes, fusariose',
    'Garder 1 mere + 1 fille + 1 petite fille par touffe et enlever feuilles malades.',
    TRUE
FROM culture c
WHERE c.nom = 'Banane'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '12 a 18 mois',
    'Planter rejets sur billons, sol draine et ensoleille.',
    '40 000 a 60 000 plants/ha',
    'Compost, apport potassique, paillage',
    '1 arrosage tous les 2 jours apres plantation si sec; ensuite peu, car plante tolerante.',
    'Cochenilles, pourriture du coeur, fusariose',
    'Utiliser rejets calibres et eviter eau stagnante au coeur.',
    TRUE
FROM culture c
WHERE c.nom = 'Ananas'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a fevrier',
    '10 a 18 mois',
    'Labour profond, sillons, planter boutures de cannes saines.',
    '6 a 10 t de boutures/ha',
    'Fumier/compost, NPK, apports azotes fractionnes',
    '1 arrosage tous les 2 a 4 jours en perimetre irrigue; pluies suffisantes en zone humide.',
    'Foreurs, charbon, rouille, rats',
    'Desherber tot, butter et recolter a bonne maturite en saison seche.',
    TRUE
FROM culture c
WHERE c.nom = 'Canne a sucre'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);


INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '80 a 110 jours',
    'Sol fin et propre; semis peu profond car graine petite.',
    '3 a 5 kg/ha',
    'Compost leger, phosphate si sol pauvre',
    'Arrosage leger a la levee seulement si pas de pluie; eviter exces d eau.',
    'Fletrissement, chenilles, pucerons',
    'Recolter avant ouverture complete des capsules pour eviter pertes.',
    TRUE
FROM culture c
WHERE c.nom = 'Sesame'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier',
    '90 a 120 jours',
    'Labourer, semer en lignes espacees sur sol bien draine.',
    '5 a 8 kg/ha',
    'Compost, NPK avec potassium',
    '1 arrosage tous les 2 a 3 jours en absence de pluie; besoin fort a floraison.',
    'Oiseaux, mildiou, alternariose',
    'Proteger les capitules contre oiseaux et recolter quand le dos jaunit.',
    TRUE
FROM culture c
WHERE c.nom = 'Tournesol'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '3 a 5 ans avant production',
    'Trou large, sol draine, porte-greffe sain, paillage.',
    '200 a 400 plants/ha',
    'Compost au trou, fumure organique annuelle, apport potassique',
    'Jeunes plants: 1 arrosage/jour en saison seche; adultes: 1 fois/semaine si sec.',
    'Gommose, tristeza, mineuse, cochenilles',
    'Eviter blessures au collet et tailler branches mortes.',
    TRUE
FROM culture c
WHERE c.nom = 'Orange'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars',
    '3 a 5 ans greffe; 5 a 7 ans franc',
    'Trou profond, sol bien draine, protection contre vent.',
    '100 a 200 plants/ha',
    'Compost au trou, paillage, apport organique annuel',
    'Jeunes plants: 1 arrosage/jour en saison seche; adultes: 1 fois/semaine si secheresse.',
    'Pourriture racinaire phytophthora, anthracnose',
    'Ne jamais planter en sol gorge d eau; pailler sans toucher le tronc.',
    TRUE
FROM culture c
WHERE c.nom = 'Avocat'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a fevrier',
    '3 a 5 ans greffe; 6 ans et plus franc',
    'Trou large, plein soleil, sol profond draine.',
    '100 a 156 plants/ha',
    'Compost au trou, fumure organique annuelle',
    'Jeunes plants: 1 arrosage/jour au debut; adultes peu irrigues sauf secheresse extreme.',
    'Anthracnose, mouches des fruits, oidium',
    'Tailler legerement et ramasser fruits attaques pour limiter les mouches.',
    TRUE
FROM culture c
WHERE c.nom = 'Mangue'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison avec irrigation; mieux novembre a mars',
    '8 a 12 mois',
    'Pepiniere puis trou avec compost, sol draine.',
    '1 500 a 2 500 plants/ha',
    'Compost, NPK leger, apport potassique',
    '1 arrosage/jour en saison seche, au pied, sans inonder.',
    'Papaya ringspot virus, pourriture racinaire, cochenilles',
    'Eviter eau stagnante et garder quelques plants males/hermaphrodites selon variete.',
    TRUE
FROM culture c
WHERE c.nom = 'Papaye'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier',
    '70 a 90 jours',
    'Poquets sur sol sableux enrichi, plein soleil.',
    '1 a 2 kg/ha',
    'Compost au poquet, potassium en floraison',
    '1 arrosage/jour au demarrage; ensuite 2 a 3 fois/semaine, reduire avant recolte.',
    'Mouches des fruits, oidium, pucerons',
    'Pailler les fruits et eviter humidite excessive en fin de cycle.',
    TRUE
FROM culture c
WHERE c.nom = 'Pastèque'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier',
    '70 a 90 jours',
    'Buttes/poquets avec compost, sol bien draine.',
    '1 a 1,5 kg/ha',
    'Compost, NPK, potassium',
    '1 arrosage/jour au pied; reduire quand fruits approchent maturite.',
    'Oidium, pucerons, mouches des fruits',
    'Eviter mouiller feuilles et mettre paille sous fruits.',
    TRUE
FROM culture c
WHERE c.nom = 'Melon'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);
-- Cultures maraicheres et aromatiques courantes de Madagascar - complement marche quotidien
-- Compatible avec les tables culture et fiche_culture du projet voly_saina

INSERT INTO culture(nom, description, saison_recommandee, localisation_recommandee, actif) VALUES
('Persil', 'Herbe aromatique tres vendue en bottes sur les marches, surtout autour des villes et zones fraiches.', 'Toute saison fraiche avec irrigation', 'Analamanga: Antananarivo, Ambohidratrimo, Avaradrano; Vakinankaratra: Antsirabe; Itasy: Miarinarivo', TRUE),
('Ciboulette', 'Aromatique en feuilles fines utilisee dans les sauces, omelettes et salades.', 'Toute saison avec eau', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra, jardins urbains de Toamasina et Mahajanga', TRUE),
('Coriandre', 'Aromatique courante pour bouillons, salades et achards, vendue en bottes fraiches.', 'Saison fraiche ou mi-ombre en saison chaude', 'Analamanga, Itasy, Vakinankaratra, Atsinanana, Boeny irrigue', TRUE),
('Celeri branche', 'Legume aromatique pour soupe et sauces, demande beaucoup d eau et de matiere organique.', 'Saison fraiche irriguee', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra', TRUE),
('Poireau', 'Legume feuille et bulbe tres utilise dans les soupes et plats quotidiens.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy, Amoron i Mania, Haute Matsiatra', TRUE),
('Ail', 'Bulbe condimentaire de base, tres present sur les marches et dans les cuisines.', 'Saison fraiche seche irriguee', 'Vakinankaratra, Itasy, Analamanga, Amoron i Mania, Haute Matsiatra', TRUE),
('Echalote', 'Petit bulbe condimentaire vendu frais ou sec, proche de l oignon.', 'Saison fraiche irriguee', 'Vakinankaratra, Itasy, Analamanga, Boeny irrigue, Menabe irrigue', TRUE),
('Oignon vert', 'Oignon recolte jeune avec feuilles, vendu en bottes comme condiment frais.', 'Toute saison avec irrigation', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, Boeny', TRUE),
('Chou chinois Petsay', 'Chou chinois allonge tres courant dans les marches, saute ou bouilli.', 'Saison fraiche ou mi-ombre', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, Boeny irrigue', TRUE),
('Chou-fleur', 'Legume fleur de saison fraiche vendu en tete blanche.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy, Haute Matsiatra', TRUE),
('Brocoli', 'Legume fleur vert, de plus en plus vendu dans les marches urbains.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy, Haute Matsiatra', TRUE),
('Epinard', 'Legume feuille vert vendu en bottes, apprecie en zones fraiches.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy, Amoron i Mania', TRUE),
('Anamamy', 'Brede morelle, legume feuille traditionnel tres commun dans les repas malgaches.', 'Toute saison avec eau', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, Analanjirofo, Boeny', TRUE),
('Anamadinika', 'Brede amarante tres courante, rapide et adaptee aux petits potagers.', 'Toute saison chaude avec humidite', 'Analamanga, Atsinanana, Analanjirofo, Boeny, Diana, Vakinankaratra', TRUE),
('Ravitoto', 'Feuilles de manioc consommees pilees; culture familiale et marche local.', 'Debut saison des pluies ou toute saison humide', 'Atsinanana, Analanjirofo, Vakinankaratra, Analamanga, Boeny, Atsimo-Andrefana', TRUE),
('Cresson', 'Legume feuille aquatique ou de bord de ruisseau, vendu en bottes.', 'Toute saison en eau fraiche propre', 'Analamanga: zones de sources et ruisseaux; Vakinankaratra; Itasy; Haute Matsiatra', TRUE),
('Brède moutarde', 'Feuille de moutarde piquante, utilisee en bouillon ou sautee.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra, Boeny irrigue', TRUE),
('Bok choy', 'Petit chou chinois a tiges blanches ou vertes, courant dans cuisines urbaines.', 'Saison fraiche ou ombrage leger', 'Analamanga, Vakinankaratra, Itasy, Atsinanana', TRUE),
('Radis', 'Legume racine rapide vendu en bottes, surtout autour des villes.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra', TRUE),
('Navet', 'Racine blanche/violette pour soupe et accompagnement, marche frais.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy, Amoron i Mania', TRUE),
('Betterave', 'Racine rouge vendue pour salade et cuisson.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra', TRUE),
('Aubergine locale', 'Legume fruit courant pour laoka, marche frais; aime chaleur et soleil.', 'Saison chaude ou toute saison irriguee', 'Analamanga irrigue, Boeny, Atsinanana, Diana, Atsimo-Andrefana irrigue', TRUE),
('Poivron', 'Legume fruit doux vert ou rouge, courant dans achards et plats.', 'Saison chaude avec irrigation', 'Analamanga, Itasy, Vakinankaratra basse altitude, Boeny, Atsinanana', TRUE),
('Piment oiseau', 'Petit piment fort tres utilise comme condiment, vendu frais ou sec.', 'Saison chaude', 'Atsinanana, Analanjirofo, Boeny, Diana, Atsimo-Andrefana, Analamanga irrigue', TRUE),
('Haricot vert', 'Legume gousse frais tres courant dans les marches et gargotes.', 'Saison fraiche a temperee', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra, Alaotra-Mangoro', TRUE),
('Petit pois frais', 'Legumineuse de saison fraiche vendue en gousses ou grains frais.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy, Haute Matsiatra', TRUE),
('Pois mange-tout', 'Pois consomme avec gousse tendre, marche frais et cuisine rapide.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy', TRUE),
('Gombo', 'Legume fruit mucilagineux courant en zones chaudes, pour sauces.', 'Saison chaude', 'Boeny, Atsimo-Andrefana, Menabe, Diana, Atsinanana, Analamanga en ete', TRUE),
('Courge locale', 'Cucurbitacee courante pour soupe et laoka, conserve bien apres recolte.', 'Saison chaude pluvieuse', 'Analamanga, Itasy, Vakinankaratra, Boeny, Menabe, Atsimo-Andrefana', TRUE),
('Citrouille', 'Grosse cucurbitacee de marche, utilisee en soupe et plats familiaux.', 'Saison chaude', 'Itasy, Analamanga, Vakinankaratra, Boeny, Menabe', TRUE),
('Chayote Sosoty', 'Legume fruit grimpant tres commun en hautes terres et zones humides.', 'Saison des pluies ou toute saison humide', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, Haute Matsiatra', TRUE),
('Chouchou feuilles', 'Jeunes pousses de chayote consommees comme bredes, vendues en bottes.', 'Toute saison humide', 'Analamanga, Vakinankaratra, Itasy, Atsinanana', TRUE),
('Basilic', 'Aromatique pour sauces, salades et plats; pousse bien en climat chaud.', 'Toute saison chaude avec eau', 'Analamanga, Atsinanana, Boeny, Diana, Itasy, Vakinankaratra', TRUE),
('Menthe', 'Aromatique pour infusion, jus et sauces, vendue en bottes.', 'Toute saison avec humidite', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, jardins urbains', TRUE),
('Thym', 'Aromatique de cuisine, prefere sol draine et soleil.', 'Saison seche fraiche irriguee', 'Analamanga, Vakinankaratra, Itasy, jardins urbains secs', TRUE),
('Romarin', 'Aromatique arbustive vendue en petites bottes, supporte secheresse.', 'Saison fraiche seche avec irrigation au depart', 'Analamanga, Vakinankaratra, Itasy, jardins urbains', TRUE),
('Oseille', 'Feuille acidulee vendue en bottes, utilisee en soupe et sauces.', 'Saison fraiche a humide', 'Analamanga, Vakinankaratra, Itasy, Atsinanana', TRUE),
('Fenouil feuille', 'Aromatique au gout anise, utilisee en salade, soupe et condiment.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy', TRUE),
('Aneth', 'Aromatique fine pour poisson, sauces et salades.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy', TRUE),
('Laurier sauce', 'Arbuste aromatique pour feuilles de sauce, adapte aux jardins.', 'Toute saison hors forte secheresse', 'Analamanga, Vakinankaratra, Itasy, Atsinanana, jardins urbains', TRUE),
('Tatsoi', 'Petite feuille asiatique proche du petsay, vendue comme brede tendre.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy', TRUE),
('Concombre amer Margose', 'Legume fruit amer utilise en cuisine et parfois vendu en marche.', 'Saison chaude humide', 'Atsinanana, Analanjirofo, Boeny, Diana, Analamanga en ete', TRUE),
('Laitue batavia', 'Variete de laitue croquante vendue en salade, differente de la laitue simple.', 'Saison fraiche ou ombrage', 'Analamanga, Vakinankaratra, Itasy, Haute Matsiatra', TRUE),
('Tomate cerise', 'Petite tomate vendue pour salade et cuisine, productive en grappe.', 'Saison seche fraiche irriguee ou saison chaude surveillee', 'Analamanga, Itasy, Vakinankaratra, Boeny, Atsinanana', TRUE),
('Carotte courte', 'Variete de carotte courte adaptee aux sols moins profonds, vendue en bottes.', 'Saison fraiche', 'Analamanga, Vakinankaratra, Itasy', TRUE),
('Maïs doux', 'Mais recolte jeune en epis tendres, vendu bouilli ou grille.', 'Debut saison des pluies ou irrigue', 'Itasy, Analamanga, Vakinankaratra, Boeny, Menabe', TRUE),
('Patate douce feuilles', 'Jeunes feuilles de patate douce consommees comme brede, tres courant familialement.', 'Saison chaude humide ou irriguee', 'Vakinankaratra, Itasy, Analamanga, Atsimo-Andrefana, Boeny', TRUE),
('Dolique asperge', 'Haricot long vendu en gousses vertes, courant en zones chaudes.', 'Saison chaude', 'Boeny, Atsinanana, Diana, Atsimo-Andrefana, Analamanga en ete', TRUE),
('Fève', 'Legumineuse de saison fraiche vendue fraiche ou seche dans certaines zones.', 'Saison fraiche', 'Vakinankaratra, Analamanga, Itasy, Haute Matsiatra', TRUE)
ON CONFLICT (nom) DO NOTHING;

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a octobre, ou toute saison sous arrosage',
    '45 a 70 jours',
    'Sol fin, riche en compost, planches surelevees, ombrage leger en saison chaude.',
    '4 a 6 kg/ha ou semis en pepiniere',
    'Compost tamise, fumier bien decompose, petit apport NPK faible dose apres coupe',
    '1 a 2 arrosages legers/jour au semis; ensuite 1 arrosage/jour, matin de preference.',
    'Fonte des semis, taches foliaires, pucerons, limaces',
    'Recolter feuille par feuille ou couper a 5 cm du sol; garder le sol humide mais non gorge d eau.',
    TRUE
FROM culture c
WHERE c.nom = 'Persil'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre ou toute saison en pot',
    '60 a 90 jours puis coupes regulieres',
    'Planche meuble, compostee, drainage correct; eviter sols acides lourds.',
    'Semis 8 a 12 kg/ha ou division de touffes',
    'Compost, fumier bien decompose, apport leger apres chaque coupe',
    '1 arrosage/jour; 2 petits arrosages/jour en forte chaleur ou en pot.',
    'Rouille, thrips, pourriture du collet si exces d eau',
    'Couper sans arracher; diviser les touffes tous les 8 a 12 mois pour garder la vigueur.',
    TRUE
FROM culture c
WHERE c.nom = 'Ciboulette'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout; possible toute saison avec ombrage',
    '30 a 45 jours pour feuilles, 90 jours pour graines',
    'Sol leger, bien emiette, semis direct en lignes, ombrage leger possible.',
    '10 a 15 kg/ha',
    'Compost fin; eviter trop d azote qui fragilise les tiges',
    '1 arrosage/jour; 2 legers/jour pendant levee si sol seche vite.',
    'Fonte des semis, pucerons, montaison rapide, taches foliaires',
    'Semer en petites quantites toutes les 2 semaines pour avoir des bottes continues.',
    TRUE
FROM culture c
WHERE c.nom = 'Coriandre'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a aout',
    '100 a 140 jours',
    'Pepiniere puis repiquage, planches riches en compost, sol profond et frais.',
    '250 a 400 g/ha en pepiniere',
    'Fumier/compost abondant, NPK equilibre, apport azote fractionne',
    '1 arrosage/jour obligatoire; 2/jour en periode seche chaude.',
    'Septoriose, pourriture du coeur, pucerons, limaces',
    'Pailler pour garder l humidite; eviter le stress hydrique qui rend les tiges dures.',
    TRUE
FROM culture c
WHERE c.nom = 'Celeri branche'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a juillet',
    '120 a 160 jours',
    'Pepiniere 45 jours, repiquage en sillons, butter progressivement les plants.',
    '2 a 4 kg/ha',
    'Compost, fumier decompose, NPK au repiquage, azote leger apres reprise',
    '1 arrosage/jour apres repiquage; ensuite 3 a 5 arrosages/semaine selon pluie.',
    'Rouille, thrips, teigne du poireau, pourriture blanche',
    'Butter pour blanchir le fut; rotation avec legumes non allium pendant 3 ans.',
    TRUE
FROM culture c
WHERE c.nom = 'Poireau'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a juin',
    '120 a 150 jours',
    'Sol meuble, planches surelevees, planter les caieux sains pointe vers le haut.',
    '500 a 800 kg de caieux/ha',
    'Compost bien mur; eviter fumier frais; NPK faible a moyen',
    'Arrosage 3 a 4 fois/semaine au debut; reduire puis arreter 2 semaines avant recolte.',
    'Rouille, pourriture blanche, nematodes, thrips',
    'Ne pas planter apres oignon/poireau; bien secher les bulbes apres recolte.',
    TRUE
FROM culture c
WHERE c.nom = 'Ail'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a juillet',
    '90 a 120 jours',
    'Sol leger, planches surelevees, plantation de petits bulbes ou semis.',
    '600 a 1000 kg de bulbes/ha',
    'Compost, cendre en petite quantite, NPK modere',
    '1 arrosage/jour apres plantation; ensuite 2 a 4/semaine; arreter avant recolte.',
    'Thrips, mildiou, pourriture du collet',
    'Recolter quand les feuilles se couchent; secher a l ombre ventilee.',
    TRUE
FROM culture c
WHERE c.nom = 'Echalote'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison, meilleur mars a septembre',
    '45 a 70 jours',
    'Semis dense sur planches fines, compostees, drainage correct.',
    '8 a 12 kg/ha',
    'Compost fin, petit apport azote apres levee',
    '1 arrosage/jour; 2 petits arrosages/jour pendant levee en saison chaude.',
    'Thrips, fonte des semis, mildiou',
    'Recolter jeune; semer toutes les 2 a 3 semaines pour vente continue.',
    TRUE
FROM culture c
WHERE c.nom = 'Oignon vert'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre; possible toute saison en zones fraiches',
    '45 a 70 jours',
    'Pepiniere courte puis repiquage; sol meuble riche et bien draine.',
    '300 a 500 g/ha',
    'Compost, fumier decompose, NPK, apport azote leger apres reprise',
    '1 arrosage/jour; 2/jour les 7 premiers jours apres repiquage si sec.',
    'Altises, chenilles, pucerons, pourriture molle',
    'Proteger jeunes plants avec filet; recolter avant montee en fleurs.',
    TRUE
FROM culture c
WHERE c.nom = 'Chou chinois Petsay'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a juillet',
    '90 a 120 jours',
    'Pepiniere, repiquage en sol profond riche; apporter compost avant plantation.',
    '250 a 350 g/ha',
    'Fumier/compost 10 a 20 t/ha, NPK equilibre, bore si carence',
    '1 arrosage/jour apres repiquage; ensuite 3 a 5/semaine, regulier en formation de pomme.',
    'Chenilles, pucerons, hernie du chou, pourriture noire',
    'Attacher quelques feuilles sur la pomme pour garder la blancheur; rotation 3 ans.',
    TRUE
FROM culture c
WHERE c.nom = 'Chou-fleur'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a juillet',
    '70 a 100 jours',
    'Pepiniere puis repiquage; sol riche, frais et draine.',
    '250 a 350 g/ha',
    'Compost, NPK, apport azote fractionne',
    '1 arrosage/jour au debut; ensuite 3 a 5/semaine, eviter stress hydrique.',
    'Chenilles, pucerons, hernie du chou, mildiou',
    'Couper la tete principale avant ouverture des fleurs; laisser repousses laterales.',
    TRUE
FROM culture c
WHERE c.nom = 'Brocoli'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '35 a 55 jours',
    'Sol fin, riche en humus, semis direct en lignes ou a la volee.',
    '20 a 30 kg/ha',
    'Compost tamise, faible azote apres coupe',
    '1 arrosage/jour; 2 legers/jour pendant levee si chaud.',
    'Mildiou, fonte des semis, pucerons, limaces',
    'Recolter les feuilles jeunes; ombrer legerement en saison chaude pour retarder la montee.',
    TRUE
FROM culture c
WHERE c.nom = 'Epinard'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison, surtout saison des pluies',
    '30 a 50 jours',
    'Sol meuble, compost, semis direct ou repiquage de jeunes plants.',
    '1 a 2 kg/ha',
    'Compost ou fumier bien decompose; apport leger apres coupes',
    '1 arrosage/jour; 2/jour en saison chaude seche.',
    'Altises, pucerons, chenilles, taches foliaires',
    'Couper les jeunes pousses regulierement; eviter pesticides forts juste avant recolte.',
    TRUE
FROM culture c
WHERE c.nom = 'Anamamy'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison, meilleur octobre a avril',
    '25 a 40 jours',
    'Planche fine, semis clair, couverture legere avec terreau.',
    '1 a 2 kg/ha',
    'Compost fin, purin/engrais organique dilue apres coupe',
    '1 arrosage/jour; 2 tres legers/jour jusqu a levee.',
    'Altises, chenilles, fonte des semis',
    'Semer toutes les 2 semaines; recolter jeune pour feuilles tendres.',
    TRUE
FROM culture c
WHERE c.nom = 'Anamadinika'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a janvier; boutures possibles avec irrigation',
    '4 a 6 mois pour feuilles regulieres',
    'Planter boutures de manioc sur billons ou buttes, sol ameubli.',
    '8000 a 12000 boutures/ha',
    'Compost au trou si disponible, culture peu exigeante',
    'Arrosage 2 a 3 fois/semaine au demarrage si pas de pluie; ensuite pluie suffit.',
    'Mosaique du manioc, cochenilles, acariens',
    'Recolter sans defolier completement; utiliser varietes douces et bien cuire les feuilles.',
    TRUE
FROM culture c
WHERE c.nom = 'Ravitoto'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison',
    '25 a 45 jours apres reprise',
    'Canaux peu profonds avec eau propre courante, substrat riche mais non pollue.',
    'Boutures/tiges: 500 a 800 kg/ha',
    'Compost tres bien decompose en bordure, eviter contamination de l eau',
    'Culture en eau constante; renouvellement permanent, pas d arrosage classique.',
    'Limaces, pucerons, pourritures, contamination par eau sale',
    'Utiliser seulement eau propre; laver soigneusement; recolter jeunes pousses.',
    TRUE
FROM culture c
WHERE c.nom = 'Cresson'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '30 a 50 jours',
    'Sol meuble, semis direct ou pepiniere courte, lignes espacees.',
    '3 a 5 kg/ha',
    'Compost, petit apport azote organique apres coupe',
    '1 arrosage/jour; 2/jour pendant levee si sec.',
    'Altises, chenilles, pucerons, mildiou',
    'Recolter jeune; filet anti-insectes utile contre altises.',
    TRUE
FROM culture c
WHERE c.nom = 'Brède moutarde'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre',
    '35 a 55 jours',
    'Pepiniere courte ou semis direct, sol riche et frais.',
    '300 a 500 g/ha',
    'Compost, fumier decompose, azote leger apres reprise',
    '1 arrosage/jour; 2/jour au repiquage si chaleur.',
    'Altises, chenilles, pucerons, pourriture molle',
    'Recolter jeune et tendre; eviter exces d azote.',
    TRUE
FROM culture c
WHERE c.nom = 'Bok choy'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre',
    '25 a 40 jours',
    'Sol fin sans cailloux, semis direct clair, humidite reguliere.',
    '8 a 12 kg/ha',
    'Compost bien mur, eviter fumier frais',
    '1 arrosage leger/jour pour racines tendres; ne pas laisser secher.',
    'Altises, fonte des semis, racines fendues',
    'Recolter tot; manque d eau donne radis piquant et creux.',
    TRUE
FROM culture c
WHERE c.nom = 'Radis'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '45 a 70 jours',
    'Sol meuble, profond, sans cailloux; semis direct en lignes.',
    '3 a 5 kg/ha',
    'Compost, engrais potassique leger si sol pauvre',
    '1 arrosage/jour au debut; ensuite 3 a 5/semaine, regulier.',
    'Altises, hernie du chou, pucerons, pourriture',
    'Eclaircir les plants; rotation avec legumes non cruciferes.',
    TRUE
FROM culture c
WHERE c.nom = 'Navet'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '60 a 90 jours',
    'Sol profond meuble, semis en lignes, eclaircissage obligatoire.',
    '8 a 12 kg/ha',
    'Compost, NPK faible a moyen, potasse utile',
    '1 arrosage/jour jusqu a levee; ensuite 3 a 4/semaine.',
    'Cercosporiose, fonte des semis, pucerons',
    'Eclaircir a 10 cm; garder humidite stable pour racines regulieres.',
    TRUE
FROM culture c
WHERE c.nom = 'Betterave'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier; toute saison en zones chaudes',
    '90 a 130 jours',
    'Pepiniere puis repiquage, sol riche, tuteurage leger possible.',
    '150 a 250 g/ha',
    'Compost, fumier decompose, NPK, apport potasse en fructification',
    '1 arrosage/jour apres repiquage; ensuite 3 a 5/semaine, plus en floraison.',
    'Pucerons, aleurodes, acariens, fletrissement bacterien',
    'Pailler, enlever fruits abimes, rotation avec non solanacees.',
    TRUE
FROM culture c
WHERE c.nom = 'Aubergine locale'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier',
    '90 a 120 jours',
    'Pepiniere, repiquage sur planche riche, tuteurage si charge forte.',
    '150 a 250 g/ha',
    'Compost, NPK, apport potasse a la floraison',
    '1 arrosage/jour au debut; ensuite 3 a 5/semaine; eviter exces d eau.',
    'Pucerons, aleurodes, anthracnose, virus, pourriture apicale',
    'Ne pas mouiller trop le feuillage; recolter vert ou colore selon marche.',
    TRUE
FROM culture c
WHERE c.nom = 'Poivron'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a janvier',
    '100 a 150 jours',
    'Pepiniere puis repiquage; sol draine, plein soleil.',
    '150 a 250 g/ha',
    'Compost, NPK modere, potasse en production',
    '1 arrosage/jour apres repiquage; ensuite 2 a 4/semaine selon chaleur.',
    'Anthracnose, pucerons, acariens, virus',
    'Recolter regulierement pour stimuler floraison; secher au soleil propre si vendu sec.',
    TRUE
FROM culture c
WHERE c.nom = 'Piment oiseau'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a juin ou septembre a novembre',
    '45 a 65 jours',
    'Semis direct en lignes, sol meuble et bien draine, tuteurage si grimpant.',
    '40 a 70 kg/ha',
    'Compost; peu d azote; phosphore utile au semis',
    '1 arrosage/jour jusqu a levee; ensuite 3 a 4/semaine, important en floraison.',
    'Anthracnose, rouille, pucerons, mouches des semis',
    'Cueillir tous les 2 jours pour gousses tendres; eviter mouiller feuilles le soir.',
    TRUE
FROM culture c
WHERE c.nom = 'Haricot vert'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a juillet',
    '70 a 100 jours',
    'Sol frais, semis direct, tuteurage avec branches ou filets.',
    '60 a 100 kg/ha',
    'Compost, phosphore; eviter exces d azote',
    '1 arrosage/jour au semis; ensuite 3 a 4/semaine, regulier a floraison.',
    'Oïdium, pucerons, fonte des semis, pourriture racinaire',
    'Semer en saison fraiche; recolter avant durcissement des grains.',
    TRUE
FROM culture c
WHERE c.nom = 'Petit pois frais'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a juillet',
    '60 a 80 jours',
    'Semis direct, sol meuble, palissage obligatoire.',
    '50 a 80 kg/ha',
    'Compost, phosphore, peu d azote',
    '1 arrosage/jour au depart; ensuite 3 a 4/semaine.',
    'Oïdium, pucerons, mouches mineuses',
    'Cueillir tres jeune tous les 2 jours pour conserver tendrete.',
    TRUE
FROM culture c
WHERE c.nom = 'Pois mange-tout'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a janvier',
    '50 a 70 jours',
    'Semis direct en poquets, sol chaud et draine.',
    '8 a 12 kg/ha',
    'Compost, NPK modere',
    '1 arrosage/jour jusqu a levee; ensuite 2 a 3/semaine, plus si sec.',
    'Pucerons, chenilles, oïdium, nematodes',
    'Recolter jeunes fruits tous les 2 jours; fruits vieux deviennent fibreux.',
    TRUE
FROM culture c
WHERE c.nom = 'Gombo'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a janvier',
    '90 a 130 jours',
    'Semis en poquets sur buttes enrichies, grand espacement.',
    '3 a 5 kg/ha',
    'Fumier/compost dans les trous, NPK ou cendre/compost',
    '1 arrosage/jour au depart si sec; ensuite 2 a 3/semaine, regulier en fructification.',
    'Oïdium, mildiou, chrysomeles, pucerons',
    'Pailler sous les fruits; recolter quand peau durcit et pedoncule seche.',
    TRUE
FROM culture c
WHERE c.nom = 'Courge locale'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a janvier',
    '100 a 140 jours',
    'Buttes larges avec fumier decompose, semis direct 2 a 3 graines/trou.',
    '3 a 5 kg/ha',
    'Compost/fumier au trou, potasse utile',
    '1 arrosage/jour au demarrage; ensuite 2 a 3/semaine; plus a nouaison.',
    'Oïdium, pucerons, pourriture des fruits',
    'Laisser espace aux lianes; tourner fruits doucement pour eviter pourriture au contact du sol.',
    TRUE
FROM culture c
WHERE c.nom = 'Citrouille'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a decembre',
    '4 a 6 mois puis production longue',
    'Planter fruit germe au pied d une treille solide, sol riche et frais.',
    '800 a 1200 fruits semences/ha selon densite',
    'Fumier/compost au trou, apport organique regulier',
    '1 arrosage/jour au demarrage; ensuite 2 a 4/semaine si pas de pluie.',
    'Oïdium, pucerons, pourriture du collet',
    'Prevoir support robuste; recolter fruits jeunes avant durcissement.',
    TRUE
FROM culture c
WHERE c.nom = 'Chayote Sosoty'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison avec eau',
    '30 a 45 jours apres reprise pour premieres pousses',
    'Meme culture que sosoty, treille ou support, sol riche.',
    'Boutures ou fruits germes',
    'Compost et fumier decompose au pied',
    '1 arrosage/jour au demarrage; ensuite 2 a 4/semaine.',
    'Pucerons, oïdium, chenilles',
    'Cueillir les pointes tendres sans affaiblir toute la liane.',
    TRUE
FROM culture c
WHERE c.nom = 'Chouchou feuilles'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Septembre a avril; toute saison en zones chaudes',
    '35 a 60 jours',
    'Semis en pepiniere ou direct, sol riche, plein soleil a mi-ombre.',
    '2 a 4 kg/ha',
    'Compost fin, petit apport organique apres coupes',
    '1 arrosage/jour; 2/jour en pot ou forte chaleur.',
    'Mildiou, pucerons, fonte des semis',
    'Pincer les fleurs pour garder les feuilles; recolter le matin.',
    TRUE
FROM culture c
WHERE c.nom = 'Basilic'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Toute saison',
    '30 a 50 jours apres bouturage',
    'Sol frais, mi-ombre possible, plantation de stolons/boutures.',
    'Boutures: 20000 a 40000 plants/ha',
    'Compost, fumier bien decompose, apport leger apres coupe',
    '1 arrosage/jour; 2/jour en pot ou saison chaude.',
    'Rouille, pucerons, acariens',
    'Cultiver en bordure ou bac car elle s etend vite; couper regulierement.',
    TRUE
FROM culture c
WHERE c.nom = 'Menthe'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a septembre',
    '90 a 120 jours puis coupes',
    'Sol leger tres draine, eviter exces d eau, plein soleil.',
    'Semis 1 a 2 kg/ha ou boutures',
    'Compost faible dose, pas trop d azote',
    'Arrosage leger 3 a 4 fois/semaine au debut; ensuite 1 a 2/semaine.',
    'Pourriture racinaire, pucerons',
    'Tailler apres recolte; preferer pot ou planche bien drainee.',
    TRUE
FROM culture c
WHERE c.nom = 'Thym'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a septembre',
    '4 a 6 mois pour premiere coupe',
    'Boutures en sol draine, plein soleil, eviter humidite stagnante.',
    'Boutures: 8000 a 12000 plants/ha',
    'Compost faible dose',
    'Arrosage 3 fois/semaine au demarrage; ensuite 1 a 2/semaine.',
    'Pourriture racinaire, cochenilles',
    'Ne pas trop arroser; tailler pour ramifier.',
    TRUE
FROM culture c
WHERE c.nom = 'Romarin'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre ou toute saison humide',
    '45 a 70 jours',
    'Semis direct ou division, sol frais riche en compost.',
    '4 a 8 kg/ha',
    'Compost, apport organique apres coupe',
    '1 arrosage/jour; 2/jour en chaleur seche.',
    'Pucerons, limaces, taches foliaires',
    'Recolter feuilles jeunes; couper les hampes florales pour prolonger la production.',
    TRUE
FROM culture c
WHERE c.nom = 'Oseille'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '70 a 100 jours',
    'Sol profond, semis direct, eclaircissage, plein soleil.',
    '4 a 6 kg/ha',
    'Compost, engrais organique modere',
    '1 arrosage/jour jusqu a levee; ensuite 3 a 4/semaine.',
    'Pucerons, chenilles, fonte des semis',
    'Eviter transplantation tardive; recolter feuilles progressivement.',
    TRUE
FROM culture c
WHERE c.nom = 'Fenouil feuille'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a aout',
    '40 a 60 jours feuilles, 90 jours graines',
    'Semis direct, sol leger, soleil doux.',
    '5 a 8 kg/ha',
    'Compost leger, pas trop d azote',
    '1 arrosage/jour au debut; ensuite 3 a 4/semaine.',
    'Pucerons, fonte des semis, montaison rapide',
    'Semer en petites series; recolter avant floraison pour feuilles tendres.',
    TRUE
FROM culture c
WHERE c.nom = 'Aneth'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Novembre a mars ou avril a juin avec eau',
    '12 a 24 mois pour recoltes regulieres',
    'Plantation de jeunes plants en sol draine, mi-ombre possible.',
    'Plants: 1000 a 2000/ha selon conduite',
    'Compost au trou, fumier decompose annuel',
    'Arrosage 2 a 3 fois/semaine au demarrage; ensuite selon pluie.',
    'Cochenilles, fumagine, pourriture racinaire',
    'Tailler pour garder buisson; recolter feuilles adultes sans depouiller.',
    TRUE
FROM culture c
WHERE c.nom = 'Laurier sauce'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre',
    '30 a 45 jours',
    'Semis direct dense ou repiquage, sol riche et humide.',
    '300 a 500 g/ha',
    'Compost, petit apport azote organique',
    '1 arrosage/jour; 2/jour pendant levee si chaud.',
    'Altises, chenilles, pucerons',
    'Recolter en jeunes feuilles; proteger avec filet si attaques d altises.',
    TRUE
FROM culture c
WHERE c.nom = 'Tatsoi'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a janvier',
    '60 a 90 jours',
    'Semis en poquets, palissage conseille, sol riche et draine.',
    '4 a 6 kg/ha',
    'Compost au trou, NPK modere',
    '1 arrosage/jour au depart; ensuite 3 a 5/semaine.',
    'Mildiou, oïdium, pucerons, mouches des fruits',
    'Palissage facilite recolte; cueillir fruits jeunes avant jaunissement.',
    TRUE
FROM culture c
WHERE c.nom = 'Concombre amer Margose'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre; toute saison sous ombriere',
    '35 a 55 jours',
    'Pepiniere courte, repiquage sur planche riche et fraiche.',
    '300 a 500 g/ha',
    'Compost fin, engrais organique leger',
    '1 arrosage/jour; 2 petits/jour apres repiquage si chaud.',
    'Fonte des semis, limaces, pucerons, pourriture du collet',
    'Arroser le matin; eviter eau stagnante; recolter tot le matin pour fraicheur.',
    TRUE
FROM culture c
WHERE c.nom = 'Laitue batavia'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a octobre; septembre a janvier en zones chaudes',
    '75 a 100 jours',
    'Pepiniere, repiquage, tuteurage obligatoire, paillage conseille.',
    '80 a 150 g/ha',
    'Compost, NPK, apport potasse/calcium en fructification',
    '1 arrosage/jour apres repiquage; ensuite 3 a 5/semaine, regulier sans exces.',
    'Mildiou, alternariose, aleurodes, tuta absoluta, pourriture apicale',
    'Tailler legerement, tuteurer, eviter mouiller feuilles, recolter souvent.',
    TRUE
FROM culture c
WHERE c.nom = 'Tomate cerise'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Mars a septembre',
    '70 a 90 jours',
    'Sol tres fin, sans mottes, semis direct clair, eclaircissage.',
    '4 a 6 kg/ha',
    'Compost bien decompose; eviter fumier frais qui fourche les racines',
    'Arrosage leger quotidien jusqu a levee; ensuite 3 a 4/semaine.',
    'Alternariose, mouche de la carotte, nematodes',
    'Ne jamais repiquer; eclaircir tot pour racines droites.',
    TRUE
FROM culture c
WHERE c.nom = 'Carotte courte'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a decembre; possible avril a juin sous irrigation',
    '70 a 90 jours',
    'Semis direct en lignes, sol riche, bonne exposition soleil.',
    '15 a 25 kg/ha',
    'Compost/fumier, NPK au semis, uree legere a 30 jours',
    '1 arrosage/jour jusqu a levee si sec; ensuite 2 a 3/semaine, important a floraison.',
    'Chenilles legionnaires, foreurs, rouille',
    'Planter en blocs pour bonne pollinisation; recolter quand grains sont laiteux.',
    TRUE
FROM culture c
WHERE c.nom = 'Maïs doux'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a mars; toute saison avec eau',
    '30 a 45 jours pour premieres feuilles',
    'Planter boutures de lianes sur billons, sol meuble et humide.',
    '25000 a 33000 boutures/ha',
    'Compost au billon si disponible',
    'Arrosage 2 a 3 fois/semaine au demarrage; ensuite selon pluie.',
    'Charancons, chenilles, taches foliaires',
    'Prelever seulement les jeunes pousses pour ne pas reduire fortement les tubercules.',
    TRUE
FROM culture c
WHERE c.nom = 'Patate douce feuilles'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Octobre a janvier',
    '50 a 70 jours',
    'Semis direct, tuteurage ou treille, sol draine.',
    '15 a 25 kg/ha',
    'Compost, peu d azote, phosphore utile',
    '1 arrosage/jour jusqu a levee; ensuite 3 a 4/semaine.',
    'Pucerons, thrips, anthracnose',
    'Cueillir gousses jeunes et longues tous les 2 jours.',
    TRUE
FROM culture c
WHERE c.nom = 'Dolique asperge'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);

INSERT INTO fiche_culture(
    id_culture, periode_plantation, duree_avant_recolte, preparation_sol, quantite_semence, engrais_recommandes, arrosage, maladies_courantes, conseils_pratiques, valide
)
SELECT c.id_culture,
    'Avril a juin',
    '90 a 120 jours',
    'Semis direct en sol frais et profond, lignes espacees.',
    '120 a 180 kg/ha',
    'Compost, phosphore, eviter exces azote',
    '1 arrosage/jour au semis si sec; ensuite 3/semaine, important a floraison.',
    'Pucerons noirs, rouille, botrytis',
    'Pincer sommets si pucerons; recolter gousses pleines encore vertes.',
    TRUE
FROM culture c
WHERE c.nom = 'Fève'
AND NOT EXISTS (SELECT 1 FROM fiche_culture f WHERE f.id_culture = c.id_culture);
-- =====================================================================
-- VolySaina+ — Jeu de données cohérent (1er janvier 2026 → 14 juillet 2026)
-- À exécuter APRÈS le script de création de schéma (voly_saina).
-- Respecte les règles métier du document "Logique Métier" :
--   - cycle de vie réservation / facture / paiement / retour / maintenance
--   - formules de calcul (prix, pénalités)
--   - décrémentation de stock + traçabilité des mouvements
-- Les mots de passe sont des hachages bcrypt PLACEHOLDER (non réels).
-- Les montants sont en Ariary (Ar).
-- =====================================================================

SET search_path TO voly_saina;

-- =====================================================================
-- 1. UTILISATEURS SUPPLÉMENTAIRES (staff + clients)
-- =====================================================================

INSERT INTO utilisateur(nom, telephone, email, mot_de_passe, id_role, id_statut_compte, date_creation) VALUES
('Solofo', '+261340000001', 'solofo@volysaina.mg', '123', 2, 1, '2026-01-02'),  -- gestionnaire
('Fenitra', '+261340000002', 'fenitra@volysaina.mg', '123', 4, 1, '2026-01-02'), -- employé
('Njaka', '+261340000003', 'njaka@volysaina.mg', '123', 4, 1, '2026-01-02');   -- employé

INSERT INTO utilisateur(nom, telephone, email, mot_de_passe, id_role, id_statut_compte, date_creation) VALUES
('Rasoa',     '+261341000001', 'rasoa@mail.mg',     '123', 1, 1, '2026-01-03'),
('Andry',     '+261341000002', 'andry@mail.mg',     '123', 1, 1, '2026-01-04'),
('Voahangy',  '+261341000003', 'voahangy@mail.mg',  '123', 1, 1, '2026-01-05'),
('Tojo',      '+261341000004', 'tojo@mail.mg',      '123', 1, 1, '2026-01-08'),
('Nirina',    '+261341000005', 'nirina@mail.mg',    '123', 1, 1, '2026-01-15'),
('Fanja',     '+261341000006', 'fanja@mail.mg',     '123', 1, 1, '2026-01-20'),
('Hery',      '+261341000007', 'hery@mail.mg',      '123', 1, 1, '2026-02-01'),
('Miora',     '+261341000008', 'miora@mail.mg',     '123', 1, 1, '2026-02-05'),
('Tiana',     '+261341000009', 'tiana@mail.mg',     '123', 1, 1, '2026-02-10'),
('Faly',      '+261341000010', 'faly@mail.mg',      '123', 1, 1, '2026-01-02'),
('Vola',      '+261341000011', 'vola@mail.mg',      '123', 1, 1, '2026-03-01'),
('Zo',        '+261341000012', 'zo@mail.mg',        '123', 1, 1, '2026-04-01'),
('Lala',      '+261341000013', 'lala@mail.mg',      '123', 1, 1, '2026-05-01'),
('Dina',      '+261341000014', 'dina@mail.mg',      '123', 1, 3, '2026-03-10'); -- compte bloqué

-- Profils utilisateur (création partielle, comme dans la vraie logique "lazy creation")
INSERT INTO profil_utilisateur(id_utilisateur, genre, age, csp, localisation, niveau_connexion, date_mise_a_jour)
SELECT id_utilisateur, v.genre, v.age, v.csp, v.localisation, v.niveau, CURRENT_TIMESTAMP
FROM utilisateur u
JOIN (VALUES
  ('rasoa@mail.mg','F',34,'Agriculteur','Antsirabe','bonne'),
  ('andry@mail.mg','M',41,'Exploitant agricole','Antananarivo','bonne'),
  ('voahangy@mail.mg','F',29,'Commerçante','Fianarantsoa','faible'),
  ('tojo@mail.mg','M',52,'Agriculteur','Antsirabe','aucune'),
  ('nirina@mail.mg','F',37,'Coopérative agricole','Antananarivo','bonne'),
  ('hery@mail.mg','M',45,'Agriculteur','Toamasina','faible'),
  ('miora@mail.mg','F',26,'Étudiante en agronomie','Antananarivo','bonne'),
  ('faly@mail.mg','M',60,'Agriculteur','Antsirabe','aucune')
) AS v(email, genre, age, csp, localisation, niveau) ON u.email = v.email;

-- =====================================================================
-- 2. MACHINES SUPPLÉMENTAIRES
-- =====================================================================

INSERT INTO machine(nom, id_type_machine, description, prix_jour, localisation, disponible, date_creation) VALUES
('Tracteur 90CV', 1, 'Tracteur polyvalent moyen tonnage', 130000, 'Zone pilote', TRUE, '2026-01-02'),
('Motoculteur 12CV', 2, 'Motoculteur léger pour petites parcelles', 60000, 'Zone pilote', TRUE, '2026-01-02');

INSERT INTO statut_machine(id_machine, id_etat_machine, date_creation)
SELECT id_machine, 1, '2026-01-02' FROM machine WHERE nom IN ('Tracteur 90CV', 'Motoculteur 12CV');

-- =====================================================================
-- 3. PRODUITS SUPPLÉMENTAIRES
-- =====================================================================

INSERT INTO produit(id_categorie, nom, description, conseil_usage, prix_unitaire, stock, seuil_stock) VALUES
(2, 'Huile moteur', 'Huile moteur pour entretien des machines agricoles', 'Vidange toutes les 200 heures d''utilisation.', 15000, 60, 10),
(3, 'Semences maïs', 'Semences de maïs sélectionnées', 'Semer en début de saison des pluies.', 8000, 200, 30);

-- =====================================================================
-- 4. FONCTIONS UTILITAIRES DE SEED (encodent la logique métier)
-- =====================================================================

-- 4.1 Réservation complète (réservation -> facture -> paiement -> retour)
CREATE OR REPLACE FUNCTION voly_saina.fn_seed_reservation(
    p_client_email TEXT,
    p_machine_nom TEXT,
    p_date_debut DATE,
    p_date_fin DATE,
    p_final_status TEXT,              -- 'en_attente' | 'validee' | 'en_cours' | 'terminee' | 'annulee'
    p_etat_retour TEXT DEFAULT NULL,  -- 'bon' | 'use' | 'endommage' | 'casse' | 'perdu'
    p_date_retour DATE DEFAULT NULL,
    p_lieu TEXT DEFAULT 'Zone pilote'
) RETURNS INT AS $$
DECLARE
    v_client_id INT;
    v_machine_id INT;
    v_prix_jour NUMERIC;
    v_jours INT;
    v_prix_total NUMERIC;
    v_reservation_id INT;
    v_statut_id INT;
    v_facture_id INT;
    v_date_creation_res TIMESTAMP;
    v_date_limite DATE;
    v_penalite_retard NUMERIC := 0;
    v_penalite_etat NUMERIC := 0;
    v_jours_retard INT := 0;
BEGIN
    SELECT id_utilisateur INTO v_client_id FROM voly_saina.utilisateur WHERE email = p_client_email;
    SELECT id_machine, prix_jour INTO v_machine_id, v_prix_jour FROM voly_saina.machine WHERE nom = p_machine_nom;

    -- Formule : prixTotal = prixJour * max(jours,1)
    v_jours := GREATEST((p_date_fin - p_date_debut), 1);
    v_prix_total := v_prix_jour * v_jours;
    v_date_creation_res := (p_date_debut - INTERVAL '4 days');

    SELECT id_statut_reservation INTO v_statut_id FROM voly_saina.statut_reservation WHERE code = p_final_status;

    INSERT INTO voly_saina.reservation_machine
        (id_client, id_machine, date_debut, date_fin, lieu_livraison, prix_total, id_statut_reservation, date_creation)
    VALUES (v_client_id, v_machine_id, p_date_debut, p_date_fin, p_lieu, v_prix_total, v_statut_id, v_date_creation_res)
    RETURNING id_reservation INTO v_reservation_id;

    -- Une facture est créée dès que la réservation quitte "en_attente"
    IF p_final_status <> 'en_attente' THEN
        v_facture_id := nextval('voly_saina.facture_id_facture_seq');
        v_date_limite := (v_date_creation_res + INTERVAL '14 days')::date;

        INSERT INTO voly_saina.facture
            (id_facture, numero, type_operation, id_client, date_facture, montant_total, montant_paye, id_statut_facture, date_limite)
        VALUES (
            v_facture_id,
            'FAC-2026-' || v_facture_id || '-' || lpad(v_facture_id::text, 4, '0'),
            'location',
            v_client_id,
            v_date_creation_res,
            v_prix_total,
            CASE WHEN p_final_status IN ('en_cours', 'terminee') THEN v_prix_total ELSE 0 END,
            (SELECT id_statut_facture FROM voly_saina.statut_facture WHERE code =
                CASE
                    WHEN p_final_status = 'annulee' THEN 'annulee'
                    WHEN p_final_status IN ('en_cours', 'terminee') THEN 'payee'
                    ELSE 'en_attente'
                END),
            v_date_limite
        );

        UPDATE voly_saina.reservation_machine SET id_facture = v_facture_id WHERE id_reservation = v_reservation_id;

        IF p_final_status IN ('en_cours', 'terminee') THEN
            INSERT INTO voly_saina.paiement(id_facture, montant, date_paiement, reference, id_mode_paiement)
            VALUES (v_facture_id, v_prix_total, v_date_creation_res + INTERVAL '1 day', 'PAY-RES-' || v_reservation_id,
                    (SELECT id_mode_paiement FROM voly_saina.mode_paiement WHERE libelle = 'Espèces'));
        END IF;
    END IF;

    -- Gestion de l'état machine + retour
    IF p_final_status = 'terminee' THEN
        IF p_date_retour > p_date_fin THEN
            v_jours_retard := p_date_retour - p_date_fin;
            v_penalite_retard := v_prix_jour * v_jours_retard * 1.5;  -- pénalité de retard
        END IF;

        v_penalite_etat := CASE p_etat_retour
            WHEN 'bon' THEN 0
            WHEN 'use' THEN 2 * v_prix_jour
            WHEN 'endommage' THEN 5 * v_prix_jour
            WHEN 'casse' THEN 15 * v_prix_jour
            WHEN 'perdu' THEN 30 * v_prix_jour
            ELSE 0
        END;

        INSERT INTO voly_saina.retour_machine(id_reservation, date_retour, etat_retour, penalite)
        VALUES (v_reservation_id, p_date_retour, p_etat_retour, v_penalite_retard + v_penalite_etat);

        UPDATE voly_saina.machine SET disponible = TRUE WHERE id_machine = v_machine_id;

    ELSIF p_final_status = 'en_cours' THEN
        UPDATE voly_saina.machine SET disponible = FALSE WHERE id_machine = v_machine_id;
        INSERT INTO voly_saina.statut_machine(id_machine, id_etat_machine, date_creation)
        VALUES (v_machine_id, (SELECT id_etat_machine FROM voly_saina.etat_machine WHERE code = 'louee'), p_date_debut);
    END IF;

    RETURN v_reservation_id;
END;
$$ LANGUAGE plpgsql;

-- 4.2 Commande via panier (panier -> commande -> lignes -> stock -> facture -> paiement)
CREATE OR REPLACE FUNCTION voly_saina.fn_seed_commande(
    p_client_email TEXT,
    p_date DATE,
    p_lignes JSONB,     -- ex: '[{"produit":"NPK","quantite":5}]'
    p_payee BOOLEAN
) RETURNS INT AS $$
DECLARE
    v_client_id INT;
    v_panier_id INT;
    v_commande_id INT;
    v_facture_id INT;
    v_montant_total NUMERIC := 0;
    v_ligne JSONB;
    v_produit_id INT;
    v_prix_unitaire NUMERIC;
    v_quantite NUMERIC;
    v_sous_total NUMERIC;
BEGIN
    SELECT id_utilisateur INTO v_client_id FROM voly_saina.utilisateur WHERE email = p_client_email;

    INSERT INTO voly_saina.panier(id_client, actif, date_creation)
    VALUES (v_client_id, FALSE, p_date)
    RETURNING id_panier INTO v_panier_id;

    INSERT INTO voly_saina.commande(id_client, date_commande, adresse_livraison, id_mode_paiement, id_statut_commande, montant_total)
    VALUES (v_client_id, p_date, 'Adresse client - zone pilote',
            (SELECT id_mode_paiement FROM voly_saina.mode_paiement WHERE libelle = 'Espèces'),
            (SELECT id_statut_commande FROM voly_saina.statut_commande WHERE code = CASE WHEN p_payee THEN 'livree' ELSE 'en_attente' END),
            0)
    RETURNING id_commande INTO v_commande_id;

    FOR v_ligne IN SELECT * FROM jsonb_array_elements(p_lignes)
    LOOP
        SELECT id_produit, prix_unitaire INTO v_produit_id, v_prix_unitaire
        FROM voly_saina.produit WHERE nom = v_ligne ->> 'produit';

        v_quantite := (v_ligne ->> 'quantite')::NUMERIC;
        v_sous_total := v_quantite * v_prix_unitaire;
        v_montant_total := v_montant_total + v_sous_total;

        INSERT INTO voly_saina.ligne_commande(id_commande, id_produit, quantite, prix_unitaire, sous_total)
        VALUES (v_commande_id, v_produit_id, v_quantite, v_prix_unitaire, v_sous_total);

        -- Décrémentation du stock + traçabilité
        UPDATE voly_saina.produit SET stock = stock - v_quantite WHERE id_produit = v_produit_id;

        INSERT INTO voly_saina.mouvement_stock(id_produit, id_type_mouvement, quantite, motif, date_mouvement)
        VALUES (v_produit_id, (SELECT id_type_mouvement FROM voly_saina.type_mouvement_stock WHERE code = 'sortie'),
                v_quantite, 'Vente - commande #' || v_commande_id, p_date);
    END LOOP;

    UPDATE voly_saina.commande SET montant_total = v_montant_total WHERE id_commande = v_commande_id;

    INSERT INTO voly_saina.panier_details(id_panier, id_commande) VALUES (v_panier_id, v_commande_id);

    v_facture_id := nextval('voly_saina.facture_id_facture_seq');
    INSERT INTO voly_saina.facture
        (id_facture, numero, type_operation, id_client, date_facture, montant_total, montant_paye, id_statut_facture, id_panier, date_limite)
    VALUES (
        v_facture_id,
        'FAC-2026-' || v_facture_id || '-' || lpad(v_facture_id::text, 4, '0'),
        'commande',
        v_client_id,
        p_date,
        v_montant_total,
        CASE WHEN p_payee THEN v_montant_total ELSE 0 END,
        (SELECT id_statut_facture FROM voly_saina.statut_facture WHERE code = CASE WHEN p_payee THEN 'payee' ELSE 'en_attente' END),
        v_panier_id,
        (p_date + INTERVAL '14 days')::date
    );

    IF p_payee THEN
        INSERT INTO voly_saina.paiement(id_facture, montant, date_paiement, reference, id_mode_paiement)
        VALUES (v_facture_id, v_montant_total, p_date + INTERVAL '1 day', 'PAY-CMD-' || v_commande_id,
                (SELECT id_mode_paiement FROM voly_saina.mode_paiement WHERE libelle = 'Espèces'));
    END IF;

    RETURN v_commande_id;
END;
$$ LANGUAGE plpgsql;

-- 4.3 Maintenance (mise hors service temporaire + retour en service)
CREATE OR REPLACE FUNCTION voly_saina.fn_seed_maintenance(
    p_machine_nom TEXT,
    p_date_debut DATE,
    p_date_retour_prevue DATE,
    p_date_retour_reelle DATE,
    p_cout NUMERIC,
    p_travaux TEXT
) RETURNS VOID AS $$
DECLARE
    v_machine_id INT;
BEGIN
    SELECT id_machine INTO v_machine_id FROM voly_saina.machine WHERE nom = p_machine_nom;

    INSERT INTO voly_saina.maintenance_machine
        (id_machine, date_debut, date_retour_prevue, date_retour_reelle, cout, travaux, id_statut_maintenance)
    VALUES (v_machine_id, p_date_debut, p_date_retour_prevue, p_date_retour_reelle, p_cout, p_travaux,
            (SELECT id_statut_maintenance FROM voly_saina.statut_maintenance WHERE code = 'terminee'));

    INSERT INTO voly_saina.statut_machine(id_machine, id_etat_machine, date_creation)
    VALUES (v_machine_id, (SELECT id_etat_machine FROM voly_saina.etat_machine WHERE code = 'maintenance'), p_date_debut);

    INSERT INTO voly_saina.statut_machine(id_machine, id_etat_machine, date_creation)
    VALUES (v_machine_id, (SELECT id_etat_machine FROM voly_saina.etat_machine WHERE code = 'disponible'), p_date_retour_reelle);

    UPDATE voly_saina.machine SET disponible = TRUE WHERE id_machine = v_machine_id;
END;
$$ LANGUAGE plpgsql;

-- =====================================================================
-- 5. RÉSERVATIONS (ordre chronologique — évite tout chevauchement de dates par machine)
-- =====================================================================

SELECT fn_seed_reservation('faly@mail.mg',     'Pulvérisateur agricole', '2026-01-05', '2026-01-06', 'terminee', 'bon',       '2026-01-06');
SELECT fn_seed_reservation('rasoa@mail.mg',    'Tracteur standard',      '2026-01-10', '2026-01-15', 'terminee', 'bon',       '2026-01-15');
SELECT fn_seed_reservation('voahangy@mail.mg', 'Motoculteur 18CV',       '2026-01-20', '2026-01-22', 'terminee', 'bon',       '2026-01-22');
SELECT fn_seed_reservation('andry@mail.mg',    'Remorque agricole',      '2026-02-01', '2026-02-04', 'terminee', 'bon',       '2026-02-04');

SELECT fn_seed_maintenance('Remorque agricole', '2026-02-10', '2026-02-17', '2026-02-17', 180000, 'Révision générale + changement de pneus');

SELECT fn_seed_reservation('miora@mail.mg',    'Motoculteur 12CV',       '2026-02-10', '2026-02-13', 'terminee', 'use',       '2026-02-14');
SELECT fn_seed_reservation('nirina@mail.mg',   'Motoculteur 18CV',       '2026-02-15', '2026-02-18', 'annulee');
SELECT fn_seed_reservation('andry@mail.mg',    'Tracteur standard',      '2026-03-01', '2026-03-05', 'terminee', 'use',       '2026-03-06');
SELECT fn_seed_reservation('vola@mail.mg',     'Pulvérisateur agricole', '2026-03-20', '2026-03-22', 'terminee', 'casse',     '2026-03-22');

SELECT fn_seed_maintenance('Pulvérisateur agricole', '2026-04-01', '2026-04-08', '2026-04-08', 220000, 'Réparation suite à casse + entretien buses');

SELECT fn_seed_reservation('hery@mail.mg',     'Motoculteur 18CV',       '2026-04-05', '2026-04-10', 'terminee', 'endommage', '2026-04-11');
SELECT fn_seed_reservation('tojo@mail.mg',     'Remorque agricole',      '2026-04-20', '2026-04-25', 'terminee', 'perdu',     '2026-04-25');
SELECT fn_seed_reservation('nirina@mail.mg',   'Tracteur 90CV',          '2026-05-01', '2026-05-06', 'terminee', 'bon',       '2026-05-06');
SELECT fn_seed_reservation('zo@mail.mg',       'Pulvérisateur agricole', '2026-05-10', '2026-05-12', 'terminee', 'bon',       '2026-05-12');

SELECT fn_seed_maintenance('Tracteur 90CV', '2026-06-01', '2026-06-08', '2026-06-08', 150000, 'Vidange + révision moteur');

SELECT fn_seed_reservation('tojo@mail.mg',     'Tracteur standard',      '2026-06-01', '2026-06-10', 'terminee', 'bon',       '2026-06-10');
SELECT fn_seed_reservation('fanja@mail.mg',    'Remorque agricole',      '2026-06-15', '2026-06-18', 'terminee', 'bon',       '2026-06-18');
SELECT fn_seed_reservation('tiana@mail.mg',    'Motoculteur 12CV',       '2026-06-25', '2026-06-28', 'terminee', 'bon',       '2026-06-28');
SELECT fn_seed_reservation('lala@mail.mg',     'Pulvérisateur agricole', '2026-07-01', '2026-07-08', 'terminee', 'bon',       '2026-07-10');
SELECT fn_seed_reservation('miora@mail.mg',    'Motoculteur 18CV',       '2026-07-05', '2026-07-12', 'terminee', 'bon',       '2026-07-12');

-- Réservations en cours / à venir à la date du jour (14 juillet 2026)
SELECT fn_seed_reservation('fanja@mail.mg',    'Tracteur standard',      '2026-07-10', '2026-07-20', 'en_cours');
SELECT fn_seed_reservation('rasoa@mail.mg',    'Pulvérisateur agricole', '2026-07-12', '2026-07-18', 'en_cours');
SELECT fn_seed_reservation('tiana@mail.mg',    'Motoculteur 18CV',       '2026-07-13', '2026-07-25', 'en_cours');
SELECT fn_seed_reservation('faly@mail.mg',     'Motoculteur 12CV',       '2026-07-14', '2026-07-16', 'validee');
SELECT fn_seed_reservation('voahangy@mail.mg', 'Remorque agricole',      '2026-07-16', '2026-07-19', 'en_attente');
SELECT fn_seed_reservation('hery@mail.mg',     'Tracteur 90CV',          '2026-07-15', '2026-07-18', 'en_attente');

-- =====================================================================
-- 6. COMMANDES (produits) — via panier
-- =====================================================================

SELECT fn_seed_commande('rasoa@mail.mg',    '2026-01-12', '[{"produit":"NPK","quantite":5},{"produit":"Compost","quantite":3}]'::jsonb, TRUE);
SELECT fn_seed_commande('andry@mail.mg',    '2026-02-08', '[{"produit":"Urée","quantite":10}]'::jsonb, TRUE);
SELECT fn_seed_commande('voahangy@mail.mg', '2026-03-15', '[{"produit":"Huile moteur","quantite":2},{"produit":"NPK","quantite":2}]'::jsonb, TRUE);
SELECT fn_seed_commande('tojo@mail.mg',     '2026-04-22', '[{"produit":"Semences maïs","quantite":20}]'::jsonb, TRUE);
SELECT fn_seed_commande('nirina@mail.mg',   '2026-05-18', '[{"produit":"Compost","quantite":8},{"produit":"Urée","quantite":4}]'::jsonb, TRUE);
SELECT fn_seed_commande('fanja@mail.mg',    '2026-06-10', '[{"produit":"NPK","quantite":6}]'::jsonb, TRUE);
SELECT fn_seed_commande('hery@mail.mg',     '2026-07-02', '[{"produit":"Huile moteur","quantite":3},{"produit":"Semences maïs","quantite":10}]'::jsonb, TRUE);
SELECT fn_seed_commande('miora@mail.mg',    '2026-07-13', '[{"produit":"NPK","quantite":4}]'::jsonb, FALSE); -- pas encore payée

-- =====================================================================
-- 7. NOTES CLIENTS
-- =====================================================================

INSERT INTO note_client(id_client, note, date_note)
SELECT id_utilisateur, v.note, v.date_note::timestamp FROM utilisateur u JOIN (VALUES
  ('rasoa@mail.mg',    'Très bon service, tracteur en excellent état.', '2026-01-16'),
  ('andry@mail.mg',    'Livraison un peu en retard mais matériel correct.', '2026-03-07'),
  ('vola@mail.mg',     'Déçue par la casse du pulvérisateur, mais bonne prise en charge du service client.', '2026-03-25'),
  ('fanja@mail.mg',    'Remorque très pratique pour le transport de la récolte.', '2026-06-19'),
  ('hery@mail.mg',     'Bon accompagnement pour la commande d''engrais.', '2026-07-03')
) AS v(email, note, date_note) ON u.email = v.email;

-- =====================================================================
-- 8. TÂCHES EMPLOYÉS
-- =====================================================================

INSERT INTO tache_employe(id_employe, description, date_limite, id_statut_tache, id_reservation, date_creation)
SELECT (SELECT id_utilisateur FROM utilisateur WHERE email = 'fenitra@volysaina.mg'),
       'Vérifier l''état du tracteur standard après retour',
       '2026-01-16',
       (SELECT id_statut_tache FROM statut_tache WHERE code = 'terminee'),
       (SELECT id_reservation FROM reservation_machine rm JOIN machine m ON rm.id_machine = m.id_machine
            WHERE m.nom = 'Tracteur standard' AND rm.date_debut = '2026-01-10'),
       '2026-01-15';

INSERT INTO tache_employe(id_employe, description, date_limite, id_statut_tache, id_commande, date_creation)
SELECT (SELECT id_utilisateur FROM utilisateur WHERE email = 'njaka@volysaina.mg'),
       'Préparer la livraison des semences de maïs',
       '2026-04-23',
       (SELECT id_statut_tache FROM statut_tache WHERE code = 'terminee'),
       (SELECT id_commande FROM commande WHERE id_client = (SELECT id_utilisateur FROM utilisateur WHERE email = 'tojo@mail.mg') AND date_commande = '2026-04-22'),
       '2026-04-22';

INSERT INTO tache_employe(id_employe, description, date_limite, id_statut_tache, id_reservation, date_creation)
SELECT (SELECT id_utilisateur FROM utilisateur WHERE email = 'fenitra@volysaina.mg'),
       'Contrôler le kilométrage du tracteur en cours de location',
       '2026-07-21',
       (SELECT id_statut_tache FROM statut_tache WHERE code = 'en_cours'),
       (SELECT id_reservation FROM reservation_machine rm JOIN machine m ON rm.id_machine = m.id_machine
            WHERE m.nom = 'Tracteur standard' AND rm.date_debut = '2026-07-10'),
       '2026-07-10';

INSERT INTO tache_employe(id_employe, description, date_limite, id_statut_tache, id_reservation, date_creation)
SELECT (SELECT id_utilisateur FROM utilisateur WHERE email = 'njaka@volysaina.mg'),
       'Traiter le dossier de pénalité — retour tardif du pulvérisateur',
       '2026-07-15',
       (SELECT id_statut_tache FROM statut_tache WHERE code = 'a_faire'),
       (SELECT id_reservation FROM reservation_machine rm JOIN machine m ON rm.id_machine = m.id_machine
            WHERE m.nom = 'Pulvérisateur agricole' AND rm.date_debut = '2026-07-01'),
       '2026-07-10';

-- =====================================================================
-- 9. PRÊT BANCAIRE + REMBOURSEMENTS
-- =====================================================================

INSERT INTO pret_bancaire(banque, montant, duree_mois, taux_interet, date_debut, id_statut_pret)
VALUES ('Bank of Africa Madagascar', 50000000, 24, 12.5, '2026-01-15',
        (SELECT id_statut_pret FROM statut_pret WHERE code = 'en_cours'));

INSERT INTO remboursement_pret(id_pret, montant, date_remboursement, reference_bancaire)
SELECT (SELECT id_pret FROM pret_bancaire WHERE banque = 'Bank of Africa Madagascar'), 2400000, d, 'VIR-' || to_char(d, 'YYYYMM')
FROM (VALUES ('2026-02-15'::date), ('2026-03-15'::date), ('2026-04-15'::date),
             ('2026-05-15'::date), ('2026-06-15'::date), ('2026-07-15'::date)) AS t(d);

-- =====================================================================
-- 10. RAPPORTS
-- =====================================================================

INSERT INTO rapport(type_rapport, periode_debut, periode_fin, contenu, date_generation) VALUES
('bilan_trimestriel', '2026-01-01', '2026-03-31',
 '{"reservations_terminees": 6, "reservations_annulees": 1, "machines_en_maintenance": 1}'::jsonb, '2026-04-02'),
('bilan_trimestriel', '2026-04-01', '2026-06-30',
 '{"reservations_terminees": 6, "reservations_annulees": 0, "machines_en_maintenance": 2}'::jsonb, '2026-07-02');

-- =====================================================================
-- 11. VÉRIFICATIONS RAPIDES (facultatif — à exécuter manuellement)
-- =====================================================================
-- SELECT code, count(*) FROM reservation_machine rm JOIN statut_reservation sr ON rm.id_statut_reservation = sr.id_statut_reservation GROUP BY code;
-- SELECT code, count(*) FROM facture f JOIN statut_facture sf ON f.id_statut_facture = sf.id_statut_facture GROUP BY code;
-- SELECT nom, stock FROM produit;
-- SELECT nom, disponible FROM machine;