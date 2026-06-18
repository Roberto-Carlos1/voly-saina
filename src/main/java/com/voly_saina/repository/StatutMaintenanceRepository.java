package com.voly_saina.repository;

import com.voly_saina.entity.StatutMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutMaintenanceRepository extends JpaRepository<StatutMaintenance, Long> {
}
