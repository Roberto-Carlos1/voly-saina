package com.voly_saina.controller.client;

import com.voly_saina.dto.ClientProfilQuickStatsDTO;
import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.dto.StatistiquesClientDTO;
import com.voly_saina.entity.Commande;
import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.ClientFactureService;
import com.voly_saina.service.client.ClientProfilService;
import com.voly_saina.service.client.ClientStatistiqueService;

import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client/profil")
public class ClientProfilController {

    private final ClientProfilService clientProfilService;
    private final ClientStatistiqueService clientStatistiqueService;
    private final ClientFactureService clientFactureService;

    public ClientProfilController(ClientProfilService clientProfilService,
                                  ClientStatistiqueService clientStatistiqueService,
                                  ClientFactureService clientFactureService) {
        this.clientProfilService = clientProfilService;
        this.clientStatistiqueService = clientStatistiqueService;
        this.clientFactureService = clientFactureService;
    }

    // Getter helper
    private Utilisateur getUtilisateur(User user) {
        return clientProfilService.getUtilisateurByEmail(user.getUsername());
    }

    @GetMapping
    public String profilPrincipal(@AuthenticationPrincipal User user,
                                  @RequestParam(required = false) String tab,
                                  @RequestParam(required = false) String periode,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
                                  Model model) {
        Utilisateur utilisateur = getUtilisateur(user);
        Long idClient = utilisateur.getIdUtilisateur();

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", clientProfilService.getProfilUtilisateur(idClient));
        model.addAttribute("ongletActif", tab != null ? tab : "info");

        // Données des onglets Réservations / Commandes / Factures
        List<ReservationMachine> reservations = clientProfilService.listerReservationsClient(idClient);
        List<Commande> commandes = clientProfilService.listerCommandesClient(idClient);
        List<FactureClientDTO> factures = clientFactureService.listerFacturesClient(idClient, null);
        Map<Long, Integer> nbArticlesParCommande = clientProfilService.compterArticlesParCommande(commandes);

        model.addAttribute("reservations", reservations);
        model.addAttribute("commandes", commandes);
        model.addAttribute("factures", factures);
        model.addAttribute("nbArticlesParCommande", nbArticlesParCommande);

        // Quick stats (tout l'historique, cohérentes avec les listes affichées)
        ClientProfilQuickStatsDTO quickStats = clientProfilService.calculerQuickStats(
                reservations, commandes, factures.size());
        model.addAttribute("nbReservations", quickStats.getNbReservations());
        model.addAttribute("nbCommandes", quickStats.getNbCommandes());
        model.addAttribute("nbFactures", quickStats.getNbFactures());
        model.addAttribute("totalDepense", quickStats.getTotalDepense());

        // Onglet Statistiques (période filtrable, 12 derniers mois par défaut)
        LocalDate debut = dateDebut;
        LocalDate fin = dateFin;

        if (periode != null && !periode.isEmpty()) {
            Map<String, LocalDate[]> periodes = clientStatistiqueService.periodesDisponibles();
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

        StatistiquesClientDTO stats = clientStatistiqueService.genererStatistiquesClient(idClient, debut, fin);
        model.addAttribute("stats", stats);
        model.addAttribute("statsDateDebut", debut);
        model.addAttribute("statsDateFin", fin);
        model.addAttribute("periodeSelectionnee", periode != null ? periode : "12mois");

        return "client/profil/index";
    }

    // Anciennes URLs d'onglets : tout est désormais sur la page principale
    @GetMapping("/informations")
    public String informations() {
        return "redirect:/client/profil";
    }

    @GetMapping("/parametres")
    public String parametres() {
        return "redirect:/client/profil?tab=settings";
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
        return "redirect:/client/profil";
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
            return "redirect:/client/profil?tab=settings";
        }

        boolean success = clientProfilService.updateMotDePasse(utilisateur.getIdUtilisateur(), oldPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès");
        } else {
            redirectAttributes.addFlashAttribute("error", "Ancien mot de passe incorrect");
        }

        return "redirect:/client/profil?tab=settings";
    }

    @PostMapping("/supprimer")
    public String supprimerCompte(@AuthenticationPrincipal User user,
                                  RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = getUtilisateur(user);
        try {
            clientProfilService.deleteCompte(utilisateur.getIdUtilisateur());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de désactiver le compte : " + e.getMessage());
            return "redirect:/client/profil?tab=settings";
        }
        return "redirect:/deconnexion";
    }

    // Helper method to get utilisateur by email
    public Utilisateur getUtilisateurByEmail(String email) {
        return clientProfilService.getUtilisateurByEmail(email);
    }
}
