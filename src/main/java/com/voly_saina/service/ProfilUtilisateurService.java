package com.voly_saina.service;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.ProfilUtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfilUtilisateurService {

    @Autowired
    private ProfilUtilisateurRepository profilUtilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

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

    public ProfilUtilisateur getProfilOuNouveau(Utilisateur utilisateur) {
        return profilUtilisateurRepository.findByUtilisateurIdUtilisateur(utilisateur.getIdUtilisateur())
                .orElseGet(() -> {
                    ProfilUtilisateur profil = new ProfilUtilisateur();
                    profil.setUtilisateur(utilisateur);
                    return profil;
                });
    }

    public ProfilUtilisateur enregistrerProfil(
            Long idUtilisateur,
            String genre,
            Integer age,
            String csp,
            String localisation,
            String niveauConnexion
    ) {
        Utilisateur utilisateur = utilisateurService.trouverParId(idUtilisateur);

        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurIdUtilisateur(idUtilisateur)
                .orElseGet(ProfilUtilisateur::new);

        profil.setUtilisateur(utilisateur);
        profil.setGenre(genre);
        profil.setAge(age);
        profil.setCsp(csp);
        profil.setLocalisation(localisation);
        profil.setNiveauConnexion(niveauConnexion);

        return profilUtilisateurRepository.save(profil);
    }

    public String determinerRecommandations(Long idUtilisateur) {
        ProfilUtilisateur profil = profilUtilisateurRepository.findByUtilisateurIdUtilisateur(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Profil non complété."));

        if (profil.getNiveauConnexion() == null) {
            return "Contenu standard.";
        }

        if ("aucune".equalsIgnoreCase(profil.getNiveauConnexion())) {
            return "Contenu très léger, utilisable avec peu ou pas de connexion.";
        }

        if ("faible".equalsIgnoreCase(profil.getNiveauConnexion())) {
            return "Contenu léger, texte simple et images limitées.";
        }

        if ("bonne".equalsIgnoreCase(profil.getNiveauConnexion())) {
            return "Contenu complet avec images, fiches détaillées et ressources riches.";
        }

        return "Contenu standard adapté au profil.";
    }
}
