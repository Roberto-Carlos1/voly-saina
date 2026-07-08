package com.voly_saina.service;

import com.voly_saina.entity.Commande;
import com.voly_saina.repository.CommandeRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Commande;
import com.voly_saina.repository.CommandeRepository;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

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

}
