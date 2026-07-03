package com.voly_saina.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutReservation;
import com.voly_saina.entity.Utilisateur;

@Service
public class PanierReservationService {

    @Autowired
    private FactureService factureService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ReservationMachineService reservationMachineService;

    @Autowired
    private StatutReservationService statutReservationService;

    @Autowired
    private PanierDetailsService panierDetailsService;

    @Autowired
    @Lazy
    private PanierService panierService;



    public ReservationMachine ajouterReservationAuPanier(Long clientId, Long machineId,
            LocalDate dateDebut, LocalDate dateFin, String lieuLivraison) {
        Utilisateur client = utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Machine machine = machineService.findById(machineId);
        if (machine == null || !Boolean.TRUE.equals(machine.getDisponible())) {
            throw new RuntimeException("Machine indisponible");
        }

        List<ReservationMachine> conflits = reservationMachineService.findConfList(machineId, dateDebut, dateFin);
        if (!conflits.isEmpty()) {
            throw new RuntimeException("La machine est déjà réservée sur cette période");
        }

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours == 0) jours = 1;
        BigDecimal prixTotal = machine.getPrixJour().multiply(BigDecimal.valueOf(jours));

        ReservationMachine reservation = new ReservationMachine();
        reservation.setMachine(machine);
        reservation.setClient(client);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setLieuLivraison(lieuLivraison);
        reservation.setPrixTotal(prixTotal);
        reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));
        ReservationMachine saved = reservationMachineService.save(reservation);

        Panier panier = panierService.findCurrentPanierByIdClient(clientId);

        PanierDetails detail = new PanierDetails();
        detail.setPanier(panier);
        detail.setReservationMachine(saved);
        panierDetailsService.save(detail);

        return saved;
    }

    public void supprimerReservationDuPanier(Long reservationId, Long clientId) {
        ReservationMachine reservation = reservationMachineService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
            throw new RuntimeException("Impossible de supprimer une réservation déjà validée");
        }

        PanierDetails panierDetail = panierDetailsService.findByReservationId(reservationId);
        if (panierDetail != null) {
            panierDetailsService.deleteById(panierDetail.getIdPanierDetails());
        }

        reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
        reservationMachineService.save(reservation);
    }

    public void modifierDatesReservation(Long reservationId, Long clientId,
            LocalDate dateDebut, LocalDate dateFin) {
        ReservationMachine reservation = reservationMachineService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Accès non autorisé");
        }
        if (!"en_attente".equals(reservation.getStatutReservation().getCode())) {
            throw new RuntimeException("Impossible de modifier une réservation déjà validée");
        }

        List<ReservationMachine> conflits = reservationMachineService.findConfList(
                reservation.getMachine().getIdMachine(), dateDebut, dateFin);
        conflits.removeIf(c -> c.getIdReservation().equals(reservationId));

        if (!conflits.isEmpty()) {
            throw new RuntimeException("La machine n'est plus disponible sur cette période");
        }

        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);

        long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (jours == 0) jours = 1;
        reservation.setPrixTotal(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(jours)));

        reservationMachineService.save(reservation);
    }

    public int validerReservationsDuPanier(Long clientId, Facture facture) {
        Panier panier = panierService.findByClientId(clientId);
        if (panier == null) {
            throw new RuntimeException("Panier vide");
        }

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        List<ReservationMachine> reservations = new ArrayList<>();
        for (PanierDetails pd : details) {
            if (pd.getReservationMachine() != null) {
                ReservationMachine r = pd.getReservationMachine();
                if ("en_attente".equals(r.getStatutReservation().getCode())) {
                    reservations.add(r);
                }
            }
        }

        if (reservations.isEmpty()) {
            return 0; // No reservations to validate
            // throw new RuntimeException("Aucune réservation à valider");
        }

        for (ReservationMachine r : reservations) {
            List<ReservationMachine> conflits = reservationMachineService.findConfList(
                    r.getMachine().getIdMachine(), r.getDateDebut(), r.getDateFin());
            conflits.removeIf(c -> c.getIdReservation().equals(r.getIdReservation()));
            if (!conflits.isEmpty()) {
                throw new RuntimeException("La machine " + r.getMachine().getNom() + " n'est plus disponible");
            }
        }

        StatutReservation statutValidee = statutReservationService.findByCode("validee");
        int count = 0;
        for (ReservationMachine r : reservations) {
            r.setStatutReservation(statutValidee);
            r.setFacture(facture);
            reservationMachineService.save(r);
            count++;
        }
        factureService.creerOperationReservation(facture, panier);
        panierService.cloturePanier(clientId);
        return count;
    }

    public int viderReservationsDuPanier(Long clientId) {
        Panier panier = panierService.findByClientId(clientId);
        if (panier == null) {
            throw new RuntimeException("Panier vide");
        }

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        int count = 0;

        for (PanierDetails pd : details) {
            if (pd.getReservationMachine() != null) {
                ReservationMachine r = pd.getReservationMachine();
                if ("en_attente".equals(r.getStatutReservation().getCode())) {
                    r.setStatutReservation(statutReservationService.findByCode("annulee"));
                    reservationMachineService.save(r);
                    count++;
                }
            }
            panierDetailsService.deleteById(pd.getIdPanierDetails());
        }

        return count;
    }

    public List<ReservationMachine> getReservationsEnAttente(Long clientId) {
        Panier panier = panierService.findByClientId(clientId);
        if (panier == null) return List.of();

        List<PanierDetails> details = panierDetailsService.findByPanierId(panier.getIdPanier());
        return details.stream()
                .filter(pd -> pd.getReservationMachine() != null)
                .map(PanierDetails::getReservationMachine)
                .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
                .toList();
    }
}
