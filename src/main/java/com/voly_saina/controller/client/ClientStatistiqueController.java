package com.voly_saina.controller.client;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/client/statistiques")
public class ClientStatistiqueController {

    @Autowired
    private ReservationMachineService reservationService;
    
    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping
    public String statistiques(@RequestParam(required = false) Long clientId, Model model) {
        Long idClient = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClient)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Long clientIdFinal = client.getIdUtilisateur();
        
        List<ReservationMachine> reservations = reservationService.findByClientId(clientIdFinal);
        
        long total = reservations.size();
        long enAttente = reservations.stream()
            .filter(r -> "en_attente".equals(r.getStatutReservation().getCode()))
            .count();
        long enCours = reservations.stream()
            .filter(r -> "en_cours".equals(r.getStatutReservation().getCode()))
            .count();
        long terminees = reservations.stream()
            .filter(r -> "terminee".equals(r.getStatutReservation().getCode()))
            .count();
        long annulees = reservations.stream()
            .filter(r -> "annulee".equals(r.getStatutReservation().getCode()))
            .count();
        
        BigDecimal penalites = reservations.stream()
            .map(ReservationMachine::getRetour)
            .filter(r -> r != null && r.getPenalite() != null)
            .map(RetourMachine::getPenalite)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal depense = reservations.stream()
            .filter(r -> "terminee".equals(r.getStatutReservation().getCode()))
            .map(ReservationMachine::getPrixTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long retoursAvecPenalite = reservations.stream()
            .map(ReservationMachine::getRetour)
            .filter(r -> r != null && r.getPenalite() != null && r.getPenalite().compareTo(BigDecimal.ZERO) > 0)
            .count();
        
        model.addAttribute("client", client);
        model.addAttribute("total", total);
        model.addAttribute("enAttente", enAttente);
        model.addAttribute("enCours", enCours);
        model.addAttribute("terminees", terminees);
        model.addAttribute("annulees", annulees);
        model.addAttribute("penalites", penalites);
        model.addAttribute("depense", depense);
        model.addAttribute("retoursAvecPenalite", retoursAvecPenalite);
        model.addAttribute("clientId", clientId);
        
        return "client/statistiques/index";
    }
}