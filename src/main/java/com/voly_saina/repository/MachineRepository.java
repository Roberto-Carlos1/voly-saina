package com.voly_saina.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    
    @Query("SELECT DISTINCT m FROM Machine m JOIN m.statuts s WHERE s.etatMachine.code = :code")
    List<Machine> findByEtatMachineCode(String code);
    
    List<Machine> findByDisponibleTrue();
    
    @Query("SELECT m FROM Machine m WHERE m.typeMachine.libelle = :typeMachine")
    List<Machine> findByTypeMachine(String typeMachine);
    
    @Query("SELECT m FROM Machine m WHERE m.prixJour <= :prixMax AND m.disponible = true")
    List<Machine> findAvailableMachinesByMaxPrice(@Param("prixMax") BigDecimal prixMax);
    
    @Query("SELECT DISTINCT m FROM Machine m JOIN m.statuts s WHERE s.etatMachine.code = :code AND m.disponible = :disponible")
    List<Machine> findByEtatMachineCodeAndDisponible(@Param("code") String code, @Param("disponible") Boolean disponible);

    Page<Machine> findAll(Pageable pageable);

    @Query("""
            SELECT DISTINCT m
            FROM Machine m
            LEFT JOIN FETCH m.typeMachine
            LEFT JOIN FETCH m.statuts s
            LEFT JOIN FETCH s.etatMachine
            WHERE m.idMachine = :id
            """)
    Optional<Machine> findByIdWithRelations(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT m
            FROM Machine m
            LEFT JOIN FETCH m.typeMachine
            LEFT JOIN FETCH m.statuts s
            LEFT JOIN FETCH s.etatMachine
            WHERE m.idMachine IN :ids
            """)
    List<Machine> findByIdMachineInWithRelations(@Param("ids") List<Long> ids);
    
    
}
