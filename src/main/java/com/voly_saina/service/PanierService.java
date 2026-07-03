package com.voly_saina.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutCommande;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.PanierRepository;

@Service
public class PanierService {

    private static final String STATUT_PANIER = "en_attente";
    private static final String STATUT_LIVRAISON = "preparee";

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private StatutCommandeService statutCommandeService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private LigneCommandeService ligneCommandeService;

    @Autowired
    private PanierDetailsService panierDetailsService;

    @Autowired
    private CommandeClientService commandeClientService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private StatutReservationService statutReservationService;

    public List<Panier> findAll() {
        return panierRepository.findAll();
    }

    public Panier findById(Long id) {
        return panierRepository.findById(id).orElse(null);
    }

    public Panier save(Panier panier) {
        return panierRepository.save(panier);
    }

    public boolean existsById(Long id) {
        return panierRepository.existsById(id);
    }

    public void deleteById(Long id) {
        panierRepository.deleteById(id);
    }

    public Panier findCurrentPanierByIdClient(Long idClient) {
        Panier retour = panierRepository.findPanierActifPlusRecent(idClient);
        if (retour == null) {
            Utilisateur u = utilisateurService.findById(idClient).orElse(null);
            retour = new Panier();
            retour.setClient(u);
            retour.setActif(true);
            save(retour);
        }
        return retour;
    }
    public Panier cloturePanier(Long idClient) {
        Panier panier = findCurrentPanierByIdClient(idClient);
        if (panier != null) {
            panier.setActif(false);
            save(panier);
        }
        return panier;
    }

    public Panier findByClientId(Long clientId) {
        return panierRepository.findFirstByClientIdUtilisateurOrderByIdPanierDesc(clientId);
    }

    // ============ PRODUITS (commandes) ============

    public Commande ajouterAuPanier(Long clientId, Long produitId, BigDecimal quantite) {
        Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Produit produit = produitService.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
            quantite = BigDecimal.ONE;
        }

        Commande commandePanier = getOrCreatePendingCommande(client);
        LigneCommande ligneExistante = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande() != null
                && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                .filter(lc -> lc.getProduit() != null && lc.getProduit().getIdProduit() != null
                && lc.getProduit().getIdProduit().equals(produitId))
                .findFirst()
                .orElse(null);

        if (ligneExistante != null) {
            BigDecimal nouvelleQuantite = (ligneExistante.getQuantite() == null ? BigDecimal.ZERO
                    : ligneExistante.getQuantite()).add(quantite);
            ligneExistante.setQuantite(nouvelleQuantite);
            ligneExistante.setPrixUnitaire(produit.getPrixUnitaire());
            ligneExistante.setSousTotal(produit.getPrixUnitaire().multiply(nouvelleQuantite));
            ligneCommandeService.save(ligneExistante);
        } else {
            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commandePanier);
            ligne.setProduit(produit);
            ligne.setQuantite(quantite);
            ligne.setPrixUnitaire(produit.getPrixUnitaire());
            ligne.setSousTotal(produit.getPrixUnitaire().multiply(quantite));
            ligneCommandeService.save(ligne);
        }

        recalculerTotal(commandePanier);

        Panier panier = findCurrentPanierByIdClient(clientId);
        PanierDetails panierDetails = new PanierDetails();
        panierDetails.setPanier(panier);
        panierDetails.setCommande(commandePanier);
        panierDetailsService.save(panierDetails);

        return commandePanier;
    }

    public Commande findPendingCommande(Long clientId) {
        return commandeService.findAll().stream()
                .filter(c -> c != null && c.getClient() != null && c.getClient().getIdUtilisateur() != null)
                .filter(c -> c.getClient().getIdUtilisateur().equals(clientId))
                .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                .sorted((a, b) -> {
                    if (a.getDateCommande() == null && b.getDateCommande() == null) return 0;
                    if (a.getDateCommande() == null) return 1;
                    if (b.getDateCommande() == null) return -1;
                    return b.getDateCommande().compareTo(a.getDateCommande());
                })
                .findFirst()
                .orElse(null);
    }

    public List<LigneCommande> getLignesFromPanier(Panier panier) {
        List<PanierDetails> details = panierDetailsService.findByIdPanier(panier.getIdPanier());
        List<LigneCommande> toutesLignes = ligneCommandeService.findAll();
        return toutesLignes.stream()
                .filter(ligne -> ligne != null && ligne.getCommande() != null)
                .filter(ligne -> details.stream()
                .anyMatch(pd -> pd.getCommande() != null
                && pd.getCommande().getIdCommande() != null
                && pd.getCommande().getIdCommande().equals(ligne.getCommande().getIdCommande())))
                .distinct()
                .toList();
    }

    public List<LigneCommande> getLignesByCommande(Commande commande) {
        return ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande() != null
                && lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .toList();
    }

    public List<LigneCommande> getLignesByPanierId(Long idPanier) {
        Panier panier = findById(idPanier);
        if (panier == null) return List.of();
        return getLignesFromPanier(panier);
    }

    public void supprimerLigne(Long ligneId, Long clientId) {
        LigneCommande ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null) return;
        Commande commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null
                || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(clientId)) {
            return;
        }
        ligneCommandeService.deleteById(ligneId);
        recalculerTotal(commande);
    }

    public void mettreAJourQuantite(Long ligneId, BigDecimal quantite, Long clientId) {
        LigneCommande ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null) return;
        Commande commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null
                || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(clientId)) {
            return;
        }
        if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
            quantite = BigDecimal.ONE;
        }
        ligne.setQuantite(quantite);
        if (ligne.getPrixUnitaire() != null) {
            ligne.setSousTotal(ligne.getPrixUnitaire().multiply(quantite));
        }
        ligneCommandeService.save(ligne);
        recalculerTotal(commande);
    }

    public void cloturerPanier(Long clientId, Long idPanier, String adresseLivraison) {
        Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Commande commandePanier = findPendingCommande(clientId);
        if (commandePanier == null) {
            throw new RuntimeException("Panier introuvable");
        }

        StatutCommande statutLivraison = statutCommandeService.findAll().stream()
                .filter(sc -> sc != null && STATUT_LIVRAISON.equalsIgnoreCase(sc.getCode()))
                .findFirst()
                .orElseGet(() -> {
                    StatutCommande sc = new StatutCommande();
                    sc.setIdStatutCommande(0L);
                    sc.setCode(STATUT_LIVRAISON);
                    sc.setLibelle("Préparée");
                    return sc;
                });

        commandePanier.setAdresseLivraison(adresseLivraison);
        commandePanier.setStatutCommande(statutLivraison);

        List<LigneCommande> lignes = getLignesByPanierId(idPanier);
        for (LigneCommande lc : lignes) {
            if (lc == null) continue;
            BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
            BigDecimal p = lc.getPrixUnitaire() == null ? BigDecimal.ZERO : lc.getPrixUnitaire();
            lc.setSousTotal(p.multiply(q));
            ligneCommandeService.save(lc);
        }

        BigDecimal total = commandeClientService.calculerMontant(commandePanier, lignes);
        commandePanier.setMontantTotal(total);
        commandeService.save(commandePanier);

        Panier panier = cloturePanier(clientId);

        commandeClientService.creerOperation("commande", commandePanier, lignes, null, panier);
    }

    public Commande findCommandeById(Long commandeId, Long clientId) {
        return commandeService.findAll().stream()
                .filter(c -> c != null && c.getIdCommande() != null)
                .filter(c -> c.getIdCommande().equals(commandeId))
                .filter(c -> c.getClient() != null && c.getClient().getIdUtilisateur() != null
                && c.getClient().getIdUtilisateur().equals(clientId))
                .findFirst()
                .orElse(null);
    }

    public void recalculerTotal(Commande commande) {
        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande() != null
                && lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);
    }

    // ============ RÉSERVATIONS ============

    public ReservationMachine ajouterReservationAuPanier(Long clientId, Long machineId,
            LocalDate dateDebut, LocalDate dateFin, String lieuLivraison) {
        Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Machine machine = machineService.findById(machineId);
        if (machine == null || !Boolean.TRUE.equals(machine.getDisponible())) {
            throw new RuntimeException("Machine indisponible");
        }

        List<ReservationMachine> conflits = reservationMachineService.findConfList(machineId, dateDebut, dateFin);
        if (!conflits.isEmpty()) {
            throw new RuntimeException("La machine est déjà réservée sur cette période");
        }

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours == 0) jours = 1;
        BigDecimal prixTotal = machine.getPrixJour().multiply(BigDecimal.valueOf(jours));

        ReservationMachine reservation = new ReservationMachine();
        reservation.setMachine(machine);
        reservation.setClient(client);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setLieuLivraison(lieuLivraison);
        reservation.setPrixTotal(prixTotal);
        reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));
        ReservationMachine saved = reservationMachineService.save(reservation);

        Panier panier = findCurrentPanierByIdClient(clientId);
        
        PanierDetails detail = new PanierDetails();
        detail.setPanier(panier);
        detail.setReservationMachine(saved);
        panierDetailsService.save(detail);

        return saved;
    }

    public void supprimerReservationDuPanier(Long reservationId, Long clientId) {
        ReservationMachine reservation = reservationMachineService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
            throw new RuntimeException("Impossible de supprimer une réservation déjà validée");
        }

        PanierDetails panierDetail = panierDetailsService.findByReservationId(reservationId);
        if (panierDetail != null) {
            panierDetailsService.deleteById(panierDetail.getIdPanierDetails());
        }

        reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
        reservationMachineService.save(reservation);
    }

    public void modifierDatesReservation(Long reservationId, Long clientId,
            LocalDate dateDebut, LocalDate dateFin) {
        ReservationMachine reservation = reservationMachineService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
            throw new RuntimeException("Impossible de modifier une réservation déjà validée");
        }

        List<ReservationMachine> conflits = reservationMachineService.findConfList(
                reservation.getMachine().getIdMachine(), dateDebut, dateFin);
        conflits.removeIf(c -> c.getIdReservation().equals(reservationId));

        if (!conflits.isEmpty()) {
            throw new RuntimeException("La machine n'est plus disponible sur cette période");
        }

        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours == 0) jours = 1;
        reservation.setPrixTotal(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(jours)));

        reservationMachineService.save(reservation);
    }

    public int validerReservationsDuPanier(Long clientId) {
        Panier panier = findByClientId(clientId);
        if (panier == null) {
            throw new RuntimeException("Panier vide");
        }

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        List<ReservationMachine> reservations = new ArrayList<>();
        for (PanierDetails pd : details) {
            if (pd.getReservationMachine() != null) {
                ReservationMachine r = pd.getReservationMachine();
                if ("en_attente".equals(r.getStatutReservation().getCode())) {
                    reservations.add(r);
                }
            }
        }

        if (reservations.isEmpty()) {
            throw new RuntimeException("Aucune réservation à valider");
        }

        for (ReservationMachine r : reservations) {
            List<ReservationMachine> conflits = reservationMachineService.findConfList(
                    r.getMachine().getIdMachine(), r.getDateDebut(), r.getDateFin());
            conflits.removeIf(c -> c.getIdReservation().equals(r.getIdReservation()));
            if (!conflits.isEmpty()) {
                throw new RuntimeException("La machine " + r.getMachine().getNom() + " n'est plus disponible");
            }
        }

        StatutReservation statutValidee = statutReservationService.findByCode("validee");
        int count = 0;
        for (ReservationMachine r : reservations) {
            r.setStatutReservation(statutValidee);
            reservationMachineService.save(r);
            count++;
        }
        cloturePanier(clientId);
        return count;
    }

    public int viderReservationsDuPanier(Long clientId) {
        Panier panier = findByClientId(clientId);
        if (panier == null) {
            throw new RuntimeException("Panier vide");
        }

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        int count = 0;

        for (PanierDetails pd : details) {
            if (pd.getReservationMachine() != null) {
                ReservationMachine r = pd.getReservationMachine();
                if ("en_attente".equals(r.getStatutReservation().getCode())) {
                    r.setStatutReservation(statutReservationService.findByCode("annulee"));
                    reservationMachineService.save(r);
                    count++;
                }
            }
            panierDetailsService.deleteById(pd.getIdPanierDetails());
        }

        return count;
    }

    public List<ReservationMachine> getReservationsEnAttente(Long clientId) {
        Panier panier = findByClientId(clientId);
        if (panier == null) return List.of();

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        return details.stream()
                .filter(pd -> pd.getReservationMachine() != null)
                .map(PanierDetails::getReservationMachine)
                .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
                .toList();
    }

    // ============ PRIVATE ============

    private Commande getOrCreatePendingCommande(Utilisateur client) {
        return commandeService.findAll().stream()
                .filter(c -> c != null && c.getClient() != null)
                .filter(c -> c.getClient().getIdUtilisateur() != null
                && c.getClient().getIdUtilisateur().equals(client.getIdUtilisateur()))
                .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                .sorted((a, b) -> {
                    if (a.getDateCommande() == null && b.getDateCommande() == null) return 0;
                    if (a.getDateCommande() == null) return 1;
                    if (b.getDateCommande() == null) return -1;
                    return b.getDateCommande().compareTo(a.getDateCommande());
                })
                .findFirst()
                .orElseGet(() -> {
                    Commande c = new Commande();
                    c.setClient(client);
                    c.setStatutCommande(statutCommandeService.findAll().stream()
                            .filter(sc -> sc != null && STATUT_PANIER.equalsIgnoreCase(sc.getCode()))
                            .findFirst()
                            .orElseGet(() -> {
                                StatutCommande sc = new StatutCommande();
                                sc.setIdStatutCommande(0L);
                                sc.setCode(STATUT_PANIER);
                                sc.setLibelle("En attente");
                                return sc;
                            }));
                    c.setMontantTotal(BigDecimal.ZERO);
                    return commandeService.save(c);
                });
    }
}
