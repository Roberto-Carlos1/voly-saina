package com.voly_saina.service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.repository.PanierDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PanierDetailsService {

    @Autowired
    private PanierDetailsRepository panierDetailsRepository;

    public List<PanierDetails> findAll() {
        return panierDetailsRepository.findAll();
    }

    public PanierDetails findById(Long id) {
        return panierDetailsRepository.findById(id).orElse(null);
    }

    public PanierDetails save(PanierDetails PanierDetails) {
        return panierDetailsRepository.save(PanierDetails);
    }

    public boolean existsById(Long id) {
        return panierDetailsRepository.existsById(id);
    }

    public void deleteById(Long id) {
        panierDetailsRepository.deleteById(id);
    }

    public BigDecimal montantReservation(Long idPanier) {
        return panierDetailsRepository.sommeMontantReservation(idPanier);
    }

    public BigDecimal montantCommande(Long idPanier) {
        return panierDetailsRepository.sommeMontantCommande(idPanier);
    }

    public List<PanierDetails> findByIdPanier(Long idPanier) {
        return panierDetailsRepository.findByPanierIdPanier(idPanier);
    }

    public List<PanierDetails> findCommandesByPanier(Long idPanier) {
        return panierDetailsRepository.findByPanierIdPanierAndCommandeIdCommandeIsNotNull(idPanier);
    }

    public List<PanierDetails> findReservationByPanier(Long idPanier) {
        return panierDetailsRepository.findByPanierIdPanierAndReservationMachineIdReservationIsNotNull(idPanier);
    }

    public Commande findByCommande(Long idCommande) {
        return panierDetailsRepository.findByCommandeIdCommande(idCommande);
    }
    public List<PanierDetails> findByPanierId(Long panierId) {
        return panierDetailsRepository.findByPanierIdPanier(panierId);
    }

    public PanierDetails findByReservationId(Long reservationId) {
        return panierDetailsRepository.findByReservationMachineIdReservation(reservationId).orElse(null);
    }

}

