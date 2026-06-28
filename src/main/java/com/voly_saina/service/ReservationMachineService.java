package com.voly_saina.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.repository.ReservationMachineRepository;

@Service
public class ReservationMachineService {

    @Autowired
    private ReservationMachineRepository reservationMachineRepository;

    public List<ReservationMachine> findAll() {
        return reservationMachineRepository.findAll();
    }

    public Optional<ReservationMachine> findById(Long id) {
        return reservationMachineRepository.findById(id);
    }

    public ReservationMachine save(ReservationMachine reservationMachine) {
        return reservationMachineRepository.save(reservationMachine);
    }

    public boolean existsById(Long id) {
        return reservationMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        reservationMachineRepository.deleteById(id);
    }

    public List<ReservationMachine> findMachine(Long id){
        return reservationMachineRepository.findByMachineIdMachine(id);
    }
    
    public List<ReservationMachine> findByClientId(Long clientId) {
        return reservationMachineRepository.findByClientIdUtilisateur(clientId);
    }

    public List<ReservationMachine> findActiveReservationsByClient(Long clientId) {
        return reservationMachineRepository.findActiveReservationsByClient(clientId);
    }

    public List<ReservationMachine> findConfList(Long machineId,LocalDate dateDebut, LocalDate dateFin) {
        if (machineId == null || machineId <= 0) {
            throw new IllegalArgumentException("ID machine invalide");
        }
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates ne peuvent pas etre nulles");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas etre avant la date de debut");
        }
        
        return reservationMachineRepository.findConflictingReservations(machineId, dateDebut, dateFin);
    }

    public boolean isMachineAvailable(Long machineId, LocalDate dateDebut, LocalDate dateFin) {
        List<ReservationMachine> conflits = findConfList(machineId, dateDebut, dateFin);
        return conflits.isEmpty();
    }

    public List<ReservationMachine> findByStatutReservationAndCode(String code) {
        return reservationMachineRepository.findByStatutReservationCode(code);
    }
}
