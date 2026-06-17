package com.voly_saina.repository;

import com.voly_saina.entity.MaintenanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceMachineRepository extends JpaRepository<MaintenanceMachine, Long> {
    
    List<MaintenanceMachine> findByMachineIdMachine(Long machineId);
    
    List<MaintenanceMachine> findByStatut(String statut);
    
    // List<Maintenance> 
}