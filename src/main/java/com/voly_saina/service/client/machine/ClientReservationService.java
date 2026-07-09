package com.voly_saina.service.client.machine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.repository.ReservationMachineRepository;
import com.voly_saina.service.StatutReservationService;

@Service
public class ClientReservationService {

    @Autowired
    private ReservationMachineRepository reservationRepository;

    @Autowired
    private StatutReservationService statutReservationService;

    public List<ReservationClientDTO> getReservationsClient(Long clientId) {
        List<ReservationMachine> reservations = reservationRepository
            .findByClientIdUtilisateur(clientId);
        
        return reservations.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<ReservationClientDTO> getReservationsClientByStatut(Long clientId, String statut) {
        List<ReservationMachine> reservations = reservationRepository
            .findByClientAndStatutReservationCode(clientId, statut);
        
        return reservations.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public ReservationClientDTO getReservationDetail(Long reservationId, Long clientId) {
        ReservationMachine reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé");
        }
        
        return mapToDTO(reservation);
    }

    private ReservationClientDTO mapToDTO(ReservationMachine reservation) {
        ReservationClientDTO dto = new ReservationClientDTO();
        dto.setIdReservation(reservation.getIdReservation());
        dto.setIdMachine(reservation.getMachine().getIdMachine());
        dto.setMachineNom(reservation.getMachine().getNom());
        dto.setMachineType(reservation.getMachine().getTypeMachine() != null ? 
            reservation.getMachine().getTypeMachine().getLibelle() : "Non défini");
        dto.setPrixJour(reservation.getMachine().getPrixJour());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setLieuLivraison(reservation.getLieuLivraison());
        dto.setPrixTotal(reservation.getPrixTotal());
        
        if (reservation.getStatutReservation() != null) {
            dto.setStatut(reservation.getStatutReservation().getCode());
            dto.setStatutLibelle(reservation.getStatutReservation().getLibelle());
        }
        
        dto.setMotifRefus(reservation.getMotifRefus());
        dto.setDateCreation(reservation.getDateCreation());
        
        // Calcul des actions possibles
        String statut = reservation.getStatutReservation() != null ? 
            reservation.getStatutReservation().getCode() : "";
        
        dto.setPeutAnnuler("en_attente".equals(statut) || "validee".equals(statut));
        dto.setPeutRetourner("en_cours".equals(statut) || "validee".equals(statut));
        dto.setEstTerminee("terminee".equals(statut) || "annulee".equals(statut));
        
        return dto;
    }
    
    public void annulerReservation(ReservationMachine reservation){
        StatutReservation statut= statutReservationService.findByCode("annulee");
        reservation.setStatutReservation(statut);
        System.out.println("statut modifié: " +reservation.getStatutReservation().getLibelle());
    }
} 