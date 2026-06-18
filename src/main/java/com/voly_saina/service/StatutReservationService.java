package com.voly_saina.service;

import com.voly_saina.entity.StatutReservation;
import com.voly_saina.repository.StatutReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutReservationService {

    @Autowired
    private StatutReservationRepository statutReservationRepository;

    public List<StatutReservation> findAll() {
        return statutReservationRepository.findAll();
    }

    public Optional<StatutReservation> findById(Long id) {
        return statutReservationRepository.findById(id);
    }

    public StatutReservation save(StatutReservation statutReservation) {
        return statutReservationRepository.save(statutReservation);
    }

    public boolean existsById(Long id) {
        return statutReservationRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutReservationRepository.deleteById(id);
    }
}
