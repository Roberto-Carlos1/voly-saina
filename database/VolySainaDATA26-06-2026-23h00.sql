-- ===========================
-- DONNÉES DE TEST — MAINTENANCE_MACHINE
-- ===========================
-- Machines: 1=Tracteur standard, 2=Motoculteur 18CV, 3=Pulvérisateur agricole, 4=Remorque agricole
-- Statuts maintenance: 1=prevue, 2=en_cours, 3=terminee, 4=annulee

INSERT INTO voly_saina.maintenance_machine
(id_machine, date_debut, date_retour_prevue, date_retour_reelle, cout, travaux, id_statut_maintenance)
VALUES
    (1, '2026-01-10', '2026-01-17', '2026-01-16', 250000.00, 'Vidange moteur, remplacement filtres huile et air', 3),
    (1, '2026-03-05', '2026-03-12', '2026-03-15', 580000.00, 'Révision complète du moteur, remplacement courroie', 3),
    (2, '2026-02-01', '2026-02-08', '2026-02-07', 120000.00, 'Remplacement lame et réglage carburateur', 3),
    (2, '2026-04-15', '2026-04-22', NULL, 180000.00, 'Réparation système transmission', 2),
    (3, '2026-03-20', '2026-03-27', '2026-03-25', 95000.00, 'Nettoyage buse, remplacement joint pompe', 3),
    (3, '2026-05-10', '2026-05-17', NULL, 140000.00, 'Révision pompe haute pression', 2),
    (4, '2026-02-20', '2026-02-27', '2026-02-28', 75000.00, 'Soudure châssis, remplacement attelage', 3),
    (4, '2026-06-01', '2026-06-08', '2026-06-06', 85000.00, 'Remplacement pneus et freins', 3),
    (1, '2026-06-20', '2026-06-27', NULL, 320000.00, 'Révision moteur + changement embrayage', 1),
    (2, '2026-06-22', '2026-06-29', NULL, 95000.00, 'Entretien périodique, vidange', 1),
    (3, '2026-06-25', '2026-07-02', NULL, 60000.00, 'Nettoyage et calibrage pulvérisateur', 1),
    (4, '2026-05-01', '2026-05-08', '2026-05-03', 45000.00, 'Graissage et peinture anti-rouille', 3),
    (1, '2026-04-01', '2026-04-08', NULL, 0.00, 'Maintenance annulée faute de pièces', 4),
    (2, '2026-05-20', '2026-05-27', NULL, 210000.00, 'Changement moteur complet', 2)
ON CONFLICT DO NOTHING;
