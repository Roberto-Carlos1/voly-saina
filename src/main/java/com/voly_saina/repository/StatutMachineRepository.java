package com.voly_saina.repository;

import com.voly_saina.entity.StatutMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutMachineRepository extends JpaRepository<StatutMachine, Long> {

    StatutMachine findTopByMachineIdMachineOrderByDateCreationDescIdDesc(Long idMachine);
}
