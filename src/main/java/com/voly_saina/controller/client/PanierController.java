package com.voly_saina.controller.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutCommande;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.CommandeClientService;
import com.voly_saina.service.CommandeService;
import com.voly_saina.service.LigneCommandeService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.PanierDetailsService;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.ProduitService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutCommandeService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/client/panier")
public class PanierController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private StatutCommandeService statutCommandeService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private LigneCommandeService ligneCommandeService;

    @Autowired
    private CommandeClientService commandeClientService;

    @Autowired
    private ModePaiementService modePaiementService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private PanierService panierService;
    @Autowired
    private PanierDetailsService panierDetailsService;

    private static final String STATUT_PANIER = "en_attente";
    private static final String STATUT_LIVRAISON = "preparee";

    // Ajoute/actualise une ligne dans le panier (1 panier unique par client)
    @GetMapping("/ajouter")
    public String ajouterAuPanier(
            @RequestParam("produitId") Long produitId,
            @RequestParam(value = "quantite", required = false, defaultValue = "1") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Produit produit = produitService.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));

            if (!Boolean.TRUE.equals(produit.getActif())) {
                model.addAttribute("error", "Produit indisponible");
                return "client/ventes/detail";
            }

            if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
                quantite = BigDecimal.ONE;
            }

            StatutCommande statutPanier = statutCommandeService.findAll().stream()
                    .filter(sc -> sc != null && STATUT_PANIER.equalsIgnoreCase(sc.getCode()))
                    .findFirst()
                    .orElseGet(() -> {
                        StatutCommande sc = new StatutCommande();
                        sc.setIdStatutCommande(0L);
                        sc.setCode(STATUT_PANIER);
                        sc.setLibelle("En attente");
                        return sc;
                    });

            Commande commandePanier = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getClient() != null)
                    .filter(c -> c.getClient().getIdUtilisateur() != null
                            && c.getClient().getIdUtilisateur().equals(idClientFinal))
                    .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                    .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                    .sorted((a, b) -> {
                        if (a.getDateCommande() == null && b.getDateCommande() == null)
                            return 0;
                        if (a.getDateCommande() == null)
                            return 1;
                        if (b.getDateCommande() == null)
                            return -1;
                        return b.getDateCommande().compareTo(a.getDateCommande());
                    })
                    .findFirst()
                    .orElseGet(() -> {
                        Commande c = new Commande();
                        c.setClient(client);
                        c.setStatutCommande(statutPanier);
                        c.setMontantTotal(BigDecimal.ZERO);
                        return commandeService.save(c);
                    });

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

            BigDecimal total = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null
                            && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            commandePanier.setMontantTotal(total);
            commandeService.save(commandePanier);

            return "redirect:/client/panier";

        } catch (Exception e) {
            model.addAttribute("produit", produitService.findById(produitId).orElse(null));
            model.addAttribute("error", e.getMessage());
            return "client/ventes/detail";
        }
    }
    
    @PostMapping("/api/ajouter-reservation")
    public String ajouterReservationAuPanier(
            @RequestParam("clientId") Long clientId,
            @RequestParam("machineId") Long machineId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam(value = "lieuLivraison", required = false) String lieuLivraison,
            Model model) {
        try {
            Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);

            Machine machine = machineService.findById(machineId);
            if (machine == null || !Boolean.TRUE.equals(machine.getDisponible())) {
                model.addAttribute("error", "Machine indisponible");
                return "client/reservations/form";
            }

            List<ReservationMachine> conflits = reservationMachineService.findConfList(machineId, debut, fin);
            if (!conflits.isEmpty()) {
                model.addAttribute("error", "La machine est déjà réservée sur cette période");
                return "client/reservations/form";
            }

            long jours = ChronoUnit.DAYS.between(debut, fin);
            if (jours == 0) jours = 1;
            BigDecimal prixTotal = machine.getPrixJour().multiply(BigDecimal.valueOf(jours));

            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);
            reservation.setLieuLivraison(lieuLivraison);
            reservation.setPrixTotal(prixTotal);
            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));
            ReservationMachine savedReservation = reservationMachineService.save(reservation);

            Panier panier = panierService.findByClientId(clientId);
            if (panier == null) {
                panier = new Panier();
                panier.setClient(client);
                panier.setDateCreation(LocalDateTime.now());
                panier = panierService.save(panier);
            }

            PanierDetails panierDetail = new PanierDetails();
            panierDetail.setPanier(panier);
            panierDetail.setReservationMachine(savedReservation);
            panierDetailsService.save(panierDetail);

            return "redirect:/client/panier?clientId=" + clientId;

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/reservations/form";
        }
    }

    
    @GetMapping
    public String voirPanier(
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
        if (client == null) {
            model.addAttribute("error", "Client non trouvé");
            return "client/panier";
        }

        Panier panier = panierService.findByClientId(idClientFinal);
        List<ReservationMachine> reservations = new ArrayList<>();
        BigDecimal totalReservations = BigDecimal.ZERO;

        if (panier != null) {
            List<PanierDetails> panierDetails = panierDetailsService.findByPanierId(panier.getIdPanier());
            
            reservations = panierDetails.stream()
                .filter(pd -> pd.getReservationMachine() != null)
                .map(PanierDetails::getReservationMachine)
                .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
                .toList();
            
            totalReservations = reservations.stream()
                .map(ReservationMachine::getPrixTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        Commande commandePanier = commandeService.findAll().stream()
                .filter(c -> c != null && c.getClient() != null)
                .filter(c -> c.getClient().getIdUtilisateur().equals(idClientFinal))
                .filter(c -> c.getStatutCommande() != null && "en_attente".equalsIgnoreCase(c.getStatutCommande().getCode()))
                .findFirst()
                .orElse(null);

        List<LigneCommande> lignes = new ArrayList<>();
        BigDecimal totalProduits = BigDecimal.ZERO;

        if (commandePanier != null) {
            lignes = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                .toList();
            
            totalProduits = lignes.stream()
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal totalGlobal = totalReservations.add(totalProduits);

        model.addAttribute("panier", panier);
        model.addAttribute("reservations", reservations);
        model.addAttribute("lignes", lignes);
        model.addAttribute("commande", commandePanier);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("totalProduits", totalProduits);
        model.addAttribute("montantTotal", totalGlobal);

        List<ModePaiement> modePaiements = modePaiementService.findAll();
        model.addAttribute("modePaiements", modePaiements);

        return "client/panier";
    }

    @org.springframework.web.bind.annotation.PostMapping("/supprimer")
    public String supprimerLigne(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null)
            return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        ligneCommandeService.deleteById(ligneId);

        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);

        return "redirect:/client/panier";
    }

    @PostMapping("/supprimer-reservation")
public String supprimerReservation(
        @RequestParam("reservationId") Long reservationId,
        @RequestParam(value = "clientId", required = false) Long clientId,
        RedirectAttributes redirectAttributes) {
    Long idClientFinal = clientId != null ? clientId : 1L;
    
    try {

        ReservationMachine reservation = reservationMachineService.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(idClientFinal)) {
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
            return "redirect:/client/panier";
        }

        if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer une réservation déjà validée");
            return "redirect:/client/panier";
        }

        PanierDetails panierDetail = panierDetailsService.findByReservationId(reservationId);
        if (panierDetail != null) {
            panierDetailsService.deleteById(panierDetail.getIdPanierDetails());
        }

        reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
        reservationMachineService.save(reservation);
        
        redirectAttributes.addFlashAttribute("success", "Réservation supprimée du panier");
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/client/panier?clientId=" + idClientFinal;
}

    @org.springframework.web.bind.annotation.PostMapping("/quantite")
    public String mettreAJourQuantite(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam("quantite") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null)
            return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
            quantite = BigDecimal.ONE;
        }
        ligne.setQuantite(quantite);
        if (ligne.getPrixUnitaire() != null) {
            ligne.setSousTotal(ligne.getPrixUnitaire().multiply(quantite));
        }
        ligneCommandeService.save(ligne);

        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);

        return "redirect:/client/panier";
    }

    @org.springframework.web.bind.annotation.PostMapping("/cloturer")
    public String cloturerPanier(
            @RequestParam("adresseLivraison") String adresseLivraison,
            @RequestParam("modePaiement") Long modePaiement,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
            if (client == null) {
                model.addAttribute("error", "Client non trouvé");
                return "client/panier";
            }

            Commande commandePanier = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getClient() != null && c.getClient().getIdUtilisateur() != null)
                    .filter(c -> c.getClient().getIdUtilisateur().equals(idClientFinal))
                    .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                    .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                    .findFirst()
                    .orElse(null);

            if (commandePanier == null) {
                model.addAttribute("error", "Panier introuvable");
                return "client/panier";
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
            ModePaiement mode = modePaiementService.findById(modePaiement);
            commandePanier.setModePaiement(mode);
            commandePanier.setStatutCommande(statutLivraison);

            // 1) recalculer sousTotal juste avant création facture (et sauvegarder)
            var lignes = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .toList();

            for (LigneCommande lc : lignes) {
                if (lc == null)
                    continue;
                BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
                BigDecimal p = lc.getPrixUnitaire() == null ? BigDecimal.ZERO : lc.getPrixUnitaire();
                lc.setSousTotal(p.multiply(q));
                ligneCommandeService.save(lc);
            }

            // 2) recalcul total + sauvegarde
            BigDecimal total = commandeClientService.calculerMontant(commandePanier, lignes);
            commandePanier.setMontantTotal(total);
            commandeService.save(commandePanier);

            // 3) vérifier stock + décrément + créer facture + opérations
            commandeClientService.creerOperation("commande", commandePanier, lignes, null);

            return "redirect:/client/panier/recap?commandeId=" + commandePanier.getIdCommande();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @GetMapping("/recap")
    public String recapCommande(
            @RequestParam("commandeId") Long commandeId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
            if (client == null) {
                model.addAttribute("error", "Client non trouvé");
                return "client/recu/recap-commande";
            }

            Commande commande = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getIdCommande() != null)
                    .filter(c -> c.getIdCommande().equals(commandeId))
                    .findFirst()
                    .orElse(null);

            if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null) {
                model.addAttribute("error", "Commande introuvable");
                return "client/recu/recap-commande";
            }

            if (!commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
                model.addAttribute("error", "Accès refusé");
                return "client/recu/recap-commande";
            }

            var lignes = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande().equals(commandeId))
                    .toList();

            BigDecimal montantTotal = commande.getMontantTotal() != null ? commande.getMontantTotal() : BigDecimal.ZERO;

            model.addAttribute("commande", commande);
            model.addAttribute("lignes", lignes);
            model.addAttribute("montantTotal", montantTotal);

            return "client/recu/recap-commande";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/recu/recap-commande";
        }
    }

    @PostMapping("/modifier-reservation")
    public String modifierDatesReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        
        try {
            ReservationMachine reservation = reservationMachineService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            
            if (!reservation.getClient().getIdUtilisateur().equals(idClientFinal)) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
                return "redirect:/client/panier";
            }
            
            if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
                redirectAttributes.addFlashAttribute("error", "Impossible de modifier une réservation déjà validée");
                return "redirect:/client/panier";
            }
            
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);

            List<ReservationMachine> conflits = reservationMachineService.findConfList(
                reservation.getMachine().getIdMachine(), debut, fin);
            conflits.removeIf(c -> c.getIdReservation().equals(reservationId));
            
            if (!conflits.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La machine n'est plus disponible sur cette période");
                return "redirect:/client/panier";
            }

            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);
            
            long jours = ChronoUnit.DAYS.between(debut, fin);
            if (jours == 0) jours = 1;
            reservation.setPrixTotal(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(jours)));
            
            reservationMachineService.save(reservation);
            
            redirectAttributes.addFlashAttribute("success", "Dates modifiées avec succès");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/client/panier?clientId=" + idClientFinal;
    }

    @PostMapping("/valider-reservations")
    public String validerPanierReservations(
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        
        try {
            Panier panier = panierService.findByClientId(idClientFinal);
            if (panier == null) {
                redirectAttributes.addFlashAttribute("error", "Panier vide");
                return "redirect:/client/panier";
            }
            List<PanierDetails> panierDetails = panierDetailsService.findByPanierId(panier.getIdPanier());
            List<ReservationMachine> reservations = panierDetails.stream()
                .filter(pd -> pd.getReservationMachine() != null)
                .map(PanierDetails::getReservationMachine)
                .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
                .toList();
            
            if (reservations.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune réservation à valider");
                return "redirect:/client/panier";
            }

            for (ReservationMachine r : reservations) {
                List<ReservationMachine> conflits = reservationMachineService.findConfList(
                    r.getMachine().getIdMachine(), 
                    r.getDateDebut(), 
                    r.getDateFin()
                );
                conflits.removeIf(c -> c.getIdReservation().equals(r.getIdReservation()));
                if (!conflits.isEmpty()) {
                    redirectAttributes.addFlashAttribute(
                        "error", 
                        "La machine " + r.getMachine().getNom() + " n'est plus disponible"
                    );
                    return "redirect:/client/panier";
                }
            }

            StatutReservation statutValidee = statutReservationService.findByCode("validee");
            for (ReservationMachine r : reservations) {
                r.setStatutReservation(statutValidee);
                reservationMachineService.save(r);
            }
            
            redirectAttributes.addFlashAttribute("success", reservations.size() + " réservation(s) validée(s)");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/client/panier?clientId=" + idClientFinal;
    }
}
