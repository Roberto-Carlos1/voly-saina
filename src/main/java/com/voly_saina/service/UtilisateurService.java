package com.voly_saina.service;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public boolean existsById(Long id) {
        return utilisateurRepository.existsById(id);
    }

    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }
}
