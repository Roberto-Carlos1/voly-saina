package com.voly_saina.controller;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.RoleUtilisateurRepository;
import com.voly_saina.service.UtilisateurService;
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

    /**
     * Page 01 : page officielle de connexion/inscription.
     * /login redirige ici pour éviter d'avoir deux pages de connexion différentes.
     */
    @GetMapping({"/page01", "/login"})
    public String afficherConnexion(Model model) {
        model.addAttribute("roles", roleUtilisateurRepository.findAll());
        return "auth/page01";
    }

    /**
     * Inscription depuis Page 01.
     * La connexion est ensuite faite par Spring Security via POST /connexion.
     */
    @PostMapping("/inscription")
    public String creerCompte(
            @RequestParam String nom,
            @RequestParam(required = false) String telephone,
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam Long idRole,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Utilisateur utilisateur = utilisateurService.creerCompte(nom, telephone, email, motDePasse, idRole);
            redirectAttributes.addFlashAttribute("success",
                    "Compte créé avec succès pour " + utilisateur.getEmail() + ". Connecte-toi maintenant.");
            return "redirect:/page01?inscription=success";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/page01";
        }
    }
}
