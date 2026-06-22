package com.voly_saina.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.StatutReservation;

@Repository
public interface StatutReservationRepository extends JpaRepository<StatutReservation, Long> {
    // recherche par code
    StatutReservation findByCode(String code);
}
