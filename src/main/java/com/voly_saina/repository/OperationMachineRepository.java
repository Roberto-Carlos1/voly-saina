package com.voly_saina.repository;

import com.voly_saina.entity.OperationMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationMachineRepository extends JpaRepository<OperationMachine, Long> {
}
