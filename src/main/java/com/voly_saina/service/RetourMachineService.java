package com.voly_saina.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.repository.RetourMachineRepository;

@Service
public class RetourMachineService {

    @Autowired
    private RetourMachineRepository retourRepository;
    
    @Autowired
    private ReservationMachineService reservationService;
    
    @Autowired
    private MachineService machineService;
    
    @Autowired
    private EtatMachineService etatMachineService;
    
    @Autowired
    private StatutReservationService statutReservationService;

    // ========== CRUD ==========
    
    public List<RetourMachine> findAll() {
        return retourRepository.findAll();
    }

    public Optional<RetourMachine> findById(Long id) {
        return retourRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return retourRepository.existsById(id);
    }

    public RetourMachine save(RetourMachine retour) {
        return retourRepository.save(retour);
    }

    public void deleteById(Long id) {
        retourRepository.deleteById(id);
    }

    // ========== Recherches ==========
    
    public RetourMachine findByReservation(Long reservationId) {
        return retourRepository.findByReservationId(reservationId);
    }

    public RetourMachine findByMachine(Long machineId) {
        return retourRepository.findByMachineId(machineId);
    }

    public RetourMachine findByClient(Long clientId) {
        return retourRepository.findByClientId(clientId);
    }

    public List<RetourMachine> findByDateRetourBetween(LocalDate startDate, LocalDate endDate) {
        return retourRepository.findByDateRetourBetween(startDate, endDate);
    }

    public List<RetourMachine> findRetoursWithPenalite() {
        return retourRepository.findByPenaliteGreaterThanZero();
    }

    // ========== Enregistrer un retour ==========
    public RetourMachine enregistrerRetour(RetourMachine retour) {
        // Récupérer la réservation
        ReservationMachine reservation = reservationService
            .findById(retour.getReservation().getIdReservation())
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        // Date de retour
        if (retour.getDateRetour() == null) {
            retour.setDateRetour(LocalDate.now());
        }
        
        // Calcul et sauvegarde de la pénalité
        long joursRetard = ChronoUnit.DAYS.between(reservation.getDateFin(), retour.getDateRetour());
        retour.setPenalite(joursRetard > 0 ? 
            reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(joursRetard)).multiply(BigDecimal.valueOf(1.5)) : 
            BigDecimal.ZERO);
        
        RetourMachine saved = retourRepository.save(retour);
        
        // Mise à jour statut réservation
        reservation.setStatutReservation(statutReservationService.findByCode("terminee"));
        reservationService.save(reservation);
        
        // Mise à jour état machine
        Machine machine = reservation.getMachine();
        machine.getStatutMachine().setEtatMachine(etatMachineService.findByCode("disponible").orElse(null));
        machineService.save(machine);
        
        return saved;
    }
}