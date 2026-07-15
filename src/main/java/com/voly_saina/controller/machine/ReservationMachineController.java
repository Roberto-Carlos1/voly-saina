package com.voly_saina.controller.machine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.service.MaintenanceMachineService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutReservationService;

@Controller
@RequestMapping("/admin/reservations-machine")
public class ReservationMachineController {

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    // GET /admin/reservations-machine
    @GetMapping
    public String getAllReservations(Model model) {
        List<ReservationMachine> reservations = reservationMachineService.findAll();
        model.addAttribute("reservations", reservations);

        // Statistiques par statut (calculées à partir de la liste déjà chargée,
        // sans requête supplémentaire). Les codes viennent de la table statut_reservation.
        model.addAttribute("totalReservations", reservations.size());
        model.addAttribute("enAttente", countByCode(reservations, "en_attente"));
        model.addAttribute("enCours", countByCode(reservations, "en_cours"));
        model.addAttribute("terminees", countByCode(reservations, "terminee"));
        return "reservation/list";
    }

    // Compte les réservations ayant un code de statut donné (petit utilitaire lisible).
    private long countByCode(List<ReservationMachine> reservations, String code) {
        long total = 0;
        for (ReservationMachine reservation : reservations) {
            if (reservation.getStatutReservation() != null
                    && code.equals(reservation.getStatutReservation().getCode())) {
                total++;
            }
        }
        return total;
    }

    // GET /admin/reservations-machine/{id} — page de détail (fiche admin d'une réservation)
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Optional<ReservationMachine> reservationOpt = reservationMachineService.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/admin/reservations-machine";
        }

        ReservationMachine reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);

        // Durée de location en jours (au moins 1 jour).
        long duree = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        if (duree == 0) {
            duree = 1;
        }
        model.addAttribute("dureeJours", duree);

        // Reste à payer, seulement si la réservation est facturée.
        if (reservation.getFacture() != null) {
            BigDecimal reste = reservation.getFacture().getMontantTotal()
                    .subtract(reservation.getFacture().getMontantPaye());
            model.addAttribute("reste", reste);
        }

        // Historique : les autres réservations de la même machine.
        if (reservation.getMachine() != null) {
            model.addAttribute("historique",
                    reservationMachineService.findMachine(reservation.getMachine().getIdMachine()));
        }

        return "reservation/detail";
    }

    // GET /admin/reservations-machine/{id}/valider — l'admin valide une réservation en attente.
    @GetMapping("/{id}/valider")
    public String valider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ReservationMachine> reservationOpt = reservationMachineService.findById(id);
        if (reservationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Réservation introuvable.");
            return "redirect:/admin/reservations-machine";
        }
        ReservationMachine reservation = reservationOpt.get();

        // On ne valide qu'une réservation encore en attente.
        if (!estEnAttente(reservation)) {
            redirectAttributes.addFlashAttribute("error", "Seule une réservation en attente peut être validée.");
            return "redirect:/admin/reservations-machine/" + id;
        }

        // Conditions de validation : maintenance, déjà réservée, machine indisponible.
        String blocage = chercherRaisonBlocage(reservation);
        if (blocage != null) {
            redirectAttributes.addFlashAttribute("error", "Validation impossible : " + blocage);
            return "redirect:/admin/reservations-machine/" + id;
        }

        reservation.setStatutReservation(statutReservationService.findByCode("validee"));
        reservationMachineService.save(reservation);
        redirectAttributes.addFlashAttribute("success", "Réservation validée avec succès.");
        return "redirect:/admin/reservations-machine/" + id;
    }

    // GET /admin/reservations-machine/{id}/refuser — l'admin refuse une réservation en attente.
    @GetMapping("/{id}/refuser")
    public String refuser(@PathVariable Long id,
                          @RequestParam(required = false) String motif,
                          RedirectAttributes redirectAttributes) {
        Optional<ReservationMachine> reservationOpt = reservationMachineService.findById(id);
        if (reservationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Réservation introuvable.");
            return "redirect:/admin/reservations-machine";
        }
        ReservationMachine reservation = reservationOpt.get();

        if (!estEnAttente(reservation)) {
            redirectAttributes.addFlashAttribute("error", "Seule une réservation en attente peut être refusée.");
            return "redirect:/admin/reservations-machine/" + id;
        }

        reservation.setStatutReservation(statutReservationService.findByCode("refusee"));
        if (motif != null && !motif.trim().isEmpty()) {
            reservation.setMotifRefus(motif.trim());
        }
        reservationMachineService.save(reservation);
        redirectAttributes.addFlashAttribute("success", "Réservation refusée.");
        return "redirect:/admin/reservations-machine/" + id;
    }

    // Vrai si la réservation est au statut « en attente ».
    private boolean estEnAttente(ReservationMachine reservation) {
        return reservation.getStatutReservation() != null
                && "en_attente".equals(reservation.getStatutReservation().getCode());
    }

    /*
     * Retourne la raison (texte) qui empêche de valider la réservation, ou null si tout est bon.
     * On vérifie trois choses : la machine est-elle indisponible, en maintenance,
     * ou déjà réservée sur la période demandée ?
     */
    private String chercherRaisonBlocage(ReservationMachine reservation) {
        Machine machine = reservation.getMachine();
        LocalDate debut = reservation.getDateDebut();
        LocalDate fin = reservation.getDateFin();

        // 1) La machine est-elle marquée indisponible / hors service ?
        if (machine.getDisponible() != null && !machine.getDisponible()) {
            return "la machine est indisponible.";
        }
        if (machine.getEtatMachine() != null && "hors_service".equals(machine.getEtatMachine().getCode())) {
            return "la machine est hors service.";
        }

        // 2) La machine est-elle en maintenance pendant la période demandée ?
        for (MaintenanceMachine maintenance : maintenanceMachineService.findByMachine(machine)) {
            boolean terminee = maintenance.getStatutMaintenance() != null
                    && "terminee".equals(maintenance.getStatutMaintenance().getCode());
            LocalDate mDebut = maintenance.getDateDebut();
            LocalDate mFin = maintenance.getDateRetourReelle() != null
                    ? maintenance.getDateRetourReelle()
                    : maintenance.getDateRetourPrevue();
            if (!terminee && mDebut != null && mFin != null && sePchevauchent(debut, fin, mDebut, mFin)) {
                return "la machine est en maintenance sur cette période.";
            }
        }

        // 3) La machine est-elle déjà réservée ? On réutilise la recherche de conflits existante
        //    (findConfList ignore déjà les réservations annulées/refusées) et on écarte la
        //    réservation courante. Un conflit « validée » ou « en cours » bloque la validation.
        List<ReservationMachine> conflits = reservationMachineService.findConfList(machine.getIdMachine(), debut, fin);
        for (ReservationMachine autre : conflits) {
            if (autre.getIdReservation().equals(reservation.getIdReservation())) {
                continue; // on ignore la réservation qu'on est en train de valider
            }
            String codeAutre = autre.getStatutReservation() != null ? autre.getStatutReservation().getCode() : "";
            if ("validee".equals(codeAutre) || "en_cours".equals(codeAutre)) {
                return "la machine est déjà réservée sur cette période.";
            }
        }

        return null; // aucune condition bloquante
    }

    // Deux périodes se chevauchent si chacune commence avant (ou le jour où) l'autre se termine.
    private boolean sePchevauchent(LocalDate debut1, LocalDate fin1, LocalDate debut2, LocalDate fin2) {
        return !debut1.isAfter(fin2) && !debut2.isAfter(fin1);
    }

    // POST /api/reservations-machine
    @PostMapping
    public ResponseEntity<ReservationMachine> create(@RequestBody ReservationMachine reservationMachine) {
        ReservationMachine saved = reservationMachineService.save(reservationMachine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/reservations-machine/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ReservationMachine> update(@PathVariable Long id, @RequestBody ReservationMachine reservationMachine) {
        if (!reservationMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reservationMachine.setIdReservation(id);
        ReservationMachine updated = reservationMachineService.save(reservationMachine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/reservations-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reservationMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reservationMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
