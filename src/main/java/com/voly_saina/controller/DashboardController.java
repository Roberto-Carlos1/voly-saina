package com.voly_saina.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.MaintenanceMachineService;
import com.voly_saina.service.ProduitService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutMachineService;
import com.voly_saina.service.UtilisateurService;

// DASHBOARD
@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private static final Locale FR = Locale.FRENCH;

    private final MachineService machineService;
    private final StatutMachineService statutMachineService;
    private final ReservationMachineService reservationMachineService;
    private final FactureService factureService;
    private final MaintenanceMachineService maintenanceMachineService;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;

    public DashboardController(MachineService machineService, StatutMachineService statutMachineService,
            ReservationMachineService reservationMachineService, FactureService factureService,
            MaintenanceMachineService maintenanceMachineService, ProduitService produitService,
            UtilisateurService utilisateurService) {
        this.machineService = machineService;
        this.statutMachineService = statutMachineService;
        this.reservationMachineService = reservationMachineService;
        this.factureService = factureService;
        this.maintenanceMachineService = maintenanceMachineService;
        this.produitService = produitService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String dashboard(Model model) {
        buildFlotte(model);
        buildReservations(model);
        buildFinances(model);
        buildMaintenances(model);
        buildStock(model);
        buildClients(model);

        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("pageTitle", "Tableau de bord");
        return "admin/dashboard";
    }

    // RECUPERATION MACHINE 

    private void buildFlotte(Model model) {
        List<Machine> machines = machineService.findAll();
        int total = machines.size();
        int louees = 0;
        int disponibles = 0;
        int maintenance = 0;
        int horsService = 0;

        List<Map<String, Object>> flotte = new ArrayList<>();
        for (Machine m : machines) {
            StatutMachine statut = statutMachineService.findCurrentByMachineId(m.getIdMachine());
            String code = "disponible";
            String libelle = "Disponible";
            if (statut != null && statut.getEtatMachine() != null) {
                code = statut.getEtatMachine().getCode();
                libelle = statut.getEtatMachine().getLibelle();
            }
            switch (code) {
                case "louee" -> louees++;
                case "maintenance" -> maintenance++;
                case "hors_service" -> horsService++;
                default -> disponibles++;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("nom", m.getNom());
            row.put("type", m.getTypeMachine() != null ? m.getTypeMachine().getLibelle() : "—");
            row.put("etatCode", code);
            row.put("etatLibelle", libelle);
            row.put("localisation", m.getLocalisation() != null ? m.getLocalisation() : "—");
            row.put("prixJour", fmt(m.getPrixJour()));
            flotte.add(row);
        }

        int taux = total > 0 ? (int) Math.round(louees * 100.0 / total) : 0;

        model.addAttribute("totalMachines", total);
        model.addAttribute("machinesLouees", louees);
        model.addAttribute("machinesDisponibles", disponibles);
        model.addAttribute("machinesMaintenance", maintenance);
        model.addAttribute("machinesHorsService", horsService);
        model.addAttribute("tauxUtilisation", taux);
        model.addAttribute("flotte", flotte.stream().limit(8).toList());
    }

    /* ------------------------------------------------------------------ */
    /* Réservations                                                        */
    /* ------------------------------------------------------------------ */
    private void buildReservations(Model model) {
        List<ReservationMachine> reservations = reservationMachineService.findAll();

        long actives = reservations.stream().filter(r -> {
            String c = codeReservation(r);
            return c.equals("validee") || c.equals("en_cours");
        }).count();
        long enAttente = reservations.stream().filter(r -> codeReservation(r).equals("en_attente")).count();

        List<Map<String, Object>> dernieres = reservations.stream()
                .sorted(Comparator.comparing(ReservationMachine::getDateCreation,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(r -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("client", r.getClient() != null ? r.getClient().getNom() : "—");
                    row.put("machine", r.getMachine() != null ? r.getMachine().getNom() : "—");
                    row.put("dateDebut", r.getDateDebut());
                    row.put("dateFin", r.getDateFin());
                    row.put("statutCode", codeReservation(r));
                    row.put("statutLibelle", r.getStatutReservation() != null
                            ? r.getStatutReservation().getLibelle() : "—");
                    row.put("prixTotal", fmt(r.getPrixTotal()));
                    return row;
                })
                .toList();

        model.addAttribute("reservationsActives", actives);
        model.addAttribute("reservationsEnAttente", enAttente);
        model.addAttribute("reservationsTotal", reservations.size());
        model.addAttribute("dernieresReservations", dernieres);
    }

    /* ------------------------------------------------------------------ */
    /* calcul anah CA (pseudo ca facture)                                   */
    /* ------------------------------------------------------------------ */
    private void buildFinances(Model model) {
        List<Facture> factures = factureService.findAll();

        BigDecimal caTotal = BigDecimal.ZERO;
        BigDecimal totalPaye = BigDecimal.ZERO;
        Map<YearMonth, BigDecimal> parMois = new LinkedHashMap<>();

        for (Facture f : factures) {
            BigDecimal montant = f.getMontantTotal() != null ? f.getMontantTotal() : BigDecimal.ZERO;
            BigDecimal paye = f.getMontantPaye() != null ? f.getMontantPaye() : BigDecimal.ZERO;
            caTotal = caTotal.add(montant);
            totalPaye = totalPaye.add(paye);
            LocalDateTime date = f.getDateFacture();
            if (date != null) {
                parMois.merge(YearMonth.from(date), montant, BigDecimal::add);
            }
        }

        // 6 derniers mois
        YearMonth courant = YearMonth.now();
        List<BigDecimal> valeurs = new ArrayList<>();
        List<Map<String, Object>> revenus = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = courant.minusMonths(i);
            valeurs.add(parMois.getOrDefault(ym, BigDecimal.ZERO));
        }
        BigDecimal max = valeurs.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = courant.minusMonths(i);
            BigDecimal montant = parMois.getOrDefault(ym, BigDecimal.ZERO);
            int pct = max.signum() > 0
                    ? montant.multiply(BigDecimal.valueOf(100)).divide(max, 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("label", ym.getMonth().getDisplayName(TextStyle.SHORT, FR).toUpperCase(FR));
            row.put("montant", fmt(montant));
            row.put("pct", pct);
            revenus.add(row);
        }

        BigDecimal caMois = parMois.getOrDefault(courant, BigDecimal.ZERO);
        BigDecimal caPrec = parMois.getOrDefault(courant.minusMonths(1), BigDecimal.ZERO);
        Integer variation = null;
        if (caPrec.signum() > 0) {
            variation = caMois.subtract(caPrec)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(caPrec, 0, RoundingMode.HALF_UP).intValue();
        }

        model.addAttribute("caTotal", fmt(caTotal));
        model.addAttribute("caMois", fmt(caMois));
        model.addAttribute("caPrec", fmt(caPrec));
        model.addAttribute("caReste", fmt(caTotal.subtract(totalPaye)));
        model.addAttribute("caVariation", variation);
        model.addAttribute("revenus", revenus);
        model.addAttribute("nombreFactures", factures.size());
    }

    /* ------------------------------------------------------------------ */
    /* Maintenances (alertes)                                              */
    /* ------------------------------------------------------------------ */
    private void buildMaintenances(Model model) {
        List<MaintenanceMachine> maintenances = maintenanceMachineService.findAll();

        List<Map<String, Object>> alertes = maintenances.stream()
                .filter(mm -> {
                    String c = codeMaintenance(mm);
                    return c.equals("prevue") || c.equals("en_cours");
                })
                .sorted(Comparator.comparing(MaintenanceMachine::getDateDebut,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .map(mm -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("machine", mm.getMachine() != null ? mm.getMachine().getNom() : "—");
                    row.put("travaux", mm.getTravaux() != null ? mm.getTravaux() : "Intervention planifiée");
                    row.put("statutCode", codeMaintenance(mm));
                    row.put("statutLibelle", mm.getStatutMaintenance() != null
                            ? mm.getStatutMaintenance().getLibelle() : "—");
                    row.put("dateDebut", mm.getDateDebut());
                    return row;
                })
                .toList();

        model.addAttribute("alertesMaintenance", alertes);
        model.addAttribute("nombreAlertes", alertes.size());
    }

    /* ------------------------------------------------------------------ */
    /* Stock produits                                                      */
    /* ------------------------------------------------------------------ */
    private void buildStock(Model model) {
        List<Produit> produits = produitService.findActifs();
        long stockFaible = produits.stream()
                .filter(p -> p.getStock() != null && p.getSeuilStock() != null
                        && p.getStock().compareTo(p.getSeuilStock()) <= 0)
                .count();
        model.addAttribute("totalProduits", produits.size());
        model.addAttribute("produitsStockFaible", stockFaible);
    }

    /* ------------------------------------------------------------------ */
    /* Clients                                                             */
    /* ------------------------------------------------------------------ */
    private void buildClients(Model model) {
        List<Utilisateur> utilisateurs = utilisateurService.findAll();
        long clients = utilisateurs.stream()
                .filter(u -> u.getRole() != null && "client".equalsIgnoreCase(u.getRole().getCode()))
                .count();
        model.addAttribute("totalClients", clients);
    }

    /* ------------------------------------------------------------------ */
    /* Helpers                                                             */
    /* ------------------------------------------------------------------ */
    private String codeReservation(ReservationMachine r) {
        return r.getStatutReservation() != null && r.getStatutReservation().getCode() != null
                ? r.getStatutReservation().getCode() : "";
    }

    private String codeMaintenance(MaintenanceMachine mm) {
        return mm.getStatutMaintenance() != null && mm.getStatutMaintenance().getCode() != null
                ? mm.getStatutMaintenance().getCode() : "";
    }

    /** Formate un montant en séparant les milliers par une espace (ex. 142 500). */
    private String fmt(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return String.format(Locale.US, "%,.0f", value).replace(',', ' ');
    }
}
