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

    /**
     * On ne crée plus de deuxième route /login ici.
     * La page officielle de connexion est /page01 dans AuthPageController.
     */
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

        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            model.addAttribute("error", "Cet email est déjà utilisé");
            return "auth/signup";
        }

        utilisateur.setEmail(utilisateur.getEmail().trim().toLowerCase());
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

        RoleUtilisateur roleClient = roleUtilisateurRepository.findByCode("client")
                .orElseGet(() -> {
                    RoleUtilisateur role = new RoleUtilisateur();
                    role.setCode("client");
                    role.setLibelle("Client / Agriculteur");
                    return roleUtilisateurRepository.save(role);
                });
        utilisateur.setRole(roleClient);

        StatutCompte statutActif = statutCompteRepository.findByCode("actif")
                .orElseGet(() -> {
                    StatutCompte statut = new StatutCompte();
                    statut.setCode("actif");
                    statut.setLibelle("Actif");
                    return statutCompteRepository.save(statut);
                });
        utilisateur.setStatutCompte(statutActif);

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        ProfilUtilisateur profil = new ProfilUtilisateur();
        profil.setUtilisateur(savedUser);
        profilUtilisateurRepository.save(profil);

        return "redirect:/connexion?inscription=success";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
