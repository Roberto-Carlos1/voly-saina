// ClientReservationService.java
package com.voly_saina.service.client.machine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Paiement;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.PaiementService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutFactureService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;

@Service
public class ClientReservationService {

    @Autowired
    private ReservationMachineService reservationService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private StatutFactureService statutFactureService;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ModePaiementService modePaiementService;

    // ========== AUTHENTIFICATION ==========

    public Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return utilisateurService.findByEmail(user.getUsername());
    }

    public void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur != null) {
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("clientId", utilisateur.getIdUtilisateur());
            model.addAttribute("idClient", utilisateur.getIdUtilisateur());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("clientId", null);
        }
    }

    public boolean isClientAuthorized(Long clientId, @AuthenticationPrincipal User user) {
        Long clientIdConnecte = getClientId(user);
        return clientIdConnecte != null && clientIdConnecte.equals(clientId);
    }

    private Long getClientId(@AuthenticationPrincipal User user) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        return utilisateur != null ? utilisateur.getIdUtilisateur() : null;
    }

    // ========== RESERVATIONS ==========

    public void annulerReservationByClient(Long reservationId, Utilisateur client) {
        if (client == null) {
            throw new RuntimeException("Client non connecté");
        }

        ReservationMachine reservation = reservationService.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getClient() == null || !reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette réservation");
        }

        reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
        reservationService.save(reservation);
    }

    public ReservationsResult getReservationsForClient(Long clientId, String statut) {
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);

        if (statut != null && !statut.isEmpty()) {
            reservations = reservations.stream()
                    .filter(r -> r.getStatutReservation() != null &&
                            statut.equals(r.getStatutReservation().getCode()))
                    .collect(Collectors.toList());
        }

        List<ReservationClientDTO> dtos = reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ReservationsResult result = new ReservationsResult();
        result.setReservations(dtos);
        return result;
    }

    public List<ReservationClientDTO> getReservationsByClient(Long clientId) {
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);
        return reservations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ReservationClientDTO> getReservationsByClientAndStatut(Long clientId, String statut) {
        List<ReservationMachine> reservations = reservationService.findByClientId(clientId)
                .stream()
                .filter(r -> r.getStatutReservation() != null &&
                        statut.equals(r.getStatutReservation().getCode()))
                .collect(Collectors.toList());
        return reservations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ReservationClientDTO getReservationById(Long id, @AuthenticationPrincipal User user) {
        ReservationMachine reservation = reservationService.findById(id).orElse(null);
        if (reservation == null) {
            return null;
        }

        Long clientIdConnecte = getClientId(user);
        if (clientIdConnecte == null || !clientIdConnecte.equals(reservation.getClient().getIdUtilisateur())) {
            return null;
        }

        return mapToDTO(reservation);
    }

    public List<ReservationClientDTO> getActiveReservations(Long clientId) {
        List<ReservationMachine> reservations = reservationService.findActiveReservationsByClient(clientId);
        return reservations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ========== CREATION RESERVATION ==========

    public ResponseEntity<Map<String, Object>> createReservation(Map<String, Object> payload, @AuthenticationPrincipal User user) {
        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                return createErrorResponse("Vous devez être connecté pour effectuer une réservation", HttpStatus.UNAUTHORIZED);
            }

            Long machineId = Long.valueOf(payload.get("machineId").toString());
            LocalDate dateDebut = LocalDate.parse(payload.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(payload.get("dateFin").toString());
            String lieuLivraison = payload.containsKey("lieuLivraison") ? payload.get("lieuLivraison").toString() : null;

            Utilisateur client = utilisateurService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Machine machine = machineService.findById(machineId);
            if (machine == null) {
                return createErrorResponse("Machine non trouvée", HttpStatus.BAD_REQUEST);
            }

            if (dateFin.isBefore(dateDebut)) {
                return createErrorResponse("Date de fin invalide", HttpStatus.BAD_REQUEST);
            }

            // Vérification des conflits
            List<ReservationMachine> conflits = reservationService.findConfList(machineId, dateDebut, dateFin);
            if (!conflits.isEmpty()) {
                return createErrorResponse("Cette machine est déjà réservée sur cette période", HttpStatus.BAD_REQUEST);
            }

            // Création de la réservation
            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setLieuLivraison(lieuLivraison);

            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours == 0) jours = 1;
            reservation.setPrixTotal(machine.getPrixJour().multiply(BigDecimal.valueOf(jours)));

            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));

            ReservationMachine saved = reservationService.save(reservation);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Réservation créée avec succès");
            response.put("reservation", mapToDTO(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ========== FACTURATION ==========

    public ResponseEntity<Map<String, Object>> facturerReservation(Long id, @AuthenticationPrincipal User user) {
        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                return createErrorResponse("Vous devez être connecté", HttpStatus.UNAUTHORIZED);
            }

            ReservationMachine reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
                return createErrorResponse("Vous n'êtes pas autorisé", HttpStatus.FORBIDDEN);
            }

            if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
                return createErrorResponse("Seules les réservations en attente peuvent être facturées", HttpStatus.BAD_REQUEST);
            }

            if (reservation.getFacture() != null) {
                return createErrorResponse("Cette réservation a déjà une facture", HttpStatus.BAD_REQUEST);
            }

            Facture facture = new Facture();
            facture.setClient(reservation.getClient());
            facture.setNumero(genererNumeroFacture());
            facture.setTypeOperation("location");
            facture.setMontantTotal(reservation.getPrixTotal());
            facture.setMontantPaye(BigDecimal.ZERO);
            facture.setStatutFacture(statutFactureService.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Statut facture non trouvé")));
            facture.setDateLimite(LocalDate.now().plusDays(14));

            Facture savedFacture = factureService.save(facture);

            reservation.setFacture(savedFacture);
            reservation.setStatutReservation(statutReservationService.findByCode("validee"));
            reservationService.save(reservation);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Facture créée avec succès");
            response.put("factureId", savedFacture.getIdFacture());
            response.put("numero", savedFacture.getNumero());
            response.put("montant", savedFacture.getMontantTotal());
            response.put("reservation", mapToDTO(reservation));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<String, Object>> payerFacture(Long factureId, Map<String, Object> payload, @AuthenticationPrincipal User user) {
        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                return createErrorResponse("Vous devez être connecté", HttpStatus.UNAUTHORIZED);
            }

            Long modePaiementId = Long.valueOf(payload.get("modePaiementId").toString());

            Facture facture = factureService.findById(factureId);
            if (facture == null) {
                return createErrorResponse("Facture non trouvée", HttpStatus.BAD_REQUEST);
            }

            if (!facture.getClient().getIdUtilisateur().equals(clientId)) {
                return createErrorResponse("Vous n'êtes pas autorisé", HttpStatus.FORBIDDEN);
            }

            if (!"en_attente".equals(facture.getStatutFacture().getCode())) {
                return createErrorResponse("Cette facture a déjà été payée", HttpStatus.BAD_REQUEST);
            }

            Paiement paiement = new Paiement();
            paiement.setFacture(facture);
            paiement.setMontant(facture.getMontantTotal());
            paiement.setModePaiement(modePaiementService.findById(modePaiementId));
            paiement.setReference("PAY-" + System.currentTimeMillis());
            paiementService.save(paiement);

            facture.setMontantPaye(facture.getMontantTotal());
            facture.setStatutFacture(statutFactureService.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Statut facture non trouvé")));
            factureService.save(facture);

            List<ReservationMachine> reservations = reservationService.findByClientId(clientId)
                    .stream()
                    .filter(r -> r.getFacture() != null && r.getFacture().getIdFacture().equals(factureId))
                    .collect(Collectors.toList());

            for (ReservationMachine r : reservations) {
                r.setStatutReservation(statutReservationService.findByCode("en_cours"));
                reservationService.save(r);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Facture payée avec succès");
            response.put("factureId", facture.getIdFacture());
            response.put("montantPaye", facture.getMontantPaye());
            response.put("statut", facture.getStatutFacture().getLibelle());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<String, Object>> getReservationsByFacture(Long factureId, @AuthenticationPrincipal User user) {
        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                return createErrorResponse("Vous devez être connecté", HttpStatus.UNAUTHORIZED);
            }

            Facture facture = factureService.findById(factureId);
            if (facture == null) {
                return ResponseEntity.notFound().build();
            }

            List<ReservationMachine> reservations = reservationService.findByClientId(clientId)
                    .stream()
                    .filter(r -> r.getFacture() != null && r.getFacture().getIdFacture().equals(factureId))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("facture", Map.of(
                    "idFacture", facture.getIdFacture(),
                    "numero", facture.getNumero(),
                    "montantTotal", facture.getMontantTotal(),
                    "montantPaye", facture.getMontantPaye(),
                    "statut", facture.getStatutFacture().getLibelle(),
                    "dateLimite", facture.getDateLimite()));
            response.put("reservations", reservations.stream().map(this::mapToDTO).collect(Collectors.toList()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ========== ANNULATION ==========

    public ResponseEntity<Map<String, Object>> annulerReservation(Long id, Map<String, String> payload, @AuthenticationPrincipal User user) {
        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                return createErrorResponse("Vous devez être connecté", HttpStatus.UNAUTHORIZED);
            }

            String motif = payload != null ? payload.get("motif") : null;

            ReservationMachine reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (reservation.getClient() == null || !reservation.getClient().getIdUtilisateur().equals(clientId)) {
                return createErrorResponse("Vous n'êtes pas autorisé", HttpStatus.FORBIDDEN);
            }

            String statut = reservation.getStatutReservation().getCode();
            if ("terminee".equals(statut) || "annulee".equals(statut)) {
                return createErrorResponse("Cette réservation ne peut plus être annulée", HttpStatus.BAD_REQUEST);
            }

            reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
            if (motif != null) reservation.setMotifRefus(motif);
            reservationService.save(reservation);

            if (reservation.getFacture() != null) {
                Facture facture = reservation.getFacture();
                facture.setStatutFacture(statutFactureService.findById(5L)
                        .orElseThrow(() -> new RuntimeException("Statut facture non trouvé")));
                factureService.save(facture);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Réservation annulée avec succès");
            response.put("reservation", mapToDTO(reservation));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Map<String, Object>> annulerTout(Long clientId, @AuthenticationPrincipal User user) {
        try {
            Long clientIdConnecte = getClientId(user);
            if (clientIdConnecte == null || !clientIdConnecte.equals(clientId)) {
                return createErrorResponse("Vous n'êtes pas autorisé", HttpStatus.FORBIDDEN);
            }

            List<ReservationMachine> reservations = reservationService.findActiveReservationsByClient(clientId);

            int annulees = 0;
            for (ReservationMachine r : reservations) {
                String statut = r.getStatutReservation().getCode();
                if (!"terminee".equals(statut) && !"annulee".equals(statut)) {
                    r.setStatutReservation(statutReservationService.findByCode("annulee"));
                    reservationService.save(r);

                    if (r.getFacture() != null) {
                        Facture facture = r.getFacture();
                        facture.setStatutFacture(statutFactureService.findById(5L)
                                .orElseThrow(() -> new RuntimeException("Statut facture non trouvé")));
                        factureService.save(facture);
                    }

                    annulees++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", annulees + " réservation(s) annulée(s)");
            response.put("total", reservations.size());
            response.put("annulees", annulees);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ========== MÉTHODES PRIVÉES ==========

    private String genererNumeroFacture() {
        Facture last = factureService.findIdByLast();
        long nextId = last != null ? last.getIdFacture() + 1 : 1;
        return "FAC-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextId);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(status).body(error);
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

        String statut = reservation.getStatutReservation() != null ? reservation.getStatutReservation().getCode() : "";

        dto.setPeutAnnuler("en_attente".equals(statut) || "validee".equals(statut));
        dto.setPeutRetourner("en_cours".equals(statut) || "validee".equals(statut));
        dto.setEstTerminee("terminee".equals(statut) || "annulee".equals(statut));

        if (reservation.getFacture() != null) {
            dto.setFactureId(reservation.getFacture().getIdFacture());
            dto.setFactureNumero(reservation.getFacture().getNumero());
        }

        return dto;
    }

    // ========== CLASSES INTERNES ==========

    public static class ReservationsResult {
        private List<ReservationClientDTO> reservations;

        public List<ReservationClientDTO> getReservations() { return reservations; }
        public void setReservations(List<ReservationClientDTO> reservations) { this.reservations = reservations; }
    }
}