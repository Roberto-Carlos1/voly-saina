package com.voly_saina.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.StatutCommande;
import com.voly_saina.entity.Utilisateur;

@Service
public class PanierCommandeService {

    private static final String STATUT_PANIER = "en_attente";
    private static final String STATUT_LIVRAISON = "preparee";

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
    @Lazy
    private PanierService panierService;

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

        Panier panier = panierService.findCurrentPanierByIdClient(clientId);
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
        Panier panier = panierService.findById(idPanier);
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

        List<LigneCommande> lignes = panierService.getLignesByPanierId(idPanier);
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

        Panier panier = panierService.cloturePanier(clientId);

        commandeClientService.creerOperation("commande", commandePanier, lignes, null, panier);
    }

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
