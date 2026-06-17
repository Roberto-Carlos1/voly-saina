package com.voly_saina.repository;

import com.voly_saina.entity.ReservationMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationMachineRepository extends JpaRepository<ReservationMachine, Long> {
    
    List<ReservationMachine> findByClientIdUtilisateur(Long clientId);
    
    List<ReservationMachine> findByMachineIdMachine(Long machineId);
    
    List<ReservationMachine> findByStatut(String statut);
    
    @Query("SELECT r FROM ReservationMachine r WHERE r.machine.idMachine = :machineId " +
           "AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut " +
           "AND r.statut NOT IN ('refusee', 'annulee', 'terminee')")
    List<ReservationMachine> findConflictingReservations(
        @Param("machineId") Long machineId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    @Query("SELECT r FROM ReservationMachine r WHERE r.client.idUtilisateur = :clientId AND r.statut = :statut")
    List<ReservationMachine> findByClientAndStatut(@Param("clientId") Long clientId, @Param("statut") String statut);
}