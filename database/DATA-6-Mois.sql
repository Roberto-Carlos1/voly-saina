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