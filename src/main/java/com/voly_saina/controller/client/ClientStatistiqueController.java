package com.voly_saina.controller.client;

import com.voly_saina.dto.StatistiquesClientDTO;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.ClientProfilService;
import com.voly_saina.service.client.ClientStatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/client/statistiques")
@RequiredArgsConstructor
public class ClientStatistiqueController {

    private final ClientStatistiqueService clientStatistiqueService;
    private final ClientProfilService clientProfilService;

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        return clientProfilService.getUtilisateurByEmail(user.getUsername());
    }

    @GetMapping
    public String statistiques(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam(required = false) String periode,
            Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        
        LocalDate debut = dateDebut;
        LocalDate fin = dateFin;
        
        if (periode != null && !periode.isEmpty()) {
            var periodes = clientStatistiqueService.periodesDisponibles();
            if (periodes.containsKey(periode)) {
                LocalDate[] dates = periodes.get(periode);
                debut = dates[0];
                fin = dates[1];
            }
        }
        
        if (debut == null || fin == null) {
            LocalDate[] periodeDefaut = clientStatistiqueService.periodeDefaut();
            debut = periodeDefaut[0];
            fin = periodeDefaut[1];
        }
        
        StatistiquesClientDTO stats = clientStatistiqueService.genererStatistiquesClient(utilisateur.getIdUtilisateur(), debut, fin);
        
        model.addAttribute("stats", stats);
        model.addAttribute("dateDebut", debut);
        model.addAttribute("dateFin", fin);
        model.addAttribute("idClient", utilisateur.getIdUtilisateur());
        model.addAttribute("periodeSelectionnee", periode);
        
        return "client/statistiques/index";
    }
}
