package com.voly_saina.service;

import com.voly_saina.entity.RoleUtilisateur;
import com.voly_saina.repository.RoleUtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleUtilisateurService {

    @Autowired
    private RoleUtilisateurRepository roleUtilisateurRepository;

    public List<RoleUtilisateur> findAll() {
        return roleUtilisateurRepository.findAll();
    }

    public Optional<RoleUtilisateur> findById(Long id) {
        return roleUtilisateurRepository.findById(id);
    }

    public RoleUtilisateur save(RoleUtilisateur roleUtilisateur) {
        return roleUtilisateurRepository.save(roleUtilisateur);
    }

    public boolean existsById(Long id) {
        return roleUtilisateurRepository.existsById(id);
    }

    public void deleteById(Long id) {
        roleUtilisateurRepository.deleteById(id);
    }
}
