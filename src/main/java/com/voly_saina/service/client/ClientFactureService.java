package com.voly_saina.service.client;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.lowagie.text.Chunk;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.exception.ResourceNotFoundException;
import com.voly_saina.repository.FactureRepository;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.LigneCommandeService;
import com.voly_saina.service.PanierDetailsService;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.ReservationMachineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class ClientFactureService {

    @Autowired
    private FactureRepository factureRepository;

    private final FactureService factureService;
    private final PanierDetailsService panierDetailsService;
    private final LigneCommandeService ligneCommandeService;
    private final ReservationMachineService reservationMachineService;

    public ClientFactureService(FactureService factureService, PanierService panierService,
            PanierDetailsService panierDetailsService, LigneCommandeService ligneCommandeService,
            ReservationMachineService reservationMachineService) {
        this.factureService = factureService;
        this.panierDetailsService = panierDetailsService;
        this.ligneCommandeService = ligneCommandeService;
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

    public void detailsFacture(Long idFacture, Long idClient, Model m) {
        Facture facture = factureService.findById(idFacture);
        Panier panier = facture.getPanier();

        List<PanierDetails> listeCommande = panierDetailsService.findCommandesByPanier(panier.getIdPanier());
        List<PanierDetails> listeReservation = panierDetailsService.findReservationByPanier(panier.getIdPanier());

        // BigDecimal montantCommande =
        // panierDetailsService.montantCommande(panier.getIdPanier()) != null ?
        // panierDetailsService.montantCommande(panier.getIdPanier()) : BigDecimal.ZERO;
        // BigDecimal montantReservation =
        // panierDetailsService.montantReservation(panier.getIdPanier()) != null ?
        // panierDetailsService.montantReservation(panier.getIdPanier()) :
        // BigDecimal.ZERO;
        // BigDecimal montantTotal = montantCommande.add(montantReservation);

        List<PanierDetails> panierDetails = panierDetailsService.findByIdPanier(panier.getIdPanier());
        List<LigneCommande> ligneCommande = ligneCommandeService.findAll();
        List<ReservationMachine> reservationMachines = reservationMachineService.findAll();

        List<LigneCommande> lignes = new ArrayList<>();
        List<ReservationMachine> ligneReservation = new ArrayList<>();

        for (LigneCommande ligne : ligneCommande) {
            for (PanierDetails panierDet : panierDetails) {
                if (panierDet.getCommande() != null
                        && (panierDet.getCommande().getIdCommande() == ligne.getCommande().getIdCommande())) {
                    lignes.add(ligne);
                }
            }
        }

        for (ReservationMachine reserve : reservationMachines) {
            if (reserve.getFacture().getIdFacture() == facture.getIdFacture()) {
                ligneReservation.add(reserve);
            }
        }

        m.addAttribute("reservations", ligneReservation);
        m.addAttribute("commandes", listeCommande);
        m.addAttribute("lignes", lignes);

    }

    public byte[] exporterFacturePDF(Long idFacture, Long idClient) {
        Facture facture = factureService.findById(idFacture);
        Panier panier = facture.getPanier();

        List<PanierDetails> listeCommande = panierDetailsService.findCommandesByPanier(panier.getIdPanier());
        List<PanierDetails> listeReservation = panierDetailsService.findReservationByPanier(panier.getIdPanier());

        BigDecimal montantCommande = panierDetailsService.montantCommande(panier.getIdPanier()) != null
                ? panierDetailsService.montantCommande(panier.getIdPanier())
                : BigDecimal.ZERO;
        BigDecimal montantReservation = panierDetailsService.montantReservation(panier.getIdPanier()) != null
                ? panierDetailsService.montantReservation(panier.getIdPanier())
                : BigDecimal.ZERO;
        BigDecimal montantTotal = montantCommande.add(montantReservation);

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Polices améliorées
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(0, 102, 51));
            Font subtitleFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(0, 51, 25));
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(0, 102, 51));

            // ============ EN-TÊTE DE LA FACTURE ============
            // Titre principal avec cadre
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[] { 60f, 40f });

            // Partie gauche - Nom de l'entreprise
            PdfPCell leftHeader = new PdfPCell();
            leftHeader.setBorder(Rectangle.NO_BORDER);
            leftHeader.setPaddingBottom(10);

            Chunk companyName = new Chunk("VOLY SAINA+",
                    new Font(Font.HELVETICA, 28, Font.BOLD, new Color(0, 102, 51)));
            Paragraph companyPara = new Paragraph(companyName);
            companyPara.setSpacingAfter(5);
            leftHeader.addElement(companyPara);

            leftHeader.addElement(new Paragraph("Adresse : Antananarivo, Madagascar", normalFont));
            leftHeader.addElement(new Paragraph("Tél : +261 34 00 000 00", normalFont));
            leftHeader.addElement(new Paragraph("Email : contact@volysaina.mg", normalFont));
            headerTable.addCell(leftHeader);

            // Partie droite - Numéro de facture
            PdfPCell rightHeader = new PdfPCell();
            rightHeader.setBorder(Rectangle.NO_BORDER);
            rightHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rightHeader.setPaddingBottom(10);

            Paragraph factureTitle = new Paragraph("FACTURE",
                    new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 102, 51)));
            factureTitle.setAlignment(Element.ALIGN_RIGHT);
            rightHeader.addElement(factureTitle);
            rightHeader.addElement(new Paragraph("N° " + facture.getNumero(), boldFont));
            rightHeader.addElement(new Paragraph(
                    "Date : "
                            + facture.getDateFacture().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    normalFont));
            rightHeader.addElement(new Paragraph(
                    "Date limite : " + facture.getDateLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    normalFont));
            headerTable.addCell(rightHeader);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // Ligne séparatrice
            Paragraph separator = new Paragraph("____________________________________________________________");
            separator.setAlignment(Element.ALIGN_CENTER);
            document.add(separator);
            document.add(Chunk.NEWLINE);

            // ============ INFORMATIONS CLIENT ============
            document.add(new Paragraph("INFORMATIONS CLIENT", subtitleFont));
            document.add(new Paragraph("Nom : " + facture.getClient().getNom(), normalFont));
            document.add(new Paragraph("Email : " + facture.getClient().getEmail(), normalFont));
            document.add(new Paragraph("Téléphone : " + facture.getClient().getTelephone(), normalFont));
            document.add(Chunk.NEWLINE);

            // ============ SECTION COMMANDES ============

            if (listeCommande.isEmpty()) {
                document.add(new Paragraph("Aucune commande de produit", normalFont));
            } else {
                PdfPTable sectionHeaderCommande = new PdfPTable(1);
                sectionHeaderCommande.setWidthPercentage(100);
                PdfPCell sectionCellCommande = new PdfPCell();
                sectionCellCommande.setBackgroundColor(new Color(0, 102, 51));
                sectionCellCommande.setPadding(8);
                sectionCellCommande.setHorizontalAlignment(Element.ALIGN_CENTER);
                sectionCellCommande.setPhrase(new Phrase("COMMANDES DE PRODUITS", headerFont));
                sectionHeaderCommande.addCell(sectionCellCommande);
                document.add(sectionHeaderCommande);
                document.add(Chunk.NEWLINE);

                PdfPTable tableCommande = new PdfPTable(5);
                tableCommande.setWidthPercentage(100);
                tableCommande.setWidths(new float[] { 10f, 15f, 15f, 15f, 15f });

                String[] titles = { "Num", "Désignation", "Quantité", "Prix Unitaire", "Prix total" };
                addTableHeader(tableCommande, titles, new Color(0, 102, 51));
                addRowsCommande(tableCommande, listeCommande);
                document.add(tableCommande);

                // Total commandes
                document.add(new Paragraph("Sous-total commandes : " + montantCommande + " MGA", boldFont));
            }
            document.add(Chunk.NEWLINE);

            // ============ SECTION RÉSERVATIONS ============

            if (listeReservation.isEmpty()) {
                document.add(new Paragraph("Aucune réservation de machines", normalFont));
            } else {
                PdfPTable sectionHeaderReservation = new PdfPTable(1);
                sectionHeaderReservation.setWidthPercentage(100);
                PdfPCell sectionCellReservation = new PdfPCell();
                sectionCellReservation.setBackgroundColor(new Color(0, 102, 51));
                sectionCellReservation.setPadding(8);
                sectionCellReservation.setHorizontalAlignment(Element.ALIGN_CENTER);
                sectionCellReservation.setPhrase(new Phrase("RÉSERVATIONS DE MACHINES", headerFont));
                sectionHeaderReservation.addCell(sectionCellReservation);
                document.add(sectionHeaderReservation);
                document.add(Chunk.NEWLINE);

                PdfPTable tableReservation = new PdfPTable(6);
                tableReservation.setWidthPercentage(100);
                tableReservation.setWidths(new float[] { 10f, 15f, 15f, 15f, 10f, 15f });

                String[] titles = { "Num", "Désignation", "Date début", "Date fin", "Duree (jours)", "Prix total" };
                addTableHeader(tableReservation, titles, new Color(0, 102, 51));
                addRowsReservation(tableReservation, listeReservation);
                document.add(tableReservation);

                document.add(new Paragraph("Sous-total réservations : " + montantReservation + " MGA", boldFont));
            }
            document.add(Chunk.NEWLINE);

            // ============ LIGNE SÉPARATRICE ============
            document.add(new Paragraph("____________________________________________________________", normalFont));
            document.add(Chunk.NEWLINE);

            // ============ RÉCAPITULATIF FINANCIER ============
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(60);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setWidths(new float[] { 50f, 50f });

            // Ligne Montant total
            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL À PAYER :", totalFont));
            totalLabel.setBorder(Rectangle.NO_BORDER);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setPadding(5);
            totalTable.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase(montantTotal + " MGA", totalFont));
            totalValue.setBorder(Rectangle.NO_BORDER);
            totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValue.setPadding(5);
            totalTable.addCell(totalValue);

            // Ligne Montant payé
            PdfPCell paidLabel = new PdfPCell(new Phrase("Montant payé :", normalFont));
            paidLabel.setBorder(Rectangle.NO_BORDER);
            paidLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            paidLabel.setPadding(5);
            totalTable.addCell(paidLabel);

            PdfPCell paidValue = new PdfPCell(new Phrase(facture.getMontantPaye() + " MGA", normalFont));
            paidValue.setBorder(Rectangle.NO_BORDER);
            paidValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            paidValue.setPadding(5);
            totalTable.addCell(paidValue);

            // Ligne Statut
            PdfPCell statusLabel = new PdfPCell(new Phrase("Statut :", normalFont));
            statusLabel.setBorder(Rectangle.NO_BORDER);
            statusLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            statusLabel.setPadding(5);
            totalTable.addCell(statusLabel);

            Font statusFont = new Font(Font.HELVETICA, 11, Font.BOLD,
                    facture.getStatutFacture().getLibelle().equals("Payée") ? new Color(0, 153, 0)
                            : new Color(204, 0, 0));
            PdfPCell statusValue = new PdfPCell(new Phrase(facture.getStatutFacture().getLibelle(), statusFont));
            statusValue.setBorder(Rectangle.NO_BORDER);
            statusValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
            statusValue.setPadding(5);
            totalTable.addCell(statusValue);

            document.add(totalTable);

            // ============ PIED DE PAGE ============
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Merci de votre confiance !", new Font(Font.HELVETICA, 10, Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph footer2 = new Paragraph("© 2026 VOLY SAINA+ - Tous droits réservés", new Font(Font.HELVETICA, 8));
            footer2.setAlignment(Element.ALIGN_CENTER);
            document.add(footer2);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la generation du PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table, String[] titles, Color backgroundColor) {
        Stream.of(titles).forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(backgroundColor != null ? backgroundColor : new Color(0, 102, 51));
            header.setBorderWidth(1);
            header.setPadding(8);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            header.setPhrase(new Phrase(columnTitle, headerFont));
            table.addCell(header);
        });
    }

    private void addRowsCommande(PdfPTable table, List<PanierDetails> paniers) {
        int numero = 1;
        for (PanierDetails detail : paniers) {
            Commande commande = detail.getCommande();
            List<LigneCommande> lignes = ligneCommandeService.findByIdCommande(commande.getIdCommande());
            for (LigneCommande line : lignes) {
                table.addCell(String.valueOf(numero++));
                table.addCell(line.getProduit().getNom());
                table.addCell(String.valueOf(line.getQuantite()));
                table.addCell(String.valueOf(line.getPrixUnitaire()) + " MGA");
                table.addCell(String.valueOf(line.getSousTotal()) + " MGA");

            }
        }
    }

    private void addRowsReservation(PdfPTable table, List<PanierDetails> paniers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int numero = 1;
        for (PanierDetails detail : paniers) {
            table.addCell(String.valueOf(numero++));
            ReservationMachine reservation = detail.getReservationMachine();
            table.addCell(reservation.getMachine().getNom());
            table.addCell(reservation.getDateDebut().format(formatter));
            table.addCell(reservation.getDateFin().format(formatter));

            long dureeJour = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
            table.addCell(String.valueOf(dureeJour));

            table.addCell(String.valueOf(reservation.getPrixTotal()) + " MGA");
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
