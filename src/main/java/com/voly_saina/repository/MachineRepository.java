package com.voly_saina.repository;

import java.math.BigDecimal;
import java.util.List;

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
        // recherche par code d'etat
        List<Machine> findByEtatMachineCode(String code);

        // recherche des machines disponibles (en utilisant l'etat seulement)
        List<Machine> findByDisponibleTrue();

        @Query("SELECT m FROM Machine m WHERE m.typeMachine.libelle = :typeMachine")
        // recherche des machines disponibles par type
        List<Machine> findByTypeMachine(String typeMachine);

        // recherche des machines avec des prix de location inférieurs ou égaux à un
        // certain montant
        @Query("SELECT m FROM Machine m WHERE m.prixJour <= :prixMax AND m.disponible = true")
        List<Machine> findAvailableMachinesByMaxPrice(@Param("prixMax") BigDecimal prixMax);

        // recherche des machines disponibles par etat et type
        @Query("SELECT m FROM Machine m JOIN m.statuts s WHERE s.etatMachine.code = 'disponible' AND m.disponible = true")
        List<Machine> findAvailableMachines();

        // recherche des machines disponibles par etat, type et prix
        @Query("SELECT m FROM Machine m JOIN m.statuts s WHERE s.etatMachine.code = :code AND m.typeMachine = :typeMachine AND m.prixJour <= :prixMax AND m.disponible = true")
        List<Machine> findAvailableMachinesByEtatTypeAndMaxPrice(@Param("code") String code,
                        @Param("typeMachine") String typeMachine, @Param("prixMax") BigDecimal prixMax);

        // compter les machines par etat
        @Query("SELECT s.etatMachine.code, COUNT(m) FROM Machine m JOIN m.statuts s GROUP BY s.etatMachine.code")
        List<Object[]> countMachinesByEtat();

        // recher les machines non disponibles par etat
        @Query("SELECT m FROM Machine m JOIN m.statuts s WHERE s.etatMachine.code = :code AND m.disponible = false")
        List<Machine> findUnavailableMachinesByEtat(@Param("code") String code);

        // recherche des machines par typeid
        @Query("SELECT m FROM Machine m WHERE m.typeMachine.idTypeMachine = :typeId")
        List<Machine> findByTypeMachineId(@Param("typeId") Long typeId);

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
