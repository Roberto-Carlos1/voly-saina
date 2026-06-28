package com.voly_saina.service.client;

import com.voly_saina.dto.*;
import com.voly_saina.entity.*;
import com.voly_saina.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientStatistiqueService {

    private final FactureRepository factureRepository;
    private final ReservationMachineRepository reservationMachineRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;

    public StatistiquesClientDTO genererStatistiquesClient(Long idClient, LocalDate dateDebut, LocalDate dateFin) {
        StatistiquesClientDTO stats = new StatistiquesClientDTO();

        LocalDateTime debut = dateDebut != null ? dateDebut.atStartOfDay() : LocalDate.now().minusMonths(12).atStartOfDay();
        LocalDateTime fin = dateFin != null ? dateFin.atTime(23, 59, 59) : LocalDateTime.now();

        List<String> statutsFactures = Arrays.asList("payee", "partiellement_payee", "en_retard", "en_attente");
        List<Facture> factures = factureRepository.findByClientIdUtilisateurAndDateFactureBetweenAndStatutFactureCodeIn(
            idClient, debut, fin, statutsFactures);
        stats.setNombreFactures(factures.size());

        List<String> statutsReservations = Arrays.asList("validee", "en_cours", "terminee");
        List<ReservationMachine> reservations = reservationMachineRepository.findByClientIdUtilisateurAndDateDebutBetweenAndStatutReservationCodeIn(
            idClient, dateDebut != null ? dateDebut : LocalDate.now().minusMonths(12), 
            dateFin != null ? dateFin : LocalDate.now(), 
            statutsReservations);
        
        BigDecimal totalDepensesLocations = reservations.stream()
            .map(ReservationMachine::getPrixTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalDepensesLocations(totalDepensesLocations);
        stats.setNombreLocations(reservations.size());

        List<String> statutsCommandes = Arrays.asList("validee", "preparee", "en_livraison", "livree");
        List<Commande> commandes = commandeRepository.findByClientIdUtilisateurAndDateCommandeBetweenAndStatutCommandeCodeIn(
            idClient, debut, fin, statutsCommandes);
        
        BigDecimal totalDepensesCommandes = commandes.stream()
            .map(Commande::getMontantTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalDepensesCommandes(totalDepensesCommandes);
        stats.setNombreCommandes(commandes.size());

        BigDecimal totalDepenses = totalDepensesLocations.add(totalDepensesCommandes);
        stats.setTotalDepenses(totalDepenses);

        stats.setTopMachines(calculerTopMachines(reservations));
        
        List<LigneCommande> lignesCommandes = ligneCommandeRepository.findLignesCommandeClient(
            idClient, debut, fin, statutsCommandes);
        stats.setTopProduits(calculerTopProduits(lignesCommandes));
        
        stats.setDepensesMensuelles(calculerDepensesMensuelles(factures));

        return stats;
    }

    private List<TopMachineDTO> calculerTopMachines(List<ReservationMachine> reservations) {
        Map<Long, List<ReservationMachine>> groupedByMachine = reservations.stream()
            .collect(Collectors.groupingBy(r -> r.getMachine().getIdMachine()));

        return groupedByMachine.entrySet().stream()
            .map(entry -> {
                Machine machine = entry.getValue().get(0).getMachine();
                int nombreLocations = entry.getValue().size();
                BigDecimal totalDepense = entry.getValue().stream()
                    .map(ReservationMachine::getPrixTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                return new TopMachineDTO(
                    machine.getIdMachine(),
                    machine.getNom(),
                    machine.getTypeMachine() != null ? machine.getTypeMachine().getLibelle() : "Non défini",
                    nombreLocations,
                    totalDepense
                );
            })
            .sorted(Comparator
                .comparing(TopMachineDTO::getNombreLocations).reversed()
                .thenComparing(TopMachineDTO::getTotalDepense).reversed()
                .thenComparing(TopMachineDTO::getNomMachine))
            .limit(5)
            .collect(Collectors.toList());
    }

    private List<TopProduitDTO> calculerTopProduits(List<LigneCommande> lignesCommandes) {
        Map<Long, List<LigneCommande>> groupedByProduit = lignesCommandes.stream()
            .collect(Collectors.groupingBy(lc -> lc.getProduit().getIdProduit()));

        return groupedByProduit.entrySet().stream()
            .map(entry -> {
                Produit produit = entry.getValue().get(0).getProduit();
                BigDecimal quantiteTotale = entry.getValue().stream()
                    .map(LigneCommande::getQuantite)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalDepense = entry.getValue().stream()
                    .map(LigneCommande::getSousTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                String categorie = produit.getCategorie() != null ? produit.getCategorie().getNom() : "Sans catégorie";
                
                return new TopProduitDTO(
                    produit.getIdProduit(),
                    produit.getNom(),
                    categorie,
                    quantiteTotale,
                    totalDepense
                );
            })
            .sorted(Comparator
                .comparing(TopProduitDTO::getQuantiteTotale).reversed()
                .thenComparing(TopProduitDTO::getTotalDepense).reversed()
                .thenComparing(TopProduitDTO::getNomProduit))
            .limit(5)
            .collect(Collectors.toList());
    }

    private List<DepenseMensuelleDTO> calculerDepensesMensuelles(List<Facture> factures) {
        Map<YearMonth, BigDecimal> groupedByMonth = factures.stream()
            .collect(Collectors.groupingBy(
                f -> YearMonth.from(f.getDateFacture()),
                Collectors.mapping(Facture::getMontantTotal, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        return groupedByMonth.entrySet().stream()
            .map(entry -> {
                int annee = entry.getKey().getYear();
                int mois = entry.getKey().getMonthValue();
                String libelleMois = entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + annee;
                
                return new DepenseMensuelleDTO(annee, mois, libelleMois, entry.getValue());
            })
            .sorted(Comparator.comparing(DepenseMensuelleDTO::getAnnee)
                .thenComparing(DepenseMensuelleDTO::getMois))
            .collect(Collectors.toList());
    }

    public LocalDate[] periodeDefaut() {
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusMonths(12);
        return new LocalDate[]{debut, fin};
    }

    public Map<String, LocalDate[]> periodesDisponibles() {
        LocalDate now = LocalDate.now();
        Map<String, LocalDate[]> periodes = new HashMap<>();
        
        periodes.put("3mois", new LocalDate[]{now.minusMonths(3), now});
        periodes.put("6mois", new LocalDate[]{now.minusMonths(6), now});
        periodes.put("12mois", new LocalDate[]{now.minusMonths(12), now});
        periodes.put("annee_en_cours", new LocalDate[]{now.withDayOfYear(1), now});
        
        return periodes;
    }

    // Wrapper methods for traceability with design document
    public StatistiquesClientDTO genererIndicateurs(String module, Long idClient, LocalDate dateDebut, LocalDate dateFin) {
        if (!"client".equals(module)) {
            throw new IllegalArgumentException("Module non supporté ici : " + module);
        }
        return genererStatistiquesClient(idClient, dateDebut, dateFin);
    }

    public List<?> listerTop(String typeRessource, Long idClient, LocalDate dateDebut, LocalDate dateFin) {
        return switch (typeRessource) {
            case "machine" -> listerTopMachines(idClient, dateDebut, dateFin);
            case "produit" -> listerTopProduits(idClient, dateDebut, dateFin);
            default -> throw new IllegalArgumentException("Type de ressource inconnu : " + typeRessource);
        };
    }

    // Extracted methods for both internal use and generic wrapper
    List<TopMachineDTO> listerTopMachines(Long idClient, LocalDate dateDebut, LocalDate dateFin) {
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now().minusMonths(12);
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now();
        
        List<String> statutsReservations = Arrays.asList("validee", "en_cours", "terminee");
        List<ReservationMachine> reservations = reservationMachineRepository.findByClientIdUtilisateurAndDateDebutBetweenAndStatutReservationCodeIn(
            idClient, debut, fin, statutsReservations);
        
        return calculerTopMachines(reservations);
    }

    List<TopProduitDTO> listerTopProduits(Long idClient, LocalDate dateDebut, LocalDate dateFin) {
        LocalDateTime debut = dateDebut != null ? dateDebut.atStartOfDay() : LocalDate.now().minusMonths(12).atStartOfDay();
        LocalDateTime fin = dateFin != null ? dateFin.atTime(23, 59, 59) : LocalDateTime.now();
        
        List<String> statutsCommandes = Arrays.asList("validee", "preparee", "en_livraison", "livree");
        List<LigneCommande> lignesCommandes = ligneCommandeRepository.findLignesCommandeClient(
            idClient, debut, fin, statutsCommandes);
        
        return calculerTopProduits(lignesCommandes);
    }
}
