package com.voly_saina.controller;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.ProfilUtilisateurService;
import com.voly_saina.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfilPageController {

    private final ProfilUtilisateurService profilUtilisateurService;
    private final UtilisateurService utilisateurService;

    public ProfilPageController(ProfilUtilisateurService profilUtilisateurService, UtilisateurService utilisateurService) {
        this.profilUtilisateurService = profilUtilisateurService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping({"/page02", "/client/profil"})
    public String afficherProfil(HttpSession session, Model model) {
        Long idUtilisateur = (Long) session.getAttribute("idUtilisateur");

        if (idUtilisateur == null) {
            return "redirect:/page01";
        }

        Utilisateur utilisateur = utilisateurService.trouverParId(idUtilisateur);
        ProfilUtilisateur profil = profilUtilisateurService.getProfilOuNouveau(utilisateur);

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", profil);
        model.addAttribute("idClient", idUtilisateur);
        model.addAttribute("roleCode", utilisateur.getRole() != null ? utilisateur.getRole().getCode() : "");

        try {
            model.addAttribute("recommandation", profilUtilisateurService.determinerRecommandations(idUtilisateur));
        } catch (RuntimeException e) {
            model.addAttribute("recommandation", "Complète le profil pour activer les recommandations.");
        }

        return "profil/page02";
    }

    @PostMapping("/page02/profil")
    public String enregistrerProfil(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String csp,
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) String niveauConnexion,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long idUtilisateur = (Long) session.getAttribute("idUtilisateur");

        if (idUtilisateur == null) {
            return "redirect:/page01";
        }

        profilUtilisateurService.enregistrerProfil(
                idUtilisateur,
                genre,
                age,
                csp,
                localisation,
                niveauConnexion
        );

        redirectAttributes.addFlashAttribute("success", "Profil enregistré avec succès.");
        return "redirect:/page02";
    }
}
