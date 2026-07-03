-- Script d'initialisation pour Spring Security
-- À exécuter sur la base de données existante

-- 1. S'assurer que les rôles existent
INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('CLIENT', 'Client')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('GESTIONNAIRE', 'Gestionnaire')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('RESPONSABLE', 'Responsable')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('EMPLOYE', 'Employé')
ON CONFLICT (code) DO NOTHING;

-- 2. S'assurer que les statuts existent
INSERT INTO voly_saina.statut_compte (code, libelle) 
VALUES ('ACTIF', 'Actif')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.statut_compte (code, libelle) 
VALUES ('INACTIF', 'Inactif')
ON CONFLICT (code) DO NOTHING;
