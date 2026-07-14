package com.voly_saina.controller.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.exception.PanierException;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/panier/reservations")
public class PanierReservationController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private UtilisateurService utilisateurService;

    private Long getClientId(User user) {
        if (user == null) {
            throw new PanierException(PanierException.NON_CONNECTE);
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(user.getUsername());
        if (utilisateur == null) {
            throw new PanierException(PanierException.CLIENT_INTROUVABLE);
        }
        return utilisateur.getIdUtilisateur();
    }

    @PostMapping("/ajouter")
    public String ajouterReservationAuPanier(
            @RequestParam("machineId") Long machineId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam(value = "lieuLivraison", required = false) String lieuLivraison,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            panierService.ajouterReservationAuPanier(
                    getClientId(user), machineId,
                    LocalDate.parse(dateDebut), LocalDate.parse(dateFin),
                    lieuLivraison);
            redirectAttributes.addFlashAttribute("success", "Réservation ajoutée au panier");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panier";
    }

    @PostMapping("/api/ajouter")
    @ResponseBody
    public Map<String, Object> ajouterReservationAuPanierAPI(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = getClientId(user);
            Long machineId = Long.valueOf(payload.get("machineId").toString());
            LocalDate dateDebut = LocalDate.parse(payload.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(payload.get("dateFin").toString());
            String lieuLivraison = payload.containsKey("lieuLivraison")
                    ? payload.get("lieuLivraison").toString() : null;

            ReservationMachine saved = panierService.ajouterReservationAuPanier(
                    clientId, machineId, dateDebut, dateFin, lieuLivraison);

            response.put("message", "Réservation ajoutée au panier avec succès");
            response.put("reservation", Map.of(
                    "idReservation", saved.getIdReservation(),
                    "machineNom", saved.getMachine().getNom(),
                    "dateDebut", saved.getDateDebut(),
                    "dateFin", saved.getDateFin(),
                    "prixTotal", saved.getPrixTotal()));
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/supprimer")
    public String supprimerReservation(
            @RequestParam("reservationId") Long reservationId,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            panierService.supprimerReservationDuPanier(reservationId, getClientId(user));
            redirectAttributes.addFlashAttribute("success", "Réservation supprimée du panier");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panier";
    }

    @PostMapping("/modifier-dates")
    public String modifierDatesReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            panierService.modifierDatesReservation(
                    reservationId, getClientId(user),
                    LocalDate.parse(dateDebut), LocalDate.parse(dateFin));
            redirectAttributes.addFlashAttribute("success", "Dates modifiées avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panier";
    }

    @PostMapping("/valider-tout")
    public String validerToutesReservations(
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            int count = panierService.validerReservationsDuPanier(getClientId(user));
            redirectAttributes.addFlashAttribute("success", count + " réservation(s) validée(s)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panier";
    }

    @PostMapping("/vider")
    public String viderToutesReservations(
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        try {
            int count = panierService.viderReservationsDuPanier(getClientId(user));
            redirectAttributes.addFlashAttribute("success", count + " réservation(s) supprimée(s)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/panier";
    }

    @GetMapping("/api/liste")
    public String getReservationsPanier(
            @AuthenticationPrincipal User user,
            Model model) {
        Long idClientFinal = getClientId(user);
        List<ReservationMachine> reservations = panierService.getReservationsEnAttente(idClientFinal);
        BigDecimal totalReservations = reservations.stream()
                .map(ReservationMachine::getPrixTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("reservations", reservations);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("clientId", idClientFinal);

        return "client/panier/reservations :: reservationList";
    }
}
