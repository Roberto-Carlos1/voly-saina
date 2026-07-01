package com.voly_saina.service;

import com.voly_saina.entity.Facture;
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

    private final FactureRepository factureRepository;
    private final UtilisateurService utilisateurService;
    private final StatutFactureService statutFactureService;
    private final PanierService panierService;

    public FactureService(FactureRepository factureRepository, UtilisateurService utilisateurService,
            StatutFactureService statutFactureService, PanierService panierService) {
        this.factureRepository = factureRepository;
        this.utilisateurService = utilisateurService;
        this.statutFactureService = statutFactureService;
        this.panierService = panierService;
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
        return prefix + year + "-" + id;
    }

    public void genererFactureProformat(Long idUtilisateur) {

        Facture last = this.findIdByLast();
        String numero = this.generateNumeroFacture(last.getIdFacture());
        Utilisateur client = utilisateurService.findById(idUtilisateur);

        BigDecimal montantReservation = panierService.montantReservation(client.getIdUtilisateur()),
                montatCommande = panierService.montantCommande(client.getIdUtilisateur());

        LocalDateTime now = LocalDateTime.now();
        StatutFacture statutFacture = statutFactureService.findById((long) 1);

        Facture factureNew = new Facture();
        factureNew.setNumero(numero);
        factureNew.setClient(client);
        factureNew.setStatutFacture(statutFacture);
        factureNew.setDateFacture(now);
        factureNew.setMontantPaye(BigDecimal.valueOf(0));
        factureNew.setMontantTotal(montatCommande.add(montantReservation));

        factureNew.setTypeOperation("commande-reservation");

        // date limite ??
        factureRepository.save(factureNew);
    }

}
