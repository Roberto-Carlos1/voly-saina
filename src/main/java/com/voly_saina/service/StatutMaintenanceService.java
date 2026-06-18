package com.voly_saina.service;

import com.voly_saina.entity.StatutMaintenance;
import com.voly_saina.repository.StatutMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutMaintenanceService {

    @Autowired
    private StatutMaintenanceRepository statutMaintenanceRepository;

    public List<StatutMaintenance> findAll() {
        return statutMaintenanceRepository.findAll();
    }

    public Optional<StatutMaintenance> findById(Long id) {
        return statutMaintenanceRepository.findById(id);
    }

    public StatutMaintenance save(StatutMaintenance statutMaintenance) {
        return statutMaintenanceRepository.save(statutMaintenance);
    }

    public boolean existsById(Long id) {
        return statutMaintenanceRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutMaintenanceRepository.deleteById(id);
    }
}
