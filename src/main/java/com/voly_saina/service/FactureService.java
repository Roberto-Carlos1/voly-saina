package com.voly_saina.service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.OperationMachine;
import com.voly_saina.entity.OperationProduit;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.StatutFacture;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.entity.dto.FactureDTO;
import com.voly_saina.repository.FactureRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FactureService {

    private final OperationMachineService operationMachineService;
    private final FactureRepository factureRepository;
    private final UtilisateurService utilisateurService;
    private final StatutFactureService statutFactureService;
    private final PanierDetailsService panierDetailsService;
    private final OperationProduitService operationProduitService;

    public FactureService(FactureRepository factureRepository, UtilisateurService utilisateurService,
            StatutFactureService statutFactureService, PanierDetailsService panierDetailsService,
            OperationProduitService operationProduitService, OperationMachineService operationMachineService) {
        this.factureRepository = factureRepository;
        this.utilisateurService = utilisateurService;
        this.statutFactureService = statutFactureService;
        this.panierDetailsService = panierDetailsService;
        this.operationProduitService = operationProduitService;
        this.operationMachineService = operationMachineService;
    }

    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    public Facture findById(Long id) {
        return factureRepository.findById(id).orElse(null);
    }

    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public boolean existsById(Long id) {
        return factureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        factureRepository.deleteById(id);
    }

    public Facture findIdByLast() {
        return factureRepository.findTopByOrderByIdFactureDesc().orElse(null);
    }

    public Page<Facture> findByPage(Pageable pageable) {
        Page<Facture> page = factureRepository.findAll(pageable);
        List<Long> ids = new ArrayList<>();

        for (Facture facture : page.getContent()) {
            ids.add(facture.getIdFacture());
        }

        if (ids.isEmpty()) {
            return page;
        }

        List<Facture> result = new ArrayList<>();
        List<Facture> liste = factureRepository.findAll();
        for (Long id : ids) {
            for (Facture facture : liste) {
                if (facture.getIdFacture() == id) {
                    result.add(facture);
                }
            }
        }

        return new PageImpl<>(result, pageable, page.getTotalElements());
    }

    public Page<Facture> findFactureClientByPage(Pageable pageable, Long idClient) {
        Page<Facture> page = factureRepository.findAll(pageable);
        List<Long> ids = new ArrayList<>();

        for (Facture facture : page.getContent()) {
            if (facture.getClient().getIdUtilisateur().equals(idClient)) {
                ids.add(facture.getIdFacture());
            }
        }

        if (ids.isEmpty()) {
            return page;
        }

        List<Facture> result = new ArrayList<>();
        List<Facture> liste = factureRepository.findAll();
        for (Long id : ids) {
            for (Facture facture : liste) {
                if (facture.getIdFacture() == id) {
                    result.add(facture);
                }
            }
        }

        return new PageImpl<>(result, pageable, page.getTotalElements());
    }

    public Page<Facture> filtreFacture(FactureDTO facture, Pageable pageable) {
        Long idStatut = null;
        if (facture.getIdStatut() != null && !facture.getIdStatut().isEmpty()) {
            idStatut = Long.parseLong(facture.getIdStatut());
        }

        return factureRepository.filtrerFactures(facture.getNomClient(), idStatut, pageable);
    }

    // Numero de facture
    public String generateNumeroFacture(long id) {
        String prefix = "FAC-";
        String year = String.valueOf(LocalDate.now().getYear());
        String secondPart = String.format("%04d", id);
        return prefix + year + "-" + id + "-" + secondPart;
    }

    public Facture genererFactureProformat(Long idUtilisateur, Long idPanier) {

        long last = this.findIdByLast() == null ? 1L : this.findIdByLast().getIdFacture() + 1;
        String numero = this.generateNumeroFacture(last);
        Utilisateur client = utilisateurService.findById(idUtilisateur).orElse(null);

        BigDecimal montantReservation = panierDetailsService.montantReservation(idPanier) != null ? panierDetailsService.montantReservation(idPanier) : BigDecimal.ZERO;
        BigDecimal montantCommande = panierDetailsService.montantCommande(idPanier) != null ? panierDetailsService.montantCommande(idPanier) : BigDecimal.ZERO;

        LocalDateTime now = LocalDateTime.now();
        StatutFacture statutFacture = statutFactureService.findById((long) 1).orElse(null);

        Facture factureNew = new Facture();
        factureNew.setNumero(numero);
        factureNew.setClient(client);
        factureNew.setStatutFacture(statutFacture);
        factureNew.setDateFacture(now);
        factureNew.setDateLimite(now.plusDays(14).toLocalDate());
        factureNew.setMontantPaye(BigDecimal.valueOf(0));
        factureNew.setMontantTotal(montantCommande.add(montantReservation));
        factureNew.setTypeOperation("commande-reservation");
        factureNew.setPanier(panierDetailsService.findPanierById(idPanier));

        factureRepository.save(factureNew);
        return factureNew;
    }

    public void creerOperationCommande(Commande commande, Facture facture, List<LigneCommande> lignesCommande) {

        for (LigneCommande lc : lignesCommande) {
            OperationProduit op = new OperationProduit();
            op.setIdProduit(lc.getProduit());
            op.setIdFacture(facture);
            BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
            op.setQuantite(q.longValue());
            operationProduitService.save(op);
        }

    }

    public void creerOperationReservation(Facture facture, Panier panier) {

        for (PanierDetails pd : panierDetailsService.findByPanierId(panier.getIdPanier())) {
            if (pd.getReservationMachine() == null) {
                continue;
            }
        }

    }

}
