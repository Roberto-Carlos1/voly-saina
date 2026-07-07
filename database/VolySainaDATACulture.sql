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
