package com.voly_saina.service;

import com.voly_saina.dto.CommandeClientDTO;
import com.voly_saina.dto.LigneCommandeDTO;
import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.repository.CommandeRepository;
import com.voly_saina.repository.LigneCommandeRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository LigneCommandeRepository;

    public CommandeClientDTO convertToCommandeClientDTO(Commande commande) {
        CommandeClientDTO dto = new CommandeClientDTO();
        dto.setIdCommande(commande.getIdCommande());
        dto.setNumeroCommande(commande.getIdCommande().toString());
        dto.setStatutCommande(commande.getStatutCommande().getCode());
        dto.setDateCommande(commande.getDateCommande().toString());
        dto.setDateLivraison(commande.getDateCommande() != null ? commande.getDateCommande().toString() : null);
        dto.setModePaiement(commande.getModePaiement().getLibelle());
        dto.setAdresseLivraison(commande.getAdresseLivraison());
        return dto;
    }

    public LigneCommandeDTO convertToLigneCommandeDTO(LigneCommande lignesCommande) {
        LigneCommandeDTO dto = new LigneCommandeDTO();
        dto.setIdLigneCommande(lignesCommande.getIdLigne());
        dto.setIdCommande(lignesCommande.getCommande().getIdCommande());
        dto.setIdProduit(lignesCommande.getProduit().getIdProduit());
        dto.setNomProduit(lignesCommande.getProduit().getNom());
        dto.setQuantite(lignesCommande.getQuantite().intValue());
        dto.setPrixUnitaire(lignesCommande.getPrixUnitaire());
        return dto;
    }   

    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    public Commande findById(Long id) {
        return commandeRepository.findById(id).orElse(null);
    }

    public Commande save(Commande commande) {
        return commandeRepository.save(commande);
    }

    public boolean existsById(Long id) {
        return commandeRepository.existsById(id);
    }

    public void deleteById(Long id) {
        commandeRepository.deleteById(id);
    }

    public List<Commande> findByClient(Long id){
        return commandeRepository.findByClientIdUtilisateur(id);
    }

    // get client commandes by idClient
    public List<CommandeClientDTO> getCommandesByClient(Long idClient) {
        List<Commande> commandes = commandeRepository.findByClientIdUtilisateur(idClient);
        return commandes.stream().map(this::convertToCommandeClientDTO).toList();
    }
    // get client comandes details by idClient
public List<LigneCommandeDTO> getLignesCommandesByClient(Long idClient) {
        List<Commande> commandes = commandeRepository.findByClientIdUtilisateur(idClient);
        return commandes.stream()
                .flatMap(commande -> LigneCommandeRepository.findByCommandeIdCommande(commande.getIdCommande()).stream())
                .map(this::convertToLigneCommandeDTO)
                .toList();
    }
}
