package com.voly_saina.controller.auth;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.RoleUtilisateur;
import com.voly_saina.entity.StatutCompte;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.ProfilUtilisateurRepository;
import com.voly_saina.repository.RoleUtilisateurRepository;
import com.voly_saina.repository.StatutCompteRepository;
import com.voly_saina.repository.UtilisateurRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final StatutCompteRepository statutCompteRepository;
    private final ProfilUtilisateurRepository profilUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UtilisateurRepository utilisateurRepository,
                         RoleUtilisateurRepository roleUtilisateurRepository,
                         StatutCompteRepository statutCompteRepository,
                         ProfilUtilisateurRepository profilUtilisateurRepository,
                         PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.statutCompteRepository = statutCompteRepository;
        this.profilUtilisateurRepository = profilUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            return "auth/signup";
        }

        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            model.addAttribute("error", "Cet email est déjà utilisé");
            return "auth/signup";
        }

        // Hacher le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        // Définir le rôle par défaut (CLIENT)
        RoleUtilisateur roleClient = roleUtilisateurRepository.findByCode("CLIENT");
        if (roleClient == null) {
            roleClient = new RoleUtilisateur();
            roleClient.setCode("CLIENT");
            roleClient.setLibelle("Client");
            roleClient = roleUtilisateurRepository.save(roleClient);
        }
        utilisateur.setRole(roleClient);

        // Définir le statut par défaut (ACTIF)
        StatutCompte statutActif = statutCompteRepository.findByCode("ACTIF");
        if (statutActif == null) {
            statutActif = new StatutCompte();
            statutActif.setCode("ACTIF");
            statutActif.setLibelle("Actif");
            statutActif = statutCompteRepository.save(statutActif);
        }
        utilisateur.setStatutCompte(statutActif);

        // Sauvegarder l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Créer le profil utilisateur vide
        ProfilUtilisateur profil = new ProfilUtilisateur();
        profil.setUtilisateur(savedUser);
        profilUtilisateurRepository.save(profil);

        return "redirect:/login?signup=success";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}