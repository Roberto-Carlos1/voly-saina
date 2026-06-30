package com.voly_saina.controller;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.RoleUtilisateurRepository;
import com.voly_saina.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {

    private final UtilisateurService utilisateurService;
    private final RoleUtilisateurRepository roleUtilisateurRepository;

    public AuthPageController(UtilisateurService utilisateurService, RoleUtilisateurRepository roleUtilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
    }

    @GetMapping({"/page01", "/login"})
    public String afficherConnexion(Model model) {
        model.addAttribute("roles", roleUtilisateurRepository.findAll());
        return "auth/page01";
    }

    @PostMapping("/inscription")
    public String creerCompte(
            @RequestParam String nom,
            @RequestParam(required = false) String telephone,
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam Long idRole,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Utilisateur utilisateur = utilisateurService.creerCompte(nom, telephone, email, motDePasse, idRole);
            enregistrerSession(session, utilisateur);
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès. Complète maintenant ton profil.");
            return "redirect:/page02";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/page01";
        }
    }

    @PostMapping("/connexion")
    public String connecter(
            @RequestParam String identifiant,
            @RequestParam String motDePasse,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Utilisateur utilisateur = utilisateurService.connecterUtilisateur(identifiant, motDePasse);
            enregistrerSession(session, utilisateur);

            if (utilisateur.getRole() != null && "client".equalsIgnoreCase(utilisateur.getRole().getCode())) {
                return "redirect:/page02";
            }

            return "redirect:/api/machines";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/page01";
        }
    }

    @GetMapping("/deconnexion")
    public String deconnecter(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Déconnexion réussie.");
        return "redirect:/page01";
    }

    private void enregistrerSession(HttpSession session, Utilisateur utilisateur) {
        session.setAttribute("idUtilisateur", utilisateur.getIdUtilisateur());
        session.setAttribute("idClient", utilisateur.getIdUtilisateur());

        if (utilisateur.getRole() != null) {
            session.setAttribute("roleUtilisateur", utilisateur.getRole().getCode());
        }
    }
}
