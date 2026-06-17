package com.voly.saina.repository;

import com.voly.saina.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    
    List<Machine> findByEtat(String etat);
    
    List<Machine> findByDisponibleTrue();
    
    List<Machine> findByTypeMachine(String typeMachine);
    
    @Query("SELECT m FROM Machine m WHERE m.prixJour <= :prixMax AND m.disponible = true")
    List<Machine> findAvailableMachinesByMaxPrice(@Param("prixMax") BigDecimal prixMax);
    
    @Query("SELECT m FROM Machine m WHERE m.etat = :etat AND m.disponible = :disponible")
    List<Machine> findByEtatAndDisponible(@Param("etat") String etat, @Param("disponible") Boolean disponible);
}