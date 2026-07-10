package com.voly_saina.controller.client;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.ClientProfilService;
import com.voly_saina.service.client.ClientStatistiqueService;

import com.voly_saina.dto.StatistiquesClientDTO;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/client/profil")
public class ClientProfilController {

    private final ClientProfilService clientProfilService;
    private final ClientStatistiqueService clientStatistiqueService;

    public ClientProfilController(ClientProfilService clientProfilService, ClientStatistiqueService clientStatistiqueService) {
        this.clientProfilService = clientProfilService;
        this.clientStatistiqueService = clientStatistiqueService;
    }


    @GetMapping
    public String profilPrincipal(@AuthenticationPrincipal User user, Model model) {
        // Récupérer l'utilisateur connecté
        User userDetails = (User) user;
        Utilisateur utilisateur = clientProfilService.getUtilisateurByEmail(userDetails.getUsername());

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", clientProfilService.getProfilUtilisateur(utilisateur.getIdUtilisateur()));

        // Quick stats (valeurs réelles)
        com.voly_saina.dto.StatistiquesClientDTO stats = clientStatistiqueService.genererStatistiquesClient(
                utilisateur.getIdUtilisateur(),
                null,
                null
        );

        model.addAttribute("nbReservations", stats.getNombreLocations());
        model.addAttribute("nbCommandes", stats.getNombreCommandes());
        model.addAttribute("nbFactures", stats.getNombreFactures());
        model.addAttribute("totalDepense", stats.getTotalDepenses());

        return "client/profil/index";
    }



    // Getter helper
    private Utilisateur getUtilisateur(@AuthenticationPrincipal User user) {
        return clientProfilService.getUtilisateurByEmail(user.getUsername());
    }

    // Onglet Informations personnelles
    @GetMapping("/informations")
    public String informations(@AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = getUtilisateur(user);
        ProfilUtilisateur profil = clientProfilService.getProfilUtilisateur(utilisateur.getIdUtilisateur());
        
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", profil);
        
        return "client/profil/informations";
    }

    @PostMapping("/informations")
    public String updateInformations(@AuthenticationPrincipal User user,
                                    @ModelAttribute Utilisateur utilisateurUpdate,
                                    @ModelAttribute ProfilUtilisateur profilUpdate,
                                    RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = getUtilisateur(user);
        
        // Mettre à jour les infos utilisateur
        clientProfilService.updateUtilisateur(utilisateur.getIdUtilisateur(), utilisateurUpdate);
        
        // Mettre à jour les infos profil
        clientProfilService.updateProfilUtilisateur(utilisateur.getIdUtilisateur(), profilUpdate);
        
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
        return "redirect:/client/profil/informations";
    }

    // Onglet Paramètres
    @GetMapping("/parametres")
    public String parametres(@AuthenticationPrincipal User user, Model model) {
        Utilisateur utilisateur = getUtilisateur(user);
        model.addAttribute("utilisateur", utilisateur);
        return "client/profil/parametres";
    }

    @PostMapping("/mot-de-passe")
    public String updateMotDePasse(@AuthenticationPrincipal User user,
                                  @RequestParam String oldPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = getUtilisateur(user);
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas");
            return "redirect:/client/profil/parametres";
        }
        
        boolean success = clientProfilService.updateMotDePasse(utilisateur.getIdUtilisateur(), oldPassword, newPassword);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Ancien mot de passe incorrect");
        }
        
        return "redirect:/client/profil/parametres";
    }

    // Helper method to get utilisateur by email
    public Utilisateur getUtilisateurByEmail(String email) {
        return clientProfilService.getUtilisateurByEmail(email);
    }
}