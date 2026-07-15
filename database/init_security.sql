-- Script d'initialisation pour Spring Security
-- À exécuter sur la base de données existante

-- 1. S'assurer que les rôles existent
INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('client', 'Client')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('gestionnaire', 'Gestionnaire')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('responsable', 'Responsable')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.role_utilisateur (code, libelle) 
VALUES ('employe', 'Employé')
ON CONFLICT (code) DO NOTHING;

-- 2. S'assurer que les statuts existent
INSERT INTO voly_saina.statut_compte (code, libelle) 
VALUES ('actif', 'Actif')
ON CONFLICT (code) DO NOTHING;

INSERT INTO voly_saina.statut_compte (code, libelle) 
VALUES ('inactif', 'Inactif')
ON CONFLICT (code) DO NOTHING;
