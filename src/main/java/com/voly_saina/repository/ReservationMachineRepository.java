package com.voly_saina.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.ReservationMachine;

@Repository
public interface ReservationMachineRepository extends JpaRepository<ReservationMachine, Long> {
    // rechercher par client
    List<ReservationMachine> findByClientIdUtilisateur(Long clientId);
    
    // rechercher par machine
    List<ReservationMachine> findByMachineIdMachine(Long machineId);
    
    // rechercher par status
    List<ReservationMachine> findByStatutReservationCode(String code);
    
    // rechercher des reservation en conflit de dates
    @Query("SELECT r FROM ReservationMachine r " +
        "WHERE r.machine.idMachine = :machineId " +
        "AND r.statutReservation.code NOT IN ('annulee', 'refusee') " +
        "AND ((:dateDebut BETWEEN r.dateDebut AND r.dateFin) " +
        "     OR (:dateFin BETWEEN r.dateDebut AND r.dateFin) " +
        "     OR (r.dateDebut BETWEEN :dateDebut AND :dateFin))")
    List<ReservationMachine> findConflictingReservations(
        @Param("machineId") Long machineId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin);
    
    // rechercher par client et par status de reservation
    @Query("SELECT r FROM ReservationMachine r WHERE r.client.idUtilisateur = :clientId AND r.statutReservation.code = :code")
    List<ReservationMachine> findByClientAndStatutReservationCode(@Param("clientId") Long clientId, @Param("code") String code);

    // recherche des reservation actives d'un client
    @Query("SELECT r FROM ReservationMachine r WHERE r.client.idUtilisateur = :clientId AND r.statutReservation.code NOT IN ('refusee', 'annulee', 'terminee')")
    List<ReservationMachine> findActiveReservationsByClient(@Param("clientId") Long clientId);

    // recherche des reservation en attente de validation
    @Query("SELECT r FROM ReservationMachine r WHERE r.statutReservation.code = 'en_attente'")
    List<ReservationMachine> findReservationsEnAttente();

    // recherche des reservation en cours
    @Query("SELECT r FROM ReservationMachine r WHERE r.statutReservation.code = 'en_cours'")
    List<ReservationMachine> findReservationsEnCours();

    // recherche des reservation d'une machine sur une periode
    @Query("SELECT r FROM ReservationMachine r WHERE r.machine.idMachine = :machineId " +
        "AND r.dateDebut <= :dateFin AND r.dateFin >= :dateDebut")
    List<ReservationMachine> findReservationsByMachineAndPeriod(
        @Param("machineId") Long machineId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );

    // recherche des reservation annule
    @Query("SELECT r FROM ReservationMachine r WHERE r.statutReservation.code = 'annulee'")
    List<ReservationMachine> findReservationSAnnule();

    List<ReservationMachine> findByClientIdUtilisateurAndDateDebutBetweenAndStatutReservationCodeIn(Long idClient, LocalDate debut, LocalDate fin, List<String> statuts);

    @Query("select r from ReservationMachine r where r.facture.idFacture = :idFacture")
    ReservationMachine findByIdFacture(@Param("idFacture") Long idFacture);
}
