package com.voly_saina.service;

import com.voly_saina.entity.RoleUtilisateur;
import com.voly_saina.entity.StatutCompte;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.RoleUtilisateurRepository;
import com.voly_saina.repository.StatutCompteRepository;
import com.voly_saina.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Autowired
    private StatutCompteRepository statutCompteRepository;

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
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

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public Utilisateur trouverParId(Long idUtilisateur) {
        return utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));
    }

    public Utilisateur creerCompte(String nom, String telephone, String email, String motDePasse, Long idRole) {
        if (nom == null || nom.isBlank()) {
            throw new RuntimeException("Le nom est obligatoire.");
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException("L'email est obligatoire.");
        }

        if (motDePasse == null || motDePasse.isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire.");
        }

        if (idRole == null) {
            throw new RuntimeException("Le rôle est obligatoire.");
        }

        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable."));

        StatutCompte statutActif = statutCompteRepository.findByCode("actif")
                .orElseThrow(() -> new RuntimeException("Statut de compte actif introuvable."));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom.trim());
        utilisateur.setTelephone(telephone);
        utilisateur.setEmail(email.trim());
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setRole(role);
        utilisateur.setStatutCompte(statutActif);

        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur connecterUtilisateur(String identifiant, String motDePasse) {
        if (identifiant == null || identifiant.isBlank()) {
            throw new RuntimeException("L'email ou le téléphone est obligatoire.");
        }

        if (motDePasse == null || motDePasse.isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire.");
        }

        Utilisateur utilisateur = utilisateurRepository
                .findByEmailOrTelephone(identifiant.trim(), identifiant.trim())
                .orElseThrow(() -> new RuntimeException("Compte introuvable."));

        if (utilisateur.getStatutCompte() == null || !"actif".equalsIgnoreCase(utilisateur.getStatutCompte().getCode())) {
            throw new RuntimeException("Compte inactif ou bloqué.");
        }

        if (!motDePasse.equals(utilisateur.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect.");
        }

        return utilisateur;
    }

    public boolean controlerDroit(Long idUtilisateur, String action, String ressource) {
        Utilisateur utilisateur = trouverParId(idUtilisateur);

        if (utilisateur.getRole() == null) {
            return false;
        }

        String role = utilisateur.getRole().getCode();

        if ("responsable".equalsIgnoreCase(role) || "gestionnaire".equalsIgnoreCase(role)) {
            return true;
        }

        if ("client".equalsIgnoreCase(role)) {
            return ressource != null && ressource.startsWith("client");
        }

        if ("employe".equalsIgnoreCase(role)) {
            return ressource != null && !ressource.contains("paiement");
        }

        return false;
    }
}
