-- Aleter commade and reservation machine to add id_facture
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='commande' AND column_name='id_facture') THEN
        ALTER TABLE commande ADD COLUMN id_facture INT REFERENCES facture(id_facture) ON DELETE SET NULL;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='reservation_machine' AND column_name='id_facture') THEN
        ALTER TABLE reservation_machine ADD COLUMN id_facture INT REFERENCES facture(id_facture) ON DELETE SET NULL;
    END IF;
END $$; 


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
JOIN voly_saina.commande c ON c.id_facture = f.id_facture
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
JOIN voly_saina.reservation_machine rm ON rm.id_facture = f.id_facture
JOIN voly_saina.statut_reservation sr ON sr.id_statut_reservation = rm.id_statut_reservation;