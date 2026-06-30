package com.voly_saina.controller.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.service.MaintenanceMachineService;
import com.voly_saina.service.ReservationMachineService;

@Controller
@RequestMapping("/api/reservations-machine")
public class CalendarController {

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    @GetMapping("/calendrier")
    public String showCalendrier() {
        return "reservation/calendrier";
    }

    @GetMapping("/calendrier/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {

        List<Map<String, Object>> events = new ArrayList<>();

        for (ReservationMachine r : reservationMachineService.findAll()) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", "Reservation: " + r.getMachine().getNom());
            event.put("start", r.getDateDebut());
            event.put("end", r.getDateFin().plusDays(1));
            event.put("backgroundColor", "#3788d8");
            events.add(event);
        }


        return events;
    }
}
