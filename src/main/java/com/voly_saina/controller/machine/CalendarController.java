package com.voly_saina.controller.machine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ReservationMachineService;

@Controller
@RequestMapping("/admin/reservations-machine")
public class CalendarController {

    private final ReservationMachineService reservationMachineService;
    private final MachineService machineService;

    public CalendarController(ReservationMachineService reservationMachineService,
            MachineService machineService) {
        this.reservationMachineService = reservationMachineService;
        this.machineService = machineService;
    }

    // Page du calendrier (rendu Thymeleaf)
    @GetMapping("/calendrier")
    public String showCalendrier(Model model) {
        model.addAttribute("pageTitle", "Calendrier des réservations");
        model.addAttribute("activeMenu", "calendrier");
        // nombre total de machines : sert au calcul du taux d'occupation côté client
        model.addAttribute("totalMachines", machineService.findAll().size());
        return "reservation/calendrier";
    }

    /**
     * Données des réservations au format JSON, consommées par le calendrier vanilla.
     * On expose uniquement les champs nécessaires à l'affichage (pas d'entité brute),
     * sans toucher à la logique métier.
     */
    @GetMapping("/calendrier/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {
        List<Map<String, Object>> events = new ArrayList<>();

        for (ReservationMachine r : reservationMachineService.findAll()) {
            if (r.getDateDebut() == null || r.getDateFin() == null) {
                continue;
            }
            Map<String, Object> ev = new LinkedHashMap<>();
            ev.put("id", r.getIdReservation());
            ev.put("client", r.getClient() != null ? r.getClient().getNom() : "—");
            ev.put("machine", r.getMachine() != null ? r.getMachine().getNom() : "—");
            ev.put("start", r.getDateDebut().toString());   // ISO yyyy-MM-dd
            ev.put("end", r.getDateFin().toString());
            ev.put("statutCode", r.getStatutReservation() != null ? r.getStatutReservation().getCode() : "");
            ev.put("statutLibelle", r.getStatutReservation() != null ? r.getStatutReservation().getLibelle() : "—");
            ev.put("prix", r.getPrixTotal());
            ev.put("lieu", r.getLieuLivraison() != null ? r.getLieuLivraison() : "—");
            events.add(ev);
        }
        return events;
    }
}
