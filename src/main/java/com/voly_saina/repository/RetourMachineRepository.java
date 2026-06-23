package com.voly_saina.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.RetourMachine;

@Repository
public interface RetourMachineRepository extends JpaRepository<RetourMachine, Long> {
    // recherche les retours par reservation
    @Query("SELECT r FROM RetourMachine r WHERE r.reservation.idReservation = :reservationId")
    RetourMachine findByReservationId(Long reservationId);

    // recherche les retours par machine (via la reservation)
    @Query("SELECT r FROM RetourMachine r WHERE r.reservation.machine.idMachine = :machineId")
    RetourMachine findByMachineId(Long machineId);

    // recherche les retours par client (via la reservation)
    @Query("SELECT r FROM RetourMachine r WHERE r.reservation.client.idUtilisateur = :clientId")
    RetourMachine findByClientId(Long clientId);

    // recherche les retours par un certain date
    @Query("SELECT r FROM RetourMachine r WHERE r.dateRetour = :dateRetour")
    RetourMachine findByDateRetour(java.time.LocalDate dateRetour);

    // rechercher les retours entre deux dates
    @Query("SELECT r FROM RetourMachine r WHERE r.dateRetour BETWEEN :startDate AND :endDate")
    List<RetourMachine> findByDateRetourBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);

    // recherche des retours avec penalite
    @Query("SELECT r FROM RetourMachine r WHERE r.penalite > 0")
    List<RetourMachine> findByPenaliteGreaterThanZero();

    // recherche des retours sans penalite
    @Query("SELECT r FROM RetourMachine r WHERE r.penalite = 0")
    List<RetourMachine> findByPenaliteEqualsZero();

    // compter les retours par mois
    @Query("SELECT MONTH(r.dateRetour), COUNT(r) FROM RetourMachine r GROUP BY MONTH(r.dateRetour)")
    List<Object[]> countRetoursByMonth();

    // compter les retours par client
    @Query("SELECT r.reservation.client.idUtilisateur, COUNT(r) FROM RetourMachine r GROUP BY r.reservation.client.idUtilisateur")
    List<Object[]> countRetoursByClient();

    // calculer la somme des penalites percues
    @Query("SELECT SUM(r.penalite) FROM RetourMachine r")
    Double sumPenalites();

    // calculer la somme des penalites sur une periode
    @Query("SELECT SUM(r.penalite) FROM RetourMachine r WHERE r.dateRetour BETWEEN :startDate AND :endDate")
    Double sumPenalitesByPeriod(java.time.LocalDate startDate, java.time.LocalDate endDate);

    // verifier si une machine a ete retournee
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RetourMachine r WHERE r.reservation.machine.idMachine = :machineId")
    boolean existsByMachineId(Long machineId);

    // verifier si un retour existe pour une reservation
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RetourMachine r WHERE r.reservation.idReservation = :reservationId")
    boolean existsByReservationId(Long reservationId);
}