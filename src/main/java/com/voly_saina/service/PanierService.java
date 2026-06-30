package com.voly_saina.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.repository.PanierRepository;

@Service
public class PanierService {
    @Autowired
    private PanierRepository panierRepository;

    public BigDecimal montantReservation(Long idclient) {
        return panierRepository.sommeMontantReservation(idclient);
    }

    public BigDecimal montantCommande(Long idclient) {
        return panierRepository.sommeMontantCommande(idclient);
    }
}
