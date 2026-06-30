package com.voly_saina.controller.client;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.ClientProfilService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientProfilService clientProfilService;

    public ClientController(ClientProfilService clientProfilService) {
        this.clientProfilService = clientProfilService;
    }

    @GetMapping("/accueil")
    public String accueil(@AuthenticationPrincipal User user, Model model) {
        // Récupérer l'utilisateur connecté via Spring Security
        Utilisateur utilisateur = clientProfilService.getUtilisateurByEmail(user.getUsername());
        model.addAttribute("idClient", utilisateur.getIdUtilisateur());
        model.addAttribute("clientId", utilisateur.getIdUtilisateur()); // Pour compatibilité
        model.addAttribute("utilisateur", utilisateur);
        return "client/accueil";
    }
}
