package com.voly_saina.service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Facture;
import com.voly_saina.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    private final FactureService factureService;

    public CommandeService(FactureService factureService) {
        this.factureService = factureService;
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

}
