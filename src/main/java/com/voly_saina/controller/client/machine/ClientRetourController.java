package com.voly_saina.controller.client.machine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.FormulaireRetourDTO;
import com.voly_saina.dto.dtoMacine.RetourClientDTO;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.RetourMachineService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/catalogue/retours")
public class ClientRetourController {

    @Autowired
    private ReservationMachineService reservationService;

    @Autowired
    private RetourMachineService retourService;

    @Autowired
    private UtilisateurService utilisateurService;

    // ========== PAGES HTML ==========

    // Page formulaire de retour
    @GetMapping("/{reservationId}/nouveau")
    public String formulaireRetour(@PathVariable Long reservationId,
                                   @RequestParam(required = false) Long clientId,
                                   Model model) {
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("clientId", clientId != null ? clientId : 1L);
        return "client/retours/form";
    }

    // ========== API REST ==========

    // GET /client/retours/api/form/{reservationId}
    @GetMapping("/api/form/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getFormulaireRetour(@PathVariable Long reservationId) {
        try {
            ReservationMachine reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            FormulaireRetourDTO dto = prepareFormulaireRetour(reservation);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET /client/retours/api/reservation/{reservationId}
    @GetMapping("/api/reservation/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getRetourByReservation(@PathVariable Long reservationId) {
        try {
            RetourMachine retour = retourService.findByReservation(reservationId);
            if (retour == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mapToRetourDTO(retour));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET /client/retours/api/client/{clientId}
    @GetMapping("/api/client/{clientId}")
    @ResponseBody
    public ResponseEntity<List<RetourClientDTO>> getRetoursByClient(@PathVariable Long clientId) {
        List<RetourMachine> retours = new ArrayList<>();
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);
        
        for (ReservationMachine r : reservations) {
            RetourMachine retour = retourService.findByReservation(r.getIdReservation());
            if (retour != null) {
                retours.add(retour);
            }
        }

        List<RetourClientDTO> response = retours.stream()
            .map(this::mapToRetourDTO)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // POST /client/retours/api
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> enregistrerRetour(@RequestBody Map<String, Object> payload) {
        try {
            Long reservationId = Long.valueOf(payload.get("reservationId").toString());
            String etatRetour = payload.get("etatRetour").toString();
            String remarque = payload.containsKey("remarque") ? 
                payload.get("remarque").toString() : null;

            ReservationMachine reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (reservation.getRetour() != null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Retour déjà enregistré pour cette réservation");
                return ResponseEntity.badRequest().body(error);
            }

            RetourMachine retour = new RetourMachine();
            retour.setReservation(reservation);
            retour.setDateRetour(LocalDate.now());
            retour.setEtatRetour(etatRetour);
            retour.setRemarque(remarque);

            RetourMachine saved = retourService.enregistrerRetour(retour);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Retour enregistré avec succès");
            response.put("retour", mapToRetourDTO(saved));
            response.put("reservation", mapToReservationInfo(reservation));
            
            BigDecimal montantTotal = reservation.getPrixTotal();
            if (saved.getPenalite() != null) {
                montantTotal = montantTotal.add(saved.getPenalite());
            }
            response.put("montantTotal", montantTotal);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET /client/retours/api/penalite/{reservationId}
    @GetMapping("/api/penalite/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getPenalite(@PathVariable Long reservationId) {
        try {
            ReservationMachine reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            LocalDate dateRetour = LocalDate.now();
            long joursRetard = ChronoUnit.DAYS.between(reservation.getDateFin(), dateRetour);
            
            BigDecimal penalite = joursRetard > 0 ?
                reservation.getMachine().getPrixJour()
                    .multiply(BigDecimal.valueOf(joursRetard))
                    .multiply(BigDecimal.valueOf(1.5)) :
                BigDecimal.ZERO;

            Map<String, Object> response = new HashMap<>();
            response.put("reservationId", reservationId);
            response.put("dateFin", reservation.getDateFin());
            response.put("dateRetour", dateRetour);
            response.put("joursRetard", Math.max(joursRetard, 0));
            response.put("estRetard", joursRetard > 0);
            response.put("penalite", penalite);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== Méthodes privées ==========

    private FormulaireRetourDTO prepareFormulaireRetour(ReservationMachine reservation) {
        LocalDate dateRetour = LocalDate.now();
        long joursRetard = ChronoUnit.DAYS.between(reservation.getDateFin(), dateRetour);
        BigDecimal penaliteRetard = joursRetard > 0 ?
            reservation.getMachine().getPrixJour()
                .multiply(BigDecimal.valueOf(joursRetard))
                .multiply(BigDecimal.valueOf(1.5)) :
            BigDecimal.ZERO;

        FormulaireRetourDTO dto = new FormulaireRetourDTO();
        dto.setReservationId(reservation.getIdReservation());
        dto.setClientId(reservation.getClient().getIdUtilisateur());
        dto.setMachineId(reservation.getMachine().getIdMachine());
        dto.setMachineNom(reservation.getMachine().getNom());
        dto.setMachineDescription(reservation.getMachine().getDescription());
        dto.setMachineType(reservation.getMachine().getTypeMachine() != null ?
            reservation.getMachine().getTypeMachine().getLibelle() : "Non défini");
        dto.setPrixJour(reservation.getMachine().getPrixJour());
        dto.setPrixTotal(reservation.getPrixTotal());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setDateRetour(dateRetour);
        dto.setPenaliteRetard(penaliteRetard);
        dto.setEstRetard(joursRetard > 0);
        dto.setJoursRetard(Math.max(joursRetard, 0));

        List<FormulaireRetourDTO.EtatOption> etats = new ArrayList<>();
        
        FormulaireRetourDTO.EtatOption bon = new FormulaireRetourDTO.EtatOption();
        bon.setCode("bon");
        bon.setLibelle("Bon état");
        bon.setIcon("check-circle");
        bon.setColor("success");
        bon.setDescription("Aucune pénalité");
        bon.setPenalite(BigDecimal.ZERO);
        etats.add(bon);
        
        FormulaireRetourDTO.EtatOption use = new FormulaireRetourDTO.EtatOption();
        use.setCode("use");
        use.setLibelle("Usure normale");
        use.setIcon("circle");
        use.setColor("warning");
        use.setDescription("2 jours de location");
        use.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(2)));
        etats.add(use);
        
        FormulaireRetourDTO.EtatOption endommage = new FormulaireRetourDTO.EtatOption();
        endommage.setCode("endommage");
        endommage.setLibelle("Endommagé");
        endommage.setIcon("exclamation-triangle");
        endommage.setColor("warning");
        endommage.setDescription("5 jours de location");
        endommage.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(5)));
        etats.add(endommage);
        
        FormulaireRetourDTO.EtatOption casse = new FormulaireRetourDTO.EtatOption();
        casse.setCode("casse");
        casse.setLibelle("Cassé");
        casse.setIcon("times-circle");
        casse.setColor("danger");
        casse.setDescription("15 jours de location");
        casse.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(15)));
        etats.add(casse);
        
        FormulaireRetourDTO.EtatOption perdu = new FormulaireRetourDTO.EtatOption();
        perdu.setCode("perdu");
        perdu.setLibelle("Perdu");
        perdu.setIcon("search-minus");
        perdu.setColor("danger");
        perdu.setDescription("30 jours de location");
        perdu.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(30)));
        etats.add(perdu);
        
        dto.setEtatsPossibles(etats.toArray(new FormulaireRetourDTO.EtatOption[0]));

        return dto;
    }

    private RetourClientDTO mapToRetourDTO(RetourMachine retour) {
        RetourClientDTO dto = new RetourClientDTO();
        dto.setIdRetour(retour.getIdRetour());
        dto.setIdReservation(retour.getReservation().getIdReservation());
        dto.setIdMachine(retour.getReservation().getMachine().getIdMachine());
        dto.setMachineNom(retour.getReservation().getMachine().getNom());
        dto.setDateRetour(retour.getDateRetour());
        dto.setEtatRetour(retour.getEtatRetour());
        dto.setEtatRetourLibelle(getLibelleEtat(retour.getEtatRetour()));
        dto.setRemarque(retour.getRemarque());
        dto.setPenalite(retour.getPenalite());

        long joursRetard = ChronoUnit.DAYS.between(
            retour.getReservation().getDateFin(),
            retour.getDateRetour()
        );
        dto.setEstRetard(joursRetard > 0);
        dto.setJoursRetard(Math.max(joursRetard, 0));

        return dto;
    }

    private Map<String, Object> mapToReservationInfo(ReservationMachine reservation) {
        Map<String, Object> info = new HashMap<>();
        info.put("idReservation", reservation.getIdReservation());
        info.put("idMachine", reservation.getMachine().getIdMachine());
        info.put("machineNom", reservation.getMachine().getNom());
        info.put("dateDebut", reservation.getDateDebut());
        info.put("dateFin", reservation.getDateFin());
        info.put("prixTotal", reservation.getPrixTotal());
        return info;
    }

    private String getLibelleEtat(String code) {
        switch (code) {
            case "bon": return "Bon état";
            case "use": return "Usure normale";
            case "endommage": return "Endommagé";
            case "casse": return "Cassé";
            case "perdu": return "Perdu";
            default: return code;
        }
    }
}