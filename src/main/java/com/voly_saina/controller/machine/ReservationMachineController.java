package com.voly_saina.controller.machine;

import java.util.List;

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

    // GET /api/reservations-machine
    @GetMapping
    public String getAllReservations(Model model) {
        List<ReservationMachine> reservations = reservationMachineService.findAll();
        model.addAttribute("reservations", reservations);
        return "reservation/list";
    }

    // GET /api/reservations-machine/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationMachine> getById(@PathVariable Long id) {
        return reservationMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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
