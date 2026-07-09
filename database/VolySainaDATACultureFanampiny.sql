-- Cultures maraicheres et aromatiques courantes de Madagascar - complement marche quotidien
-- Compatible avec les tables culture et fiche_culture du projet voly_saina
SET search_path TO voly_saina;

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
