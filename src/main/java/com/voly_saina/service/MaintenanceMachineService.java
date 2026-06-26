package com.voly_saina.service;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.StatutMachine;
import com.voly_saina.repository.EtatMachineRepository;
import com.voly_saina.repository.MachineRepository;
import com.voly_saina.repository.MaintenanceMachineRepository;
import com.voly_saina.repository.StatutMachineRepository;
import com.voly_saina.repository.StatutMaintenanceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceMachineService {

    private final MaintenanceMachineRepository maintenanceMachineRepository;
    private final EtatMachineRepository etatMachineRepository;
    private final StatutMachineRepository statutMachineRepository;
    private final MachineRepository machineRepository;
    private final StatutMaintenanceRepository statutMaintenanceRepository;

    public MaintenanceMachineService(StatutMachineService statutMachineService,
            MaintenanceMachineRepository maintenanceMachineRepository,
            EtatMachineRepository etatMachineRepository,
            StatutMachineRepository statutMachineRepository,
            MachineRepository machineRepository,
            StatutMaintenanceRepository statutMaintenanceRepository) {
        this.maintenanceMachineRepository = maintenanceMachineRepository;
        this.etatMachineRepository = etatMachineRepository;
        this.statutMachineRepository = statutMachineRepository;
        this.machineRepository = machineRepository;
        this.statutMaintenanceRepository = statutMaintenanceRepository;
    }

    // create a maintenace machine
    public MaintenanceMachine createMaintenanceMachine(Long idMachine, String travaux, double cout,
            LocalDate dateCreation) {
        MaintenanceMachine maintenanceMachine = new MaintenanceMachine();
        maintenanceMachine.setDateDebut(dateCreation);
        maintenanceMachine.setDateRetourPrevue(dateCreation.plusDays(7));
        maintenanceMachine.setTravaux(travaux);
        maintenanceMachine.setCout(BigDecimal.valueOf(cout));
        maintenanceMachine.setMachine(machineRepository.findById(idMachine).orElse(null));
        maintenanceMachine.setStatutMaintenance(statutMaintenanceRepository.findByCode("prevue").orElse(null));

        StatutMachine status = new StatutMachine();
        status.setMachine(machineRepository.findById(idMachine).orElse(null));
        status.setEtatMachine(etatMachineRepository.findByCode("maintenance").orElse(null));
        status.setDateCreation(dateCreation);
        statutMachineRepository.save(status);

        return maintenanceMachineRepository.save(maintenanceMachine);
    }

    // validate maintenance machine
    public MaintenanceMachine validateMaintenanceMachine(Long idMaintenance, Long idMachine,
            LocalDate dateRetourReelle) {
        Optional<MaintenanceMachine> optionalMaintenanceMachine = maintenanceMachineRepository.findById(idMaintenance);
        if (optionalMaintenanceMachine.isPresent()) {
            MaintenanceMachine maintenanceMachine = optionalMaintenanceMachine.get();
            maintenanceMachine.setDateRetourReelle(dateRetourReelle);
            maintenanceMachine.setStatutMaintenance(statutMaintenanceRepository.findByCode("terminee").orElse(null));

            StatutMachine status = new StatutMachine();
            status.setMachine(machineRepository.findById(idMachine).orElse(null));
            status.setEtatMachine(etatMachineRepository.findByCode("disponible").orElse(null));
            status.setDateCreation(dateRetourReelle);
            statutMachineRepository.save(status);
            return maintenanceMachineRepository.save(maintenanceMachine);
        } else {
            throw new RuntimeException("Maintenance machine not found with id: " + idMaintenance);
        }
    }

    public List<MaintenanceMachine> findAll() {
        return maintenanceMachineRepository.findAll();
    }

    public Page<MaintenanceMachine> findAllByPage(Pageable pageable) {
        return maintenanceMachineRepository.findAll(pageable);
    }

    public Page<MaintenanceMachine> findByMachinePage(Machine machine, Pageable pageable) {
        return maintenanceMachineRepository.findByMachine(machine, pageable);
    }

    public Page<MaintenanceMachine> findByStatutCode(String code, Pageable pageable) {
        return maintenanceMachineRepository.findByStatutMaintenance_Code(code, pageable);
    }

    public Page<MaintenanceMachine> findByMachineAndStatutCode(Long idMachine, String code, Pageable pageable) {
        return maintenanceMachineRepository.findByMachine_IdMachineAndStatutMaintenance_Code(idMachine, code, pageable);
    }

    public Page<MaintenanceMachine> findByMachineId(Long idMachine, Pageable pageable) {
        return maintenanceMachineRepository.findByMachine_IdMachine(idMachine, pageable);
    }

    public Optional<MaintenanceMachine> findById(Long id) {
        return maintenanceMachineRepository.findById(id);
    }

    public List<MaintenanceMachine> findByMachine(Machine machineId) {
        return maintenanceMachineRepository.findByMachine(machineId);
    }

    public MaintenanceMachine save(MaintenanceMachine maintenanceMachine) {
        return maintenanceMachineRepository.save(maintenanceMachine);
    }

    public boolean existsById(Long id) {
        return maintenanceMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        maintenanceMachineRepository.deleteById(id);
    }
}
