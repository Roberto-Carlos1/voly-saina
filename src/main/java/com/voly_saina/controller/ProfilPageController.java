package com.voly_saina.controller;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.ProfilUtilisateurService;
import com.voly_saina.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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

    /**
     * Page 02 : profil utilisateur.
     * IMPORTANT : on garde seulement /profil pour éviter le conflit avec ClientProfilController sur /client/profil.
     */
    @GetMapping("/profil")
    public String afficherProfil(@AuthenticationPrincipal User user, HttpSession session, Model model) {
        Utilisateur utilisateur = recupererUtilisateurConnecte(user, session);

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        session.setAttribute("idUtilisateur", utilisateur.getIdUtilisateur());
        session.setAttribute("idClient", utilisateur.getIdUtilisateur());

        if (utilisateur.getRole() != null) {
            session.setAttribute("roleUtilisateur", utilisateur.getRole().getCode());
        }

        ProfilUtilisateur profil = profilUtilisateurService.getProfilOuNouveau(utilisateur);

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", profil);
        model.addAttribute("idClient", utilisateur.getIdUtilisateur());
        model.addAttribute("roleCode", utilisateur.getRole() != null ? utilisateur.getRole().getCode() : "");

        try {
            model.addAttribute("recommandation", profilUtilisateurService.determinerRecommandations(utilisateur.getIdUtilisateur()));
        } catch (RuntimeException e) {
            model.addAttribute("recommandation", "Complète le profil pour activer les recommandations.");
        }

        return "profil/page02";
    }

    @PostMapping("/profil/enregistrer")
    public String enregistrerProfil(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String csp,
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) String niveauConnexion,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Utilisateur utilisateur = recupererUtilisateurConnecte(user, session);

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        profilUtilisateurService.enregistrerProfil(
                utilisateur.getIdUtilisateur(),
                genre,
                age,
                csp,
                localisation,
                niveauConnexion
        );

        redirectAttributes.addFlashAttribute("success", "Profil enregistré avec succès.");
        return "redirect:/profil";
    }

    private Utilisateur recupererUtilisateurConnecte(User user, HttpSession session) {
        if (user != null && user.getUsername() != null) {
            Utilisateur utilisateur = utilisateurService.findByEmail(user.getUsername());
            if (utilisateur != null) {
                return utilisateur;
            }
        }

        Long idUtilisateur = (Long) session.getAttribute("idUtilisateur");
        if (idUtilisateur != null) {
            return utilisateurService.trouverParId(idUtilisateur);
        }

        return null;
    }
}
