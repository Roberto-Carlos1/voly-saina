package com.voly_saina.controller.client.machine;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.OperationMachine;
import com.voly_saina.entity.Paiement;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.OperationMachineService;
import com.voly_saina.service.PaiementService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutFactureService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;
import com.voly_saina.service.client.machine.ClientReservationService;

@Controller
@RequestMapping("/catalogue/reservations")
public class ClientReservationController {

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
    private OperationMachineService operationMachineService;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ModePaiementService modePaiementService;

    @Autowired
    private ClientReservationService clientReservationService;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return utilisateurService.findByEmail(user.getUsername());
    }

    private Long getClientId(@AuthenticationPrincipal User user) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur == null) {
            return null;
        }
        return utilisateur.getIdUtilisateur();
    }

    private void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
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

    // ========== PAGES HTML ==========

    @GetMapping("/{machineId}/nouvelle")
    public String formulaireReservation(
            @PathVariable Long machineId,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);
        model.addAttribute("machineId", machineId);
        return "client/reservations/form";
    }

    @GetMapping("/{id}/annuler")
    public String formulaireAnnulation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);

        ReservationMachine reservationMachine = reservationService.findById(id).orElse(null);
        clientReservationService.annulerReservation(reservationMachine);
        reservationService.save(reservationMachine);

        return "redirect:/catalogue/reservations/mes-reservations";
    }

    @GetMapping("/mes-reservations")
    public String mesReservations(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String statut,
            Model model) {

        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        addUtilisateurConnecte(model, user);

        // Récupérer les réservations du client connecté
        List<ReservationMachine> reservations = reservationService.findByClientId(utilisateur.getIdUtilisateur());

        // Filtrer par statut si nécessaire
        if (statut != null && !statut.isEmpty()) {
            reservations = reservations.stream()
                    .filter(r -> r.getStatutReservation() != null &&
                            statut.equals(r.getStatutReservation().getCode()))
                    .collect(Collectors.toList());
        }

        List<ReservationClientDTO> reservationDTOs = reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        model.addAttribute("reservations", reservationDTOs);
        model.addAttribute("filtreStatut", statut);
        return "client/reservations/list";
    }

    @GetMapping("/{id}")
    public String detailReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model) {

        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        addUtilisateurConnecte(model, user);
        model.addAttribute("reservationId", id);
        return "client/reservations/detail";
    }

    @GetMapping("/facture/{factureId}")
    public String detailFacture(
            @PathVariable Long factureId,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);
        model.addAttribute("factureId", factureId);
        return "client/reservations/facture-detail";
    }

    // ========== API REST ==========

    @GetMapping("/api/client/{clientId}")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        Long clientIdConnecte = getClientId(user);
        if (clientIdConnecte == null || !clientIdConnecte.equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ReservationMachine> reservations = reservationService.findByClientId(clientId);
        return ResponseEntity.ok(reservations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/api/client/{clientId}/statut/{statut}")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClientAndStatut(
            @PathVariable Long clientId,
            @PathVariable String statut,
            @AuthenticationPrincipal User user) {

        Long clientIdConnecte = getClientId(user);
        if (clientIdConnecte == null || !clientIdConnecte.equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ReservationMachine> reservations = reservationService.findByClientId(clientId)
                .stream()
                .filter(r -> r.getStatutReservation() != null &&
                        statut.equals(r.getStatutReservation().getCode()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ReservationClientDTO> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ReservationMachine reservation = reservationService.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        Long clientIdConnecte = getClientId(user);
        if (clientIdConnecte == null || !clientIdConnecte.equals(reservation.getClient().getIdUtilisateur())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(mapToDTO(reservation));
    }

    @GetMapping("/api/client/{clientId}/active")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getActiveReservations(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        Long clientIdConnecte = getClientId(user);
        if (clientIdConnecte == null || !clientIdConnecte.equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ReservationMachine> reservations = reservationService.findActiveReservationsByClient(clientId);
        return ResponseEntity.ok(reservations.stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReservation(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {

        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous devez être connecté pour effectuer une réservation");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long machineId = Long.valueOf(payload.get("machineId").toString());
            LocalDate dateDebut = LocalDate.parse(payload.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(payload.get("dateFin").toString());
            String lieuLivraison = payload.containsKey("lieuLivraison") ? payload.get("lieuLivraison").toString()
                    : null;

            Utilisateur client = utilisateurService.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Machine machine = machineService.findById(machineId);
            if (machine == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Machine non trouvée");
                return ResponseEntity.badRequest().body(error);
            }

            if (dateFin.isBefore(dateDebut)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Date de fin invalide");
                return ResponseEntity.badRequest().body(error);
            }

            // Vérification des conflits
            List<ReservationMachine> conflits = reservationService.findConfList(machineId, dateDebut, dateFin);
            if (!conflits.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette machine est déjà réservée sur cette période");
                return ResponseEntity.badRequest().body(error);
            }

            // Création de la réservation
            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setLieuLivraison(lieuLivraison);

            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours == 0)
                jours = 1;
            reservation.setPrixTotal(machine.getPrixJour().multiply(BigDecimal.valueOf(jours)));

            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));

            ReservationMachine saved = reservationService.save(reservation);

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

    @GetMapping("/api/{id}/facturer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> facturerReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous devez être connecté");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            ReservationMachine reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous n'êtes pas autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Seules les réservations en attente peuvent être facturées");
                return ResponseEntity.badRequest().body(error);
            }

            if (reservation.getFacture() != null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette réservation a déjà une facture");
                return ResponseEntity.badRequest().body(error);
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/api/facture/{factureId}/payer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payerFacture(
            @PathVariable Long factureId,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {

        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous devez être connecté");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long modePaiementId = Long.valueOf(payload.get("modePaiementId").toString());

            Facture facture = factureService.findById(factureId);
            if (facture == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Facture non trouvée");
                return ResponseEntity.badRequest().body(error);
            }

            if (!facture.getClient().getIdUtilisateur().equals(clientId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous n'êtes pas autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            if (!"en_attente".equals(facture.getStatutFacture().getCode())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette facture a déjà été payée");
                return ResponseEntity.badRequest().body(error);
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/facture/{factureId}/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservationsByFacture(
            @PathVariable Long factureId,
            @AuthenticationPrincipal User user) {

        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous devez être connecté");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/api/{id}/annuler")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> annulerReservation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload,
            @AuthenticationPrincipal User user) {

        try {
            Long clientId = getClientId(user);
            if (clientId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous devez être connecté");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String motif = payload != null ? payload.get("motif") : null;

            ReservationMachine reservation = reservationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (reservation.getClient() == null || !reservation.getClient().getIdUtilisateur().equals(clientId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous n'êtes pas autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            String statut = reservation.getStatutReservation().getCode();
            if ("terminee".equals(statut) || "annulee".equals(statut)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cette réservation ne peut plus être annulée");
                return ResponseEntity.badRequest().body(error);
            }

            reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
            if (motif != null)
                reservation.setMotifRefus(motif);
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/api/client/{clientId}/annuler-tout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> annulerTout(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        try {
            Long clientIdConnecte = getClientId(user);
            if (clientIdConnecte == null || !clientIdConnecte.equals(clientId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Vous n'êtes pas autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ========== MÉTHODES PRIVÉES ==========

    private String genererNumeroFacture() {
        Facture last = factureService.findIdByLast();
        long nextId = last != null ? last.getIdFacture() + 1 : 1;
        return "FAC-" + LocalDate.now().getYear() + "-" + String.format("%04d", nextId);
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
}