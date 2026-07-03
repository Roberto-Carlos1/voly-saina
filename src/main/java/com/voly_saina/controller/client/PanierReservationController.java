package com.voly_saina.controller.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.PanierDetailsService;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/client/panier/reservations")
public class PanierReservationController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private PanierService panierService;

    @Autowired
    private PanierDetailsService panierDetailsService;

    @PostMapping("/ajouter")
    public String ajouterReservationAuPanier(
            @RequestParam("clientId") Long clientId,
            @RequestParam("machineId") Long machineId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam(value = "lieuLivraison", required = false) String lieuLivraison,
            RedirectAttributes redirectAttributes) {
        try {
            Utilisateur client = utilisateurService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);

            Machine machine = machineService.findById(machineId);
            if (machine == null || !Boolean.TRUE.equals(machine.getDisponible())) {
                redirectAttributes.addFlashAttribute("error", "Machine indisponible");
                return "redirect:/client/reservations/" + machineId + "/nouvelle?clientId=" + clientId;
            }

            List<ReservationMachine> conflits = reservationMachineService.findConfList(machineId, debut, fin);
            if (!conflits.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La machine est déjà réservée sur cette période");
                return "redirect:/client/reservations/" + machineId + "/nouvelle?clientId=" + clientId;
            }

            long jours = ChronoUnit.DAYS.between(debut, fin);
            if (jours == 0)
                jours = 1;
            BigDecimal prixTotal = machine.getPrixJour().multiply(BigDecimal.valueOf(jours));

            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);
            reservation.setLieuLivraison(lieuLivraison);
            reservation.setPrixTotal(prixTotal);
            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));
            ReservationMachine savedReservation = reservationMachineService.save(reservation);

            Panier panier = panierService.findByClientId(clientId);
            if (panier == null) {
                panier = new Panier();
                panier.setClient(client);
                panier.setDateCreation(LocalDateTime.now());
                panier = panierService.save(panier);
            }

            PanierDetails panierDetail = new PanierDetails();
            panierDetail.setPanier(panier);
            panierDetail.setReservationMachine(savedReservation);
            panierDetailsService.save(panierDetail);

            redirectAttributes.addFlashAttribute("success",
                    "Réservation ajoutée au panier : " + machine.getNom());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/client/panier?clientId=" + clientId;
    }

 
    @PostMapping("/api/ajouter")
    @ResponseBody
    public Map<String, Object> ajouterReservationAuPanierAPI(
            @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long clientId = Long.valueOf(payload.get("clientId").toString());
            Long machineId = Long.valueOf(payload.get("machineId").toString());
            LocalDate dateDebut = LocalDate.parse(payload.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(payload.get("dateFin").toString());
            String lieuLivraison = payload.containsKey("lieuLivraison") ? payload.get("lieuLivraison").toString() : null;

            Utilisateur client = utilisateurService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Machine machine = machineService.findById(machineId);
            if (machine == null || !Boolean.TRUE.equals(machine.getDisponible())) {
                response.put("error", "Machine indisponible");
                return response;
            }

            List<ReservationMachine> conflits = reservationMachineService.findConfList(machineId, dateDebut, dateFin);
            if (!conflits.isEmpty()) {
                response.put("error", "La machine est déjà réservée sur cette période");
                return response;
            }

            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours == 0)
                jours = 1;
            BigDecimal prixTotal = machine.getPrixJour().multiply(BigDecimal.valueOf(jours));

            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setLieuLivraison(lieuLivraison);
            reservation.setPrixTotal(prixTotal);
            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));
            ReservationMachine savedReservation = reservationMachineService.save(reservation);

            Panier panier = panierService.findByClientId(clientId);
            if (panier == null) {
                panier = new Panier();
                panier.setClient(client);
                panier.setDateCreation(LocalDateTime.now());
                panier = panierService.save(panier);
            }

            PanierDetails panierDetail = new PanierDetails();
            panierDetail.setPanier(panier);
            panierDetail.setReservationMachine(savedReservation);
            panierDetailsService.save(panierDetail);

            response.put("message", "Réservation ajoutée au panier avec succès");
            response.put("reservation", Map.of(
                "idReservation", savedReservation.getIdReservation(),
                "machineNom", machine.getNom(),
                "dateDebut", savedReservation.getDateDebut(),
                "dateFin", savedReservation.getDateFin(),
                "prixTotal", savedReservation.getPrixTotal()
            ));

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }

    @PostMapping("/supprimer")
    public String supprimerReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;

        try {
            ReservationMachine reservation = reservationMachineService.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(idClientFinal)) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
                return "redirect:/client/panier";
            }

            if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
                redirectAttributes.addFlashAttribute("error", "Impossible de supprimer une réservation déjà validée");
                return "redirect:/client/panier";
            }

            PanierDetails panierDetail = panierDetailsService.findByReservationId(reservationId);
            if (panierDetail != null) {
                panierDetailsService.deleteById(panierDetail.getIdPanierDetails());
            }

            reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
            reservationMachineService.save(reservation);

            redirectAttributes.addFlashAttribute("success", "Réservation supprimée du panier");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/client/panier?clientId=" + idClientFinal;
    }

    @PostMapping("/modifier-dates")
    public String modifierDatesReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;

        try {
            ReservationMachine reservation = reservationMachineService.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(idClientFinal)) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé");
                return "redirect:/client/panier";
            }

            if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
                redirectAttributes.addFlashAttribute("error", "Impossible de modifier une réservation déjà validée");
                return "redirect:/client/panier";
            }

            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);

            List<ReservationMachine> conflits = reservationMachineService.findConfList(
                    reservation.getMachine().getIdMachine(), debut, fin);
            conflits.removeIf(c -> c.getIdReservation().equals(reservationId));

            if (!conflits.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La machine n'est plus disponible sur cette période");
                return "redirect:/client/panier";
            }

            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);

            long jours = ChronoUnit.DAYS.between(debut, fin);
            if (jours == 0)
                jours = 1;
            reservation.setPrixTotal(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(jours)));

            reservationMachineService.save(reservation);

            redirectAttributes.addFlashAttribute("success", "Dates modifiées avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/client/panier?clientId=" + idClientFinal;
    }

    @PostMapping("/valider-tout")
    public String validerToutesReservations(
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;

        try {
            Panier panier = panierService.findByClientId(idClientFinal);
            if (panier == null) {
                redirectAttributes.addFlashAttribute("error", "Panier vide");
                return "redirect:/client/panier";
            }

            List<PanierDetails> panierDetails = panierDetailsService.findByPanierId(panier.getIdPanier());
            List<ReservationMachine> reservations = new ArrayList<>();
                    for(PanierDetails pd : panierDetails) {
                        if (pd.getReservationMachine() != null) {
                            ReservationMachine r = pd.getReservationMachine();
                            if ("en_attente".equals(r.getStatutReservation().getCode())) {
                                reservations.add(r);
                            }
                        }
                    }

            if (reservations.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Aucune réservation à valider");
                return "redirect:/client/panier";
            }

            for (ReservationMachine r : reservations) {
                List<ReservationMachine> conflits = reservationMachineService.findConfList(
                        r.getMachine().getIdMachine(),
                        r.getDateDebut(),
                        r.getDateFin());
                conflits.removeIf(c -> c.getIdReservation().equals(r.getIdReservation()));
                if (!conflits.isEmpty()) {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "La machine " + r.getMachine().getNom() + " n'est plus disponible");
                    return "redirect:/client/panier";
                }
            }

            StatutReservation statutValidee = statutReservationService.findByCode("validee");
            int count = 0;
            for (ReservationMachine r : reservations) {
                r.setStatutReservation(statutValidee);
                reservationMachineService.save(r);
                count++;
            }

            redirectAttributes.addFlashAttribute("success", count + " réservation(s) validée(s)");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/client/panier?clientId=" + idClientFinal;
    }

    @PostMapping("/vider")
    public String viderToutesReservations(
            @RequestParam(value = "clientId", required = false) Long clientId,
            RedirectAttributes redirectAttributes) {
        Long idClientFinal = clientId != null ? clientId : 1L;

        try {
            Panier panier = panierService.findByClientId(idClientFinal);
            if (panier == null) {
                redirectAttributes.addFlashAttribute("error", "Panier vide");
                return "redirect:/client/panier";
            }

            List<PanierDetails> panierDetails = panierDetailsService.findByPanierId(panier.getIdPanier());
            int count = 0;

            for (PanierDetails pd : panierDetails) {
                if (pd.getReservationMachine() != null) {
                    ReservationMachine r = pd.getReservationMachine();
                    if ("en_attente".equals(r.getStatutReservation().getCode())) {
                        r.setStatutReservation(statutReservationService.findByCode("annulee"));
                        reservationMachineService.save(r);
                        count++;
                    }
                }
                panierDetailsService.deleteById(pd.getIdPanierDetails());
            }

            redirectAttributes.addFlashAttribute("success", count + " réservation(s) supprimée(s)");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/client/panier?clientId=" + idClientFinal;
    }

    // ============================================================
    // API : RÉCUPÉRER LES RÉSERVATIONS DU PANIER (AJAX)
    // ============================================================
    @GetMapping("/api/liste")
    public String getReservationsPanier(
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;

        Panier panier = panierService.findByClientId(idClientFinal);
        List<ReservationMachine> reservations = new ArrayList<>();
        BigDecimal totalReservations = BigDecimal.ZERO;

        if (panier != null) {
            List<PanierDetails> panierDetails = panierDetailsService.findByPanierId(panier.getIdPanier());

            reservations = panierDetails.stream()
                    .filter(pd -> pd.getReservationMachine() != null)
                    .map(PanierDetails::getReservationMachine)
                    .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
                    .toList();

            totalReservations = reservations.stream()
                    .map(ReservationMachine::getPrixTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        model.addAttribute("reservations", reservations);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("clientId", idClientFinal);

        return "client/panier/reservations :: reservationList";
    }
}