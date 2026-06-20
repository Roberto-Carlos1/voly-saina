package com.voly_saina.repository;

import com.voly_saina.entity.Machine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    
    List<Machine> findByEtatMachineCode(String code);
    
    List<Machine> findByDisponibleTrue();
    
    List<Machine> findByTypeMachine(String typeMachine);
    
    @Query("SELECT m FROM Machine m WHERE m.prixJour <= :prixMax AND m.disponible = true")
    List<Machine> findAvailableMachinesByMaxPrice(@Param("prixMax") BigDecimal prixMax);
    
    @Query("SELECT m FROM Machine m WHERE m.etatMachine.code = :code AND m.disponible = :disponible")
    List<Machine> findByEtatMachineCodeAndDisponible(@Param("code") String code, @Param("disponible") Boolean disponible);

    Page<Machine> findAll(Pageable pageable);
    
}
