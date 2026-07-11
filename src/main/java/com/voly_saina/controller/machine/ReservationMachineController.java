package com.voly_saina.controller.machine;

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

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.service.ReservationMachineService;

@Controller
@RequestMapping("/admin/reservations-machine")
public class ReservationMachineController {

    @Autowired
    private ReservationMachineService reservationMachineService;

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

    // GET /admin/reservations-machine/{id} — page de détail d'une réservation
    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Optional<ReservationMachine> reservationOpt = reservationMachineService.findById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/catalogue/reservations/mes-reservations";
        }
        
        ReservationMachine reservation = reservationOpt.get();
        model.addAttribute("reservation", reservation);
        model.addAttribute("reservationId", id);
        model.addAttribute("clientId", reservation.getClient().getIdUtilisateur());
        
        // Ajouter les infos pour le statut et les actions
        String statutCode = reservation.getStatutReservation() != null ? 
            reservation.getStatutReservation().getCode() : "";
        model.addAttribute("statut", statutCode);
        
        // Vérifier si une facture existe
        if (reservation.getFacture() != null) {
            model.addAttribute("factureId", reservation.getFacture().getIdFacture());
            model.addAttribute("factureNumero", reservation.getFacture().getNumero());
        }
        
        return "client/reservations/detail";
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
