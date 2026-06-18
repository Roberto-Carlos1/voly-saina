package com.voly_saina.service;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.repository.ReservationMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
