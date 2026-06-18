package com.voly_saina.repository;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceMachineRepository extends JpaRepository<MaintenanceMachine, Long> {
    
    List<MaintenanceMachine> findByMachine(Machine machine);
    
    List<MaintenanceMachine> findByStatutMaintenanceCode(String code);


}
