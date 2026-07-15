package com.voly_saina.repository;

import com.voly_saina.entity.StatutMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutMachineRepository extends JpaRepository<StatutMachine, Long> {

    @Query(value = """
            SELECT *
            FROM voly_saina.statut_machine
            WHERE id_machine = :idMachine
            ORDER BY date_creation DESC, id DESC
            LIMIT 1
            """, nativeQuery = true)
    StatutMachine findCurrentMachine(@Param("idMachine") Long idMachine);
}
