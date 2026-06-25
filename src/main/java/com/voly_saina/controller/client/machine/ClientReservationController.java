package com.voly_saina.controller.client.machine;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/reservations")
public class ClientReservationController {

    @Autowired
    private ReservationMachineService reservationService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private StatutReservationService statutReservationService;

    // GET /api/client/reservations/client/{clientId}
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClient(@PathVariable Long clientId) {
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);
        List<ReservationClientDTO> response = reservations.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/client/reservations/client/{clientId}/statut/{statut}
    @GetMapping("/client/{clientId}/statut/{statut}")
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClientAndStatut(
            @PathVariable Long clientId,
            @PathVariable String statut) {
        List<ReservationMachine> reservations = reservationService
            .findByClientAndStatutReservationCode(clientId, statut);
        List<ReservationClientDTO> response = reservations.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/client/reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationClientDTO> getReservationById(@PathVariable Long id) {
        ReservationMachine reservation = reservationService.findById(id)
            .orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToDTO(reservation));
    }

    // GET /api/client/reservations/client/{clientId}/active
    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<List<ReservationClientDTO>> getActiveReservations(@PathVariable Long clientId) {
        List<ReservationMachine> reservations = reservationService
            .findActiveReservationsByClient(clientId);
        List<ReservationClientDTO> response = reservations.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // POST /api/client/reservations
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody Map<String, Object> payload) {
        try {
            Long clientId = Long.valueOf(payload.get("clientId").toString());
            Long machineId = Long.valueOf(payload.get("machineId").toString());
            LocalDate dateDebut = LocalDate.parse(payload.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(payload.get("dateFin").toString());
            String lieuLivraison = payload.containsKey("lieuLivraison") ? 
                payload.get("lieuLivraison").toString() : null;

            // Vérifier l'utilisateur
            Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            // Vérifier la machine
            Machine machine = machineService.findById(machineId);
            if (machine == null || !machine.getDisponible()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Machine non disponible");
                return ResponseEntity.badRequest().body(error);
            }

            // Vérifier les dates
            if (dateFin.isBefore(dateDebut)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Date de fin invalide");
                return ResponseEntity.badRequest().body(error);
            }

            if (dateDebut.isBefore(LocalDate.now())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "La date de début ne peut pas être dans le passé");
                return ResponseEntity.badRequest().body(error);
            }

            // Vérifier les conflits
            List<ReservationMachine> conflits = reservationService
                .findConflictingReservations(machineId, dateDebut, dateFin);
            if (!conflits.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette machine est déjà réservée sur cette période");
                return ResponseEntity.badRequest().body(error);
            }

            // Créer la réservation
            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setLieuLivraison(lieuLivraison);

            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours == 0) jours = 1;
            reservation.setPrixTotal(machine.getPrixJour().multiply(BigDecimal.valueOf(jours)));

            reservation.setStatutReservation(
                statutReservationService.findByCode("en_attente")
            );

            ReservationMachine saved = reservationService.save(reservation);

            // Rendre la machine indisponible
            machine.setDisponible(false);
            machineService.save(machine);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Réservation créée avec succès");
            response.put("reservation", mapToDTO(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // PUT /api/client/reservations/{id}/annuler
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annulerReservation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        try {
            String motif = payload != null ? payload.get("motif") : null;
            
            ReservationMachine reservation = reservationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            String statut = reservation.getStatutReservation().getCode();
            if ("terminee".equals(statut) || "annulee".equals(statut)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette réservation ne peut plus être annulée");
                return ResponseEntity.badRequest().body(error);
            }

            reservation.setStatutReservation(
                statutReservationService.findByCode("annulee")
            );
            if (motif != null) reservation.setMotifRefus(motif);
            reservationService.save(reservation);

            // Libérer la machine
            Machine machine = reservation.getMachine();
            machine.setDisponible(true);
            machineService.save(machine);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Réservation annulée avec succès");
            response.put("reservation", mapToDTO(reservation));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // PUT /api/client/reservations/client/{clientId}/annuler-tout
    @PutMapping("/client/{clientId}/annuler-tout")
    public ResponseEntity<Map<String, Object>> annulerTout(@PathVariable Long clientId) {
        try {
            List<ReservationMachine> reservations = reservationService
                .findActiveReservationsByClient(clientId);

            int annulees = 0;
            for (ReservationMachine r : reservations) {
                String statut = r.getStatutReservation().getCode();
                if (!"terminee".equals(statut) && !"annulee".equals(statut)) {
                    r.setStatutReservation(
                        statutReservationService.findByCode("annulee")
                    );
                    reservationService.save(r);
                    
                    // Libérer la machine
                    Machine machine = r.getMachine();
                    machine.setDisponible(true);
                    machineService.save(machine);
                    
                    annulees++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", annulees + " réservation(s) annulée(s)");
            response.put("total", reservations.size());
            response.put("annulees", annulees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET /api/client/reservations/client/{clientId}/statistiques
    @GetMapping("/client/{clientId}/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques(@PathVariable Long clientId) {
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);
        
        long total = reservations.size();
        long enCours = reservations.stream()
            .filter(r -> "en_cours".equals(r.getStatutReservation().getCode()))
            .count();
        long terminees = reservations.stream()
            .filter(r -> "terminee".equals(r.getStatutReservation().getCode()))
            .count();
        long annulees = reservations.stream()
            .filter(r -> "annulee".equals(r.getStatutReservation().getCode()))
            .count();
        
        BigDecimal totalDepenses = reservations.stream()
            .filter(r -> "terminee".equals(r.getStatutReservation().getCode()))
            .map(ReservationMachine::getPrixTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReservations", total);
        stats.put("reservationsEnCours", enCours);
        stats.put("reservationsTerminees", terminees);
        stats.put("reservationsAnnulees", annulees);
        stats.put("totalDepenses", totalDepenses);
        
        return ResponseEntity.ok(stats);
    }

    private ReservationClientDTO mapToDTO(ReservationMachine reservation) {
        ReservationClientDTO dto = new ReservationClientDTO();
        dto.setIdReservation(reservation.getIdReservation());
        dto.setIdMachine(reservation.getMachine().getIdMachine());
        dto.setMachineNom(reservation.getMachine().getNom());
        
        if (reservation.getMachine().getTypeMachine() != null) {
            dto.setMachineType(reservation.getMachine().getTypeMachine().getLibelle());
        }
        
        dto.setPrixJour(reservation.getMachine().getPrixJour());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setLieuLivraison(reservation.getLieuLivraison());
        dto.setPrixTotal(reservation.getPrixTotal());
        
        if (reservation.getStatutReservation() != null) {
            dto.setStatut(reservation.getStatutReservation().getCode());
            dto.setStatutLibelle(reservation.getStatutReservation().getLibelle());
        }
        
        dto.setMotifRefus(reservation.getMotifRefus());
        dto.setDateCreation(reservation.getDateCreation());
        
        // Calcul des actions possibles
        String statut = reservation.getStatutReservation() != null ? 
            reservation.getStatutReservation().getCode() : "";
        
        dto.setPeutAnnuler("en_attente".equals(statut) || "validee".equals(statut));
        dto.setPeutRetourner("en_cours".equals(statut) || "validee".equals(statut));
        dto.setEstTerminee("terminee".equals(statut) || "annulee".equals(statut));
        
        return dto;
    }
}