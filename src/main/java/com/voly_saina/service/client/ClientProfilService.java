package com.voly_saina.service.client;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.ProfilUtilisateurRepository;
import com.voly_saina.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilUtilisateurRepository profilUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientProfilService(UtilisateurRepository utilisateurRepository,
                              ProfilUtilisateurRepository profilUtilisateurRepository,
                              PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.profilUtilisateurRepository = profilUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Utilisateur getUtilisateur(Long idUtilisateur) {
        return utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    @Transactional(readOnly = true)
    public Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public ProfilUtilisateur getProfilUtilisateur(Long idUtilisateur) {
        return profilUtilisateurRepository.findByUtilisateurIdUtilisateur(idUtilisateur)
            .orElse(new ProfilUtilisateur());
    }

    @Transactional
    public Utilisateur updateUtilisateur(Long idUtilisateur, Utilisateur utilisateurUpdate) {
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        
        utilisateur.setNom(utilisateurUpdate.getNom());
        utilisateur.setTelephone(utilisateurUpdate.getTelephone());
        // Email ne peut pas être modifié pour des raisons de sécurité
        
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public ProfilUtilisateur updateProfilUtilisateur(Long idUtilisateur, ProfilUtilisateur profilUpdate) {
        ProfilUtilisateur profil = getProfilUtilisateur(idUtilisateur);
        
        if (profil.getIdProfil() == null) {
            // Créer un nouveau profil s'il n'existe pas
            Utilisateur utilisateur = getUtilisateur(idUtilisateur);
            profil.setUtilisateur(utilisateur);
        }
        
        profil.setGenre(profilUpdate.getGenre());
        profil.setAge(profilUpdate.getAge());
        profil.setCsp(profilUpdate.getCsp());
        profil.setLocalisation(profilUpdate.getLocalisation());
        
        return profilUtilisateurRepository.save(profil);
    }

    @Transactional
    public boolean updateMotDePasse(Long idUtilisateur, String oldPassword, String newPassword) {
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        
        if (!passwordEncoder.matches(oldPassword, utilisateur.getMotDePasse())) {
            return false;
        }
        
        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(utilisateur);
        return true;
    }

    @Transactional
    public void deleteCompte(Long idUtilisateur) {
        // Soft delete : désactiver le compte plutôt que supprimer
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        utilisateur.getStatutCompte().setCode("INACTIF");
        utilisateurRepository.save(utilisateur);
    }
}