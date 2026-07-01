create table panier(
    id_panier SERIAL PRIMARY KEY,
    id_client INT NOT NULL REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    id_commande INT REFERENCES commande(id_commande) ON DELETE CASCADE,
    id_reservation_machine INT REFERENCES reservation_machine(id_reservation) ON DELETE CASCADE,
    date_arret TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

