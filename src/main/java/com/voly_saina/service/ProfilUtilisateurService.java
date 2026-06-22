package com.voly_saina.service;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.repository.ProfilUtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfilUtilisateurService {

    @Autowired
    private ProfilUtilisateurRepository profilUtilisateurRepository;

    public List<ProfilUtilisateur> findAll() {
        return profilUtilisateurRepository.findAll();
    }

    public Optional<ProfilUtilisateur> findById(Long id) {
        return profilUtilisateurRepository.findById(id);
    }

    public ProfilUtilisateur save(ProfilUtilisateur profilUtilisateur) {
        return profilUtilisateurRepository.save(profilUtilisateur);
    }

    public boolean existsById(Long id) {
        return profilUtilisateurRepository.existsById(id);
    }

    public void deleteById(Long id) {
        profilUtilisateurRepository.deleteById(id);
    }
}
