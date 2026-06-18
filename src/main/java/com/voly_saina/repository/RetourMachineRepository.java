package com.voly_saina.repository;

import com.voly_saina.entity.RetourMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetourMachineRepository extends JpaRepository<RetourMachine, Long> {
}
