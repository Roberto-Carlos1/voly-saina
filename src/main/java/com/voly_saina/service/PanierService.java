package com.voly_saina.service;

import java.math.BigDecimal;
import java.sql.ClientInfoStatus;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Panier;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.PanierRepository;

@Service
public class PanierService {
    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    public List<Panier> findAll() {
        return panierRepository.findAll();
    }

    public Panier findById(Long id) {
        return panierRepository.findById(id).orElse(null);
    }

    public Panier save(Panier Panier) {
        return panierRepository.save(Panier);
    }

    public boolean existsById(Long id) {
        return panierRepository.existsById(id);
    }

    public void deleteById(Long id) {
        panierRepository.deleteById(id);
    }

    public Panier findCurrentPanierByIdClient(Long idClient) {
        Panier retour = panierRepository.findFirstByClientIdUtilisateurOrderByIdPanierDesc(idClient);

        if (retour == null) {
            Utilisateur u = utilisateurService.findById(idClient).orElse(null);
            retour = new Panier();
            retour.setClient(u);
            retour.setActif(true);
            this.save(retour);
        }

        return retour;

    }

    public Panier findByClientId(Long clientId) {
        return panierRepository.findByClientIdUtilisateur(clientId).orElse(null);
    }


}
