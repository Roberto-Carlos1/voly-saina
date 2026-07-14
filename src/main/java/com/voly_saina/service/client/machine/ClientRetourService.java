package com.voly_saina.service.client.machine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.dto.dtoMacine.FormulaireRetourDTO;
import com.voly_saina.dto.dtoMacine.RetourClientDTO;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.RetourMachineService;

@Service
public class ClientRetourService {

    @Autowired
    private ReservationMachineService reservationService;

    @Autowired
    private RetourMachineService retourService;

    public FormulaireRetourDTO prepareFormulaireRetour(Long reservationId, Long clientId) {
        ReservationMachine reservation = reservationService.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.getClient().getIdUtilisateur().equals(clientId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé");
        }

        LocalDate dateRetour = LocalDate.now();
        long joursRetard = ChronoUnit.DAYS.between(reservation.getDateFin(), dateRetour);
        BigDecimal penaliteRetard = joursRetard > 0 ?
            reservation.getMachine().getPrixJour()
                .multiply(BigDecimal.valueOf(joursRetard))
                .multiply(BigDecimal.valueOf(1.5)) :
            BigDecimal.ZERO;

        FormulaireRetourDTO dto = new FormulaireRetourDTO();
        dto.setReservationId(reservation.getIdReservation());
        dto.setClientId(clientId);
        dto.setMachineId(reservation.getMachine().getIdMachine());
        dto.setMachineNom(reservation.getMachine().getNom());
        dto.setMachineDescription(reservation.getMachine().getDescription());
        dto.setMachineType(reservation.getMachine().getTypeMachine() != null ?
            reservation.getMachine().getTypeMachine().getLibelle() : "Non défini");
        dto.setPrixJour(reservation.getMachine().getPrixJour());
        dto.setPrixTotal(reservation.getPrixTotal());
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setDateRetour(dateRetour);
        dto.setPenaliteRetard(penaliteRetard);
        dto.setEstRetard(joursRetard > 0);
        dto.setJoursRetard(Math.max(joursRetard, 0));

        // États possibles
        List<FormulaireRetourDTO.EtatOption> etats = new ArrayList<>();
        
        FormulaireRetourDTO.EtatOption bon = new FormulaireRetourDTO.EtatOption();
        bon.setCode("bon");
        bon.setLibelle("Bon état");
        bon.setIcon("check-circle");
        bon.setColor("success");
        bon.setDescription("Aucune pénalité");
        bon.setPenalite(BigDecimal.ZERO);
        etats.add(bon);
        
        FormulaireRetourDTO.EtatOption use = new FormulaireRetourDTO.EtatOption();
        use.setCode("use");
        use.setLibelle("Usure normale");
        use.setIcon("circle");
        use.setColor("warning");
        use.setDescription("2 jours de location");
        use.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(2)));
        etats.add(use);
        
        FormulaireRetourDTO.EtatOption endommage = new FormulaireRetourDTO.EtatOption();
        endommage.setCode("endommage");
        endommage.setLibelle("Endommagé");
        endommage.setIcon("exclamation-triangle");
        endommage.setColor("warning");
        endommage.setDescription("5 jours de location");
        endommage.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(5)));
        etats.add(endommage);
        
        FormulaireRetourDTO.EtatOption casse = new FormulaireRetourDTO.EtatOption();
        casse.setCode("casse");
        casse.setLibelle("Cassé");
        casse.setIcon("times-circle");
        casse.setColor("danger");
        casse.setDescription("15 jours de location");
        casse.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(15)));
        etats.add(casse);
        
        FormulaireRetourDTO.EtatOption perdu = new FormulaireRetourDTO.EtatOption();
        perdu.setCode("perdu");
        perdu.setLibelle("Perdu");
        perdu.setIcon("search-minus");
        perdu.setColor("danger");
        perdu.setDescription("30 jours de location");
        perdu.setPenalite(reservation.getMachine().getPrixJour().multiply(BigDecimal.valueOf(30)));
        etats.add(perdu);
        
        dto.setEtatsPossibles(etats.toArray(new FormulaireRetourDTO.EtatOption[0]));

        return dto;
    }

    public RetourClientDTO getRetourClient(Long reservationId) {
        RetourMachine retour = retourService.findByReservation(reservationId);
        if (retour == null) return null;

        RetourClientDTO dto = new RetourClientDTO();
        dto.setIdRetour(retour.getIdRetour());
        dto.setIdReservation(retour.getReservation().getIdReservation());
        dto.setIdMachine(retour.getReservation().getMachine().getIdMachine());
        dto.setMachineNom(retour.getReservation().getMachine().getNom());
        dto.setDateRetour(retour.getDateRetour());
        dto.setEtatRetour(retour.getEtatRetour());
        dto.setEtatRetourLibelle(getLibelleEtat(retour.getEtatRetour()));
        dto.setRemarque(retour.getRemarque());
        dto.setPenalite(retour.getPenalite());

        long joursRetard = ChronoUnit.DAYS.between(
            retour.getReservation().getDateFin(),
            retour.getDateRetour()
        );
        dto.setEstRetard(joursRetard > 0);
        dto.setJoursRetard(Math.max(joursRetard, 0));

        return dto;
    }

    private String getLibelleEtat(String code) {
        switch (code) {
            case "bon": return "Bon état";
            case "use": return "Usure normale";
            case "endommage": return "Endommagé";
            case "casse": return "Cassé";
            case "perdu": return "Perdu";
            default: return code;
        }
    }
}