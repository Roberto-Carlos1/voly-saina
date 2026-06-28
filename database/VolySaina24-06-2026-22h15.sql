-- Migration SQL pour Voly Saina+
-- Date: 24-06-2026
-- Ajout de la colonne id_operation et données de test pour les pages factures et statistiques

-- Ajout de la colonne id_operation à la table facture
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'voly_saina' 
        AND table_name = 'facture' 
        AND column_name = 'id_operation'
    ) THEN
        ALTER TABLE voly_saina.facture ADD COLUMN id_operation BIGINT;
        RAISE NOTICE 'Colonne id_operation ajoutée à la table facture';
    ELSE
        RAISE NOTICE 'Colonne id_operation existe déjà';
    END IF;
END $$;

-- Données de test pour les factures
-- Pour le client 1 uniquement (id_client=1 existe déjà)
-- Statuts: 1=en_attente, 2=payee, 3=partiellement_payee, 4=en_retard, 5=annulee

INSERT INTO voly_saina.facture (numero, type_operation, id_operation, id_client, date_facture, montant_total, montant_paye, id_statut_facture, date_limite)
VALUES 
    ('FAC-2024-001', 'location', 1, 1, '2024-01-15 10:00:00', 50000.00, 50000.00, 2, '2024-02-15'), -- payee (100%)
    ('FAC-2024-002', 'commande', 1, 1, '2024-02-10 14:30:00', 25000.00, 15000.00, 4, '2024-03-10'), -- en_retard (60%, date dépassée)
    ('FAC-2024-003', 'location', 2, 1, '2024-03-05 09:00:00', 75000.00, 0.00, 4, '2024-04-05'), -- en_retard (0%, date dépassée)
    ('FAC-2024-004', 'commande', 2, 1, '2024-04-12 11:00:00', 35000.00, 35000.00, 2, '2024-05-12'), -- payee (100%)
    ('FAC-2024-005', 'location', 3, 1, '2024-05-20 08:00:00', 60000.00, 30000.00, 4, '2024-06-20'), -- en_retard (50%, date dépassée)
    ('FAC-2024-006', 'commande', 3, 1, '2024-06-08 15:00:00', 45000.00, 0.00, 4, '2024-07-08'), -- en_retard (0%, date dépassée)
    ('FAC-2024-007', 'location', 4, 1, '2024-06-15 10:00:00', 80000.00, 40000.00, 4, '2024-07-15'), -- en_retard (50%, date dépassée)
    ('FAC-2024-008', 'commande', 4, 1, '2024-06-20 16:00:00', 20000.00, 20000.00, 2, '2024-07-20'), -- payee (100%)
    ('FAC-2024-009', 'location', 5, 1, '2026-06-25 10:00:00', 55000.00, 0.00, 1, '2026-08-25'), -- en_attente (0%, date future)
    ('FAC-2024-010', 'commande', 5, 1, '2026-06-26 14:30:00', 30000.00, 10000.00, 3, '2026-08-26') -- partiellement_payee (33%, date future)
ON CONFLICT (numero) DO NOTHING;

-- Données de test pour les réservations de machines (pour statistiques)
-- Pour le client 1 uniquement

INSERT INTO voly_saina.reservation_machine (id_client, id_machine, date_debut, date_fin, prix_total, id_statut_reservation)
VALUES 
    (1, 1, '2024-01-15', '2024-01-20', 50000.00, 3), -- terminée
    (1, 2, '2024-02-01', '2024-02-05', 45000.00, 3), -- terminée
    (1, 1, '2024-03-05', '2024-03-10', 75000.00, 2), -- en_cours
    (1, 3, '2024-04-01', '2024-04-06', 60000.00, 3), -- terminée
    (1, 2, '2024-05-15', '2024-05-20', 55000.00, 3), -- terminée
    (1, 1, '2024-06-01', '2024-06-06', 50000.00, 1), -- validee
    (1, 3, '2024-06-10', '2024-06-15', 60000.00, 2), -- en_cours
    (1, 2, '2024-06-20', '2024-06-25', 45000.00, 1)  -- validee
ON CONFLICT DO NOTHING;

-- Données de test pour les commandes (pour statistiques)
-- Pour le client 1 uniquement

INSERT INTO voly_saina.commande (id_client, date_commande, montant_total, id_statut_commande)
VALUES 
    (1, '2024-01-10 10:00:00', 25000.00, 4), -- livree
    (1, '2024-02-15 14:00:00', 30000.00, 4), -- livree
    (1, '2024-03-20 11:00:00', 35000.00, 4), -- livree
    (1, '2024-04-25 09:00:00', 20000.00, 4), -- livree
    (1, '2024-05-30 15:00:00', 45000.00, 3), -- en_livraison
    (1, '2024-06-05 10:00:00', 28000.00, 4), -- livree
    (1, '2024-06-18 14:00:00', 32000.00, 2), -- preparee
    (1, '2024-06-22 11:00:00', 25000.00, 1)  -- validee
ON CONFLICT DO NOTHING;

-- Données de test pour les lignes de commande (pour statistiques des produits)
-- Pour le client 1 uniquement, produits 1, 2, 3 uniquement

INSERT INTO voly_saina.ligne_commande (id_commande, id_produit, quantite, prix_unitaire, sous_total)
VALUES 
    (1, 1, 10, 1500.00, 15000.00),
    (1, 2, 5, 2000.00, 10000.00),
    (2, 1, 15, 1500.00, 22500.00),
    (2, 3, 5, 1500.00, 7500.00),
    (3, 2, 10, 2000.00, 20000.00),
    (3, 3, 5, 3000.00, 15000.00),
    (4, 1, 8, 1500.00, 12000.00),
    (4, 3, 4, 2000.00, 8000.00),
    (5, 2, 12, 2000.00, 24000.00),
    (5, 3, 7, 3000.00, 21000.00),
    (6, 1, 10, 1500.00, 15000.00),
    (6, 3, 6, 2166.67, 13000.00),
    (7, 2, 8, 2000.00, 16000.00),
    (7, 3, 5, 3200.00, 16000.00),
    (8, 1, 10, 1500.00, 15000.00),
    (8, 2, 5, 2000.00, 10000.00)
ON CONFLICT DO NOTHING;

-- Mise à jour des statuts de facture pour correspondre aux codes attendus
-- 1 = payee, 2 = partiellement_payee, 3 = en_attente, 4 = en_retard, 5 = annulee
-- Assurez-vous que ces statuts existent dans la table statut_facture

-- Note: Les données ci-dessus supposent que les tables suivantes existent avec des données valides:
-- - utilisateur (id_utilisateur 1 et 2)
-- - machine (id_machine 1, 2, 3)
-- - produit (id_produit 1, 2, 3, 4)
-- - statut_facture (id_statut_facture 1-5)
-- - statut_reservation (id_statut_reservation 1-3)
-- - statut_commande (id_statut_commande 1-4)

-- Si nécessaire, ajustez les IDs selon votre base de données existante
