package com.voly_saina.service.client.machine;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.repository.ReservationMachineRepository;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.StatutFactureService;
import com.voly_saina.service.StatutReservationService;

@Service
public class ClientReservationService {

    @Autowired
    private ReservationMachineRepository reservationRepository;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private StatutFactureService statutFactureService;

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

    /**
     * Annuler une réservation
     * Vérifie que la réservation peut être annulée avant de procéder
     */
    @Transactional
    public void annulerReservation(ReservationMachine reservation) {
        // Vérifier que la réservation peut être annulée
        String statut = reservation.getStatutReservation() != null ? 
            reservation.getStatutReservation().getCode() : "";

        // Une réservation ne peut pas être annulée si elle est déjà terminée ou annulée
        if ("terminee".equals(statut) || "annulee".equals(statut)) {
            throw new RuntimeException("Cette réservation ne peut plus être annulée.");
        }

        // Mettre à jour le statut de la réservation
        StatutReservation statutAnnule = statutReservationService.findByCode("annulee");
        if (statutAnnule == null) {
            throw new RuntimeException("Statut de réservation 'annulee' non trouvé");
        }
        reservation.setStatutReservation(statutAnnule);
        reservation.setMotifRefus("Annulé par le client");

        // Si une facture existe, l'annuler aussi
        if (reservation.getFacture() != null) {
            Facture facture = reservation.getFacture();
            
            // Récupérer le statut "annulée" pour la facture
            // Utiliser le code "annulee" - SI CE STATUT EXISTE DANS VOTRE BASE
            // Sinon utiliser "en_attente" ou un autre statut approprié
            var statutFactureAnnule = statutFactureService.findByCode("annulee");
            
            if (statutFactureAnnule.isPresent()) {
                facture.setStatutFacture(statutFactureAnnule.get());
            } else {
                // Si le statut "annulee" n'existe pas, on utilise "en_attente" ou on ne change pas
                // OU on peut définir le montant à 0
                facture.setMontantTotal(BigDecimal.ZERO);
                facture.setMontantPaye(BigDecimal.ZERO);
                // Ne pas changer le statut si "annulee" n'existe pas
            }
            
            factureService.save(facture);
        }
    }

    /**
     * Annuler une facture associée à une réservation
     * Utilise les statuts existants
     */
    @Transactional
    public void annulerFacture(ReservationMachine reservation) {
        if (reservation.getFacture() == null) {
            return;
        }

        Facture f = factureService.findById(reservation.getFacture().getIdFacture());
        if (f == null) {
            return;
        }

        // Vérifier si le statut "annulee" existe dans la base
        var statutAnnule = statutFactureService.findByCode("annulee");
        
        if (statutAnnule.isPresent()) {
            // Si le statut existe, on l'utilise
            f.setStatutFacture(statutAnnule.get());
        } else {
            // Sinon on met le montant à 0
            f.setMontantTotal(BigDecimal.ZERO);
            f.setMontantPaye(BigDecimal.ZERO);
        }
        
        factureService.save(f);
        reservation.setFacture(null);
    }

    private ReservationClientDTO mapToDTO(ReservationMachine reservation) {
        ReservationClientDTO dto = new ReservationClientDTO();
        dto.setIdReservation(reservation.getIdReservation());
        dto.setIdMachine(reservation.getMachine().getIdMachine());
        dto.setMachineNom(reservation.getMachine().getNom());
        dto.setMachineType(reservation.getMachine().getTypeMachine() != null
                ? reservation.getMachine().getTypeMachine().getLibelle()
                : "Non défini");
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
        String statut = reservation.getStatutReservation() != null ? reservation.getStatutReservation().getCode() : "";

        // On peut annuler si en_attente ou validee
        dto.setPeutAnnuler("en_attente".equals(statut) || "validee".equals(statut));
        
        // On peut retourner si en_cours ou validee
        dto.setPeutRetourner("en_cours".equals(statut) || "validee".equals(statut));
        
        dto.setEstTerminee("terminee".equals(statut) || "annulee".equals(statut));

        // Facture
        if (reservation.getFacture() != null) {
            dto.setFactureId(reservation.getFacture().getIdFacture());
            dto.setFactureNumero(reservation.getFacture().getNumero());
        }

        return dto;
    }
}