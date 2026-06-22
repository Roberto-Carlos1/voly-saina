package com.voly_saina.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.EtatMachine;

@Repository
public interface EtatMachineRepository extends JpaRepository<EtatMachine, Long> {
    // recherche par code
    Optional<EtatMachine> findByCode(String code);
}
