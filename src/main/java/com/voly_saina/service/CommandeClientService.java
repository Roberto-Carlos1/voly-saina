package com.voly_saina.service;

import com.voly_saina.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class CommandeClientService {

    private static final String TYPE_OPERATION_COMMANDE = "commande";
    private static final String TYPE_MOUVEMENT_STOCK_SORTIE = "sortie";

    @Autowired
    private MouvementStockService mouvementStockService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private StatutFactureService statutFactureService;

    @Autowired
    private OperationProduitService operationProduitService;

    @Autowired
    private LigneCommandeService ligneCommandeService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private StatutCommandeService statutCommandeService;

    @Autowired
    private TypeMouvementStockRepositoryAdapter typeMouvementStockRepositoryAdapter;

    /**
     * Unique source of truth pour calculer le montant d'une commande.
     */
    public BigDecimal calculerMontant(Commande commande, List<LigneCommande> lignesCommande) {
        if (lignesCommande == null || lignesCommande.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return lignesCommande.stream()
                .filter(Objects::nonNull)
                .map(lc -> {
                    BigDecimal st = lc.getSousTotal();
                    if (st != null) return st;
                    BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
                    BigDecimal p = lc.getPrixUnitaire() == null ? BigDecimal.ZERO : lc.getPrixUnitaire();
                    return p.multiply(q);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Vérifie stock -> décrémente stock -> crée facture + opérations.
     *
     * Pour l'instant, on traite "commande" => facture avec idOperation=commande.id.
     */
    @Transactional
    public Facture creerOperation(String typeOperation, Commande commande, List<LigneCommande> lignesCommande,
                                    String numeroFacturePrefixeIfNeeded) {

        if (commande == null || commande.getIdCommande() == null) {
            throw new IllegalArgumentException("Commande invalide");
        }

        if (lignesCommande == null || lignesCommande.isEmpty()) {
            throw new IllegalArgumentException("Aucune ligne de commande");
        }

        // 1) Vérifier stock pour chaque ligne
        for (LigneCommande lc : lignesCommande) {
            if (lc == null || lc.getProduit() == null || lc.getProduit().getIdProduit() == null) {
                throw new IllegalArgumentException("Ligne invalide (produit manquant)");
            }
            Produit p = produitService.findById(lc.getProduit().getIdProduit())
                    .orElseThrow(() -> new IllegalStateException("Produit introuvable: " + lc.getProduit().getIdProduit()));

            BigDecimal quantite = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
            if (p.getStock() == null) {
                throw new IllegalStateException("Stock null pour le produit: " + p.getIdProduit());
            }

            if (p.getStock().compareTo(quantite) < 0) {
                throw new IllegalStateException("Stock insuffisant pour " + p.getNom() + " (stock=" + p.getStock() + ", demandé=" + quantite + ")");
            }
        }

        // 2) Décrémenter stock + tracer MouvementStock
        TypeMouvementStock typeSortie = typeMouvementStockRepositoryAdapter.findByCodeOrThrow(TYPE_MOUVEMENT_STOCK_SORTIE);
        for (LigneCommande lc : lignesCommande) {
            Produit p = produitService.findById(lc.getProduit().getIdProduit())
                    .orElseThrow(() -> new IllegalStateException("Produit introuvable: " + lc.getProduit().getIdProduit()));

            BigDecimal quantite = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
            if (quantite.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal nouveauStock = p.getStock().subtract(quantite);
            // Sécurité supplémentaire (ne jamais passer sous 0)
            if (nouveauStock.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Stock insuffisant au moment du décrément pour " + p.getNom() + " (stock=" + p.getStock() + ", décrément=" + quantite + ")"
                );
            }

            p.setStock(nouveauStock);
            produitService.save(p);

            MouvementStock ms = new MouvementStock();
            ms.setProduit(p);
            ms.setTypeMouvement(typeSortie);
            ms.setQuantite(quantite);
            ms.setMotif("Vente - commande #" + commande.getIdCommande());
            mouvementStockService.save(ms);
        }


        // 3) Calcul montant + créer facture (uniquement quand cloture => en_livraison)
        BigDecimal montantTotal = calculerMontant(commande, lignesCommande);
        StatutFacture statutFacture = statutFactureService.findAll().stream()
                .filter(sf -> sf != null && "en_attente".equalsIgnoreCase(sf.getCode()))
                .findFirst()
                .orElseGet(() -> {
                    // fallback: créer un statut si non trouvé
                    StatutFacture s = new StatutFacture();
                    s.setIdStatutFacture(0L);
                    s.setCode("en_attente");
                    s.setLibelle("En attente");
                    return statutFactureService.save(s);
                });

        Facture facture = new Facture();
        // Le champ numéro a unique=true: on génère un numéro simple (à adapter si collision possible)
        String numero = numeroFacturePrefixeIfNeeded != null ? numeroFacturePrefixeIfNeeded : factureService.generateNumeroFacture(commande.getIdCommande());
        
        facture.setNumero(numero);

        facture.setTypeOperation(TYPE_OPERATION_COMMANDE);
        facture.setIdOperation(commande.getIdCommande());
        facture.setClient(commande.getClient());
        facture.setMontantTotal(montantTotal);
        facture.setMontantPaye(BigDecimal.ZERO);
        facture.setStatutFacture(statutFacture);
        facture.setDateLimite(LocalDate.now().plusDays(14));

        facture = factureService.save(facture);

        // 4) Créer OperationProduit
        for (LigneCommande lc : lignesCommande) {
            OperationProduit op = new OperationProduit();
            op.setIdProduit(lc.getProduit());
            op.setIdFacture(facture);
            BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
            op.setQuantite(q.longValue());
            operationProduitService.save(op);
        }

        return facture;
    }

    /**
     * Helper: récupère les lignes d'une commande et trie stable.
     */
    public List<LigneCommande> lignesPourCommande(Commande commande) {
        if (commande == null || commande.getIdCommande() == null) return List.of();
        return ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .sorted(Comparator.comparing(lc -> lc.getIdLigne() == null ? Long.MAX_VALUE : lc.getIdLigne()))
                .toList();
    }

    /**
     * Petit adaptateur pour éviter d'avoir à lire TypeMouvementStockRepository à travers des services.
     */
    @Service
    public static class TypeMouvementStockRepositoryAdapter {

        @Autowired
        private com.voly_saina.repository.TypeMouvementStockRepository typeMouvementStockRepository;

        public TypeMouvementStock findByCodeOrThrow(String code) {
            return typeMouvementStockRepository.findAll().stream()
                    .filter(t -> t != null && t.getCode() != null)
                    .filter(t -> t.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("TypeMouvementStock introuvable pour code=" + code));
        }
    }
}

