package com.voly_saina.repository;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceMachineRepository extends JpaRepository<MaintenanceMachine, Long> {
    
    List<MaintenanceMachine> findByMachine(Machine machine);

    Page<MaintenanceMachine> findByMachine(Machine machine, Pageable pageable);
    
    List<MaintenanceMachine> findByStatutMaintenanceCode(String code);

    Page<MaintenanceMachine> findByStatutMaintenance_Code(String code, Pageable pageable);

    Page<MaintenanceMachine> findByMachine_IdMachine(Long idMachine, Pageable pageable);

    Page<MaintenanceMachine> findByMachine_IdMachineAndStatutMaintenance_Code(Long idMachine, String code, Pageable pageable);

}
