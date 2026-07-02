package com.voly_saina.service.client;

import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.exception.ResourceNotFoundException;
import com.voly_saina.repository.FactureRepository;
import com.voly_saina.service.CommandeService;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.LigneCommandeService;
import com.voly_saina.service.PanierDetailsService;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.ReservationMachineService;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ClientFactureService {

    @Autowired
    private FactureRepository factureRepository;

    private final FactureService factureService;
    private final PanierService panierService;
    private final PanierDetailsService panierDetailsService;
    private final LigneCommandeService ligneCommandeService;
    private final CommandeService commandeService;
    private final ReservationMachineService reservationMachineService;

    public ClientFactureService(FactureService factureService, PanierService panierService,
            PanierDetailsService panierDetailsService, LigneCommandeService ligneCommandeService,
            CommandeService commandeService, ReservationMachineService reservationMachineService) {
        this.factureService = factureService;
        this.panierService = panierService;
        this.panierDetailsService = panierDetailsService;
        this.ligneCommandeService = ligneCommandeService;
        this.commandeService = commandeService;
        this.reservationMachineService = reservationMachineService;
    }

    public List<FactureClientDTO> listerFacturesClient(Long idClient, String filtreStatut) {
        List<Facture> factures;

        if (filtreStatut == null || filtreStatut.isEmpty()) {
            factures = factureRepository.findByClientIdUtilisateurOrderByDateFactureDesc(idClient);
        } else {
            factures = factureRepository.findByClientIdUtilisateurAndStatutFactureCodeOrderByDateFactureDesc(idClient,
                    filtreStatut);
        }

        return factures.stream().map(this::mapToDTO).toList();
    }

    public FactureClientDTO voirDetailFacture(Long idFacture, Long idClient) {
        Optional<Facture> factureOpt = factureRepository.findByIdFactureAndClientIdUtilisateur(idFacture, idClient);

        if (factureOpt.isEmpty()) {
            throw new ResourceNotFoundException("Facture non trouvée avec l'ID : " + idFacture);
        }

        return mapToDTO(factureOpt.get());
    }

    public byte[] exporterFacturePDF(Long idFacture, Long idClient) {
        // FactureClientDTO facture = voirDetailFacture(idFacture, idClient);
        Facture facture = factureService.findById(idFacture);
        Panier panier = facture.getPanier();

        List<PanierDetails> listeCommande = panierDetailsService.findCommandesByPanier(panier.getIdPanier());
        List<PanierDetails> listeReservation = panierDetailsService.findReservationByPanier(panier.getIdPanier());

        BigDecimal montantCommande = panierDetailsService.montantCommande(panier.getIdPanier()),
                montantReservation = panierDetailsService.montantReservation(panier.getIdPanier()),
                montantTotal = montantCommande.add(montantReservation);

        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);

            document.open();

            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.COURIER, 18,
                    com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font normalFont = new com.lowagie.text.Font(com.lowagie.text.Font.COURIER, 12);

            document.add(new com.lowagie.text.Paragraph("FACTURE", titleFont));
            document.add(com.lowagie.text.Chunk.NEWLINE);

            document.add(new com.lowagie.text.Paragraph("Numero : " + facture.getNumero(), normalFont));
            document.add(new com.lowagie.text.Paragraph("Type : " + facture.getTypeOperation(), normalFont));
            document.add(
                    new com.lowagie.text.Paragraph("Date : " + facture.getDateFacture().toLocalDate(), normalFont));
            document.add(new com.lowagie.text.Paragraph("Date limite : " + facture.getDateLimite(), normalFont));
            document.add(com.lowagie.text.Chunk.NEWLINE);

            document.add(new com.lowagie.text.Paragraph("COMMANDES", normalFont));
            if (listeCommande.size() == 0) {
                document.add(new com.lowagie.text.Paragraph("Aucune commande de produit", normalFont));
            } else {
                PdfPTable tableCommande = new PdfPTable(4);
                String[] titles = { "Designation", "Quantite", "Prix Unitaire", "Prix total" };
                addTableHeader(tableCommande, titles);
                addRowsCommande(tableCommande, listeCommande);
                document.add(tableCommande);
            }
            document.add(com.lowagie.text.Chunk.NEWLINE);
            document.add(new com.lowagie.text.Paragraph(
                    "Total montant pour commande : " + String.valueOf(montantCommande), normalFont));
            document.add(com.lowagie.text.Chunk.NEWLINE);

            document.add(new com.lowagie.text.Paragraph("RESERVATION MACHINES", normalFont));
            if (listeReservation.size() == 0) {
                document.add(new com.lowagie.text.Paragraph("Aucune reservation de machines", normalFont));
            } else {
                PdfPTable tableReservation = new PdfPTable(4);
                String[] titles = { "Designation", "Date debut", "Date fin", "Prix total" };
                addTableHeader(tableReservation, titles);
                addRowsReservation(tableReservation, listeReservation);
                document.add(tableReservation);
            }

            document.add(com.lowagie.text.Chunk.NEWLINE);
            document.add(new com.lowagie.text.Paragraph(
                    "Total montant pour reservation : " + String.valueOf(montantReservation), normalFont));
            document.add(com.lowagie.text.Chunk.NEWLINE);

            document.add(
                    new com.lowagie.text.Paragraph("Statut : " + facture.getStatutFacture().getLibelle(), normalFont));

            document.add(new com.lowagie.text.Paragraph("Montant total : " + montantTotal + " MGA",
                    normalFont));
            document.add(
                    new com.lowagie.text.Paragraph("Montant paye : " + facture.getMontantPaye() + " MGA", normalFont));

            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la generation du PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table, String[] titles) {

        Stream.of(titles)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    // header.setBackgroundColor(Color.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRowsCommande(PdfPTable table, List<PanierDetails> paniers) {
        for (PanierDetails detail : paniers) {
            Commande commande = detail.getCommande();
            List<LigneCommande> lignes = ligneCommandeService.findByIdCommande(commande.getIdCommande());
            for (LigneCommande line : lignes) {
                table.addCell(line.getProduit().getNom());
                table.addCell(String.valueOf(line.getQuantite()));
                table.addCell(String.valueOf(line.getPrixUnitaire()));
                table.addCell(String.valueOf(line.getSousTotal()));
            }
        }
    }

    private void addRowsReservation(PdfPTable table, List<PanierDetails> paniers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (PanierDetails detail : paniers) {
            ReservationMachine reservation = detail.getReservationMachine();
            table.addCell(reservation.getMachine().getNom());
            table.addCell(String.valueOf(reservation.getDateCreation().format(formatter)));
            table.addCell(String.valueOf(reservation.getDateFin().format(formatter)));
            table.addCell(String.valueOf(reservation.getPrixTotal()));
        }
    }

    // Wrapper methods for traceability with design document
    public List<FactureClientDTO> listerOperations(String typeOperation, Long idClient, String filtreStatut) {
        if (!"facture".equals(typeOperation)) {
            throw new IllegalArgumentException("Type d'opération non supporté ici : " + typeOperation);
        }
        return listerFacturesClient(idClient, filtreStatut);
    }

    public FactureClientDTO consulterDetail(String typeObjet, Long idFacture, Long idClient) {
        if (!"facture".equals(typeObjet)) {
            throw new IllegalArgumentException("Type d'objet non supporté ici : " + typeObjet);
        }
        return voirDetailFacture(idFacture, idClient);
    }

    public byte[] exporterDocument(String typeDocument, Long idFacture, Long idClient, String format) {
        if (!"facture".equals(typeDocument) || !"PDF".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Document/format non supporté ici : " + typeDocument + " / " + format);
        }
        return exporterFacturePDF(idFacture, idClient);
    }

    private FactureClientDTO mapToDTO(Facture facture) {
        FactureClientDTO dto = new FactureClientDTO();
        dto.setIdFacture(facture.getIdFacture());
        dto.setNumero(facture.getNumero());
        dto.setTypeOperation(facture.getTypeOperation());
        dto.setDateFacture(facture.getDateFacture());

        // Handle null montantTotal and montantPaye
        java.math.BigDecimal montantTotal = facture.getMontantTotal() != null ? facture.getMontantTotal()
                : java.math.BigDecimal.ZERO;
        java.math.BigDecimal montantPaye = facture.getMontantPaye() != null ? facture.getMontantPaye()
                : java.math.BigDecimal.ZERO;

        dto.setMontantTotal(montantTotal);
        dto.setMontantPaye(montantPaye);

        if (montantTotal.compareTo(java.math.BigDecimal.ZERO) > 0) {
            dto.setMontantRestant(montantTotal.subtract(montantPaye));

            // Calculate percentage paid
            int percentage = montantPaye.multiply(java.math.BigDecimal.valueOf(100))
                    .divide(montantTotal, 0, java.math.RoundingMode.HALF_UP)
                    .intValue();
            dto.setPourcentagePaye(percentage);
        } else {
            dto.setMontantRestant(java.math.BigDecimal.ZERO);
            dto.setPourcentagePaye(0);
        }

        // Calculate status dynamically based on amounts and date
        String statutCode;
        String statutLibelle;

        boolean dateDepassee = facture.getDateLimite() != null && facture.getDateLimite().isBefore(LocalDate.now());

        if (montantPaye.compareTo(montantTotal) >= 0) {
            statutCode = "payee";
            statutLibelle = "Payée";
        } else if (dateDepassee) {
            statutCode = "en_retard";
            statutLibelle = "En retard";
        } else if (montantPaye.compareTo(java.math.BigDecimal.ZERO) == 0) {
            statutCode = "en_attente";
            statutLibelle = "En attente";
        } else {
            statutCode = "partiellement_payee";
            statutLibelle = "Partiellement payée";
        }

        dto.setStatutCode(statutCode);
        dto.setStatutLibelle(statutLibelle);

        dto.setDateLimite(facture.getDateLimite());

        boolean enRetard = facture.getDateLimite() != null
                && facture.getDateLimite().isBefore(LocalDate.now())
                && montantPaye.compareTo(montantTotal) < 0;
        dto.setEnRetard(enRetard);

        return dto;
    }
}
