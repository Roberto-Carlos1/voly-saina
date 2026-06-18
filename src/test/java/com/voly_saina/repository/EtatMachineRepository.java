package com.voly_saina.repository;

import com.voly_saina.entity.EtatMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtatMachineRepository extends JpaRepository<EtatMachine, Long> {
}
