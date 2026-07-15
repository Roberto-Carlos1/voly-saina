package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.entity.FicheCulture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Produit;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GuidePlantationExportService {

    private static final Color VERT_PRIMAIRE = new Color(0, 76, 34);
    private static final Color VERT_SECONDAIRE = new Color(22, 101, 52);
    private static final Color VERT_CLAIR = new Color(166, 244, 181);
    private static final Color SURFACE = new Color(255, 248, 242);
    private static final Color SURFACE_CONTAINER = new Color(244, 237, 230);
    private static final Color TEXTE = new Color(30, 27, 23);
    private static final Color TEXTE_MUTED = new Color(64, 73, 64);
    private static final Color BORDURE = new Color(191, 201, 189);

    public byte[] exporterCulturesExcel(List<Culture> cultures) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cultures");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nom");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Region / localisation adaptee");
            header.createCell(3).setCellValue("Saison adaptee");

            for (int i = 0; i < cultures.size(); i++) {
                Culture culture = cultures.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(valeur(culture.getNom()));
                row.createCell(1).setCellValue(valeur(culture.getDescription()));
                row.createCell(2).setCellValue(valeur(culture.getLocalisationRecommandee()));
                row.createCell(3).setCellValue(valeur(culture.getSaisonRecommandee()));
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de generer l'export Excel des cultures", exception);
        }
    }

    public byte[] exporterCulturesPdf(List<Culture> cultures) {
        return exporterCulturesPdf(cultures, Collections.emptyMap());
    }

    public byte[] exporterCulturesPdf(List<Culture> cultures, Map<Long, String> imagesCultures) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = creerDocument(outputStream);

            ajouterEntete(document, "Guide de plantation", "Liste des cultures disponibles");
            ajouterTexteIntro(document, "Cultures correspondant aux filtres actuellement affiches dans le guide.");

            if (cultures.isEmpty()) {
                ajouterMessageVide(document, "Aucune culture disponible pour ces criteres.");
            } else {
                PdfPTable table = creerTableau(new float[]{18f, 20f, 34f, 16f, 12f});
                ajouterCelluleEntete(table, "Image");
                ajouterCelluleEntete(table, "Culture");
                ajouterCelluleEntete(table, "Description");
                ajouterCelluleEntete(table, "Localisation");
                ajouterCelluleEntete(table, "Saison");

                for (Culture culture : cultures) {
                    ajouterCelluleImage(table, imagesCultures.get(culture.getIdCulture()), 70, 46);
                    ajouterCellule(table, valeur(culture.getNom()), true);
                    ajouterCellule(table, valeur(culture.getDescription()), false);
                    ajouterCellule(table, valeur(culture.getLocalisationRecommandee()), false);
                    ajouterCellule(table, valeur(culture.getSaisonRecommandee()), false);
                }
                document.add(table);
            }

            ajouterPiedDePage(document);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new IllegalStateException("Impossible de generer l'export PDF des cultures", exception);
        }
    }

    public byte[] exporterFicheCulturePdf(Culture culture, FicheCulture ficheCulture, List<?> outils, List<?> produits) {
        return exporterFicheCulturePdf(culture, ficheCulture, outils, produits, null);
    }

    public byte[] exporterFicheCulturePdf(Culture culture, FicheCulture ficheCulture, List<?> outils, List<?> produits, String imageCulture) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = creerDocument(outputStream);

            ajouterEntete(document, "Fiche detaillee", valeur(culture.getNom()));
            ajouterImagePrincipale(document, imageCulture, culture);
            ajouterCarteCulture(document, culture);

            if (ficheCulture == null) {
                ajouterMessageVide(document, "Aucune fiche detaillee disponible pour cette culture.");
            } else {
                ajouterTitreSection(document, "Guide technique detaille");
                PdfPTable ficheTable = creerTableau(new float[]{30f, 70f});
                ajouterLigneInfo(ficheTable, "Periode de plantation", ficheCulture.getPeriodePlantation());
                ajouterLigneInfo(ficheTable, "Duree avant recolte", ficheCulture.getDureeAvantRecolte());
                ajouterLigneInfo(ficheTable, "Preparation du sol", ficheCulture.getPreparationSol());
                ajouterLigneInfo(ficheTable, "Quantite de semence", ficheCulture.getQuantiteSemence());
                ajouterLigneInfo(ficheTable, "Engrais recommandes", ficheCulture.getEngraisRecommandes());
                ajouterLigneInfo(ficheTable, "Arrosage", ficheCulture.getArrosage());
                ajouterLigneInfo(ficheTable, "Maladies courantes", ficheCulture.getMaladiesCourantes());
                ajouterLigneInfo(ficheTable, "Conseils pratiques", ficheCulture.getConseilsPratiques());
                document.add(ficheTable);
            }

            ajouterEquipements(document, outils);
            ajouterProduits(document, produits);

            ajouterPiedDePage(document);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new IllegalStateException("Impossible de generer l'export PDF de la fiche culture", exception);
        }
    }

    private Document creerDocument(ByteArrayOutputStream outputStream) {
        Document document = new Document(PageSize.A4, 42, 42, 42, 42);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        return document;
    }

    private void ajouterEntete(Document document, String titre, String sousTitre) throws DocumentException {
        PdfPTable entete = new PdfPTable(2);
        entete.setWidthPercentage(100);
        entete.setWidths(new float[]{58f, 42f});

        PdfPCell marque = new PdfPCell();
        marque.setBorder(Rectangle.NO_BORDER);
        marque.setBackgroundColor(VERT_PRIMAIRE);
        marque.setPadding(14);
        marque.addElement(new Paragraph("VolySaina+", new Font(Font.HELVETICA, 24, Font.BOLD, Color.WHITE)));
        marque.addElement(new Paragraph("Guide agricole", new Font(Font.HELVETICA, 10, Font.NORMAL, VERT_CLAIR)));
        entete.addCell(marque);

        PdfPCell contexte = new PdfPCell();
        contexte.setBorder(Rectangle.NO_BORDER);
        contexte.setBackgroundColor(SURFACE_CONTAINER);
        contexte.setPadding(14);
        Paragraph titrePdf = new Paragraph(titre, new Font(Font.HELVETICA, 18, Font.BOLD, VERT_PRIMAIRE));
        titrePdf.setAlignment(Element.ALIGN_RIGHT);
        contexte.addElement(titrePdf);
        Paragraph sousTitrePdf = new Paragraph(sousTitre, new Font(Font.HELVETICA, 11, Font.NORMAL, TEXTE_MUTED));
        sousTitrePdf.setAlignment(Element.ALIGN_RIGHT);
        contexte.addElement(sousTitrePdf);
        entete.addCell(contexte);

        document.add(entete);
        document.add(Chunk.NEWLINE);
    }

    private void ajouterTexteIntro(Document document, String texte) throws DocumentException {
        Paragraph paragraphe = new Paragraph(texte, new Font(Font.HELVETICA, 11, Font.NORMAL, TEXTE_MUTED));
        paragraphe.setSpacingAfter(12);
        document.add(paragraphe);
    }

    private void ajouterCarteCulture(Document document, Culture culture) throws DocumentException {
        PdfPTable carte = creerTableau(new float[]{30f, 70f});
        ajouterLigneInfo(carte, "Description", culture.getDescription());
        ajouterLigneInfo(carte, "Localisation adaptee", culture.getLocalisationRecommandee());
        ajouterLigneInfo(carte, "Saison adaptee", culture.getSaisonRecommandee());
        document.add(carte);
        document.add(Chunk.NEWLINE);
    }

    private void ajouterTitreSection(Document document, String titre) throws DocumentException {
        PdfPTable section = new PdfPTable(1);
        section.setWidthPercentage(100);
        PdfPCell cellule = new PdfPCell(new Phrase(titre, new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE)));
        cellule.setBackgroundColor(VERT_SECONDAIRE);
        cellule.setBorderColor(VERT_SECONDAIRE);
        cellule.setPadding(8);
        section.addCell(cellule);
        section.setSpacingBefore(8);
        section.setSpacingAfter(8);
        document.add(section);
    }

    private PdfPTable creerTableau(float[] largeurs) throws DocumentException {
        PdfPTable table = new PdfPTable(largeurs.length);
        table.setWidthPercentage(100);
        table.setWidths(largeurs);
        table.setSpacingAfter(10);
        return table;
    }

    private void ajouterLigneInfo(PdfPTable table, String libelle, String contenu) {
        PdfPCell celluleLibelle = new PdfPCell(new Phrase(libelle, new Font(Font.HELVETICA, 10, Font.BOLD, VERT_PRIMAIRE)));
        celluleLibelle.setBackgroundColor(SURFACE_CONTAINER);
        celluleLibelle.setBorderColor(BORDURE);
        celluleLibelle.setPadding(7);
        table.addCell(celluleLibelle);

        PdfPCell celluleContenu = new PdfPCell(new Phrase(valeur(contenu), new Font(Font.HELVETICA, 10, Font.NORMAL, TEXTE)));
        celluleContenu.setBackgroundColor(SURFACE);
        celluleContenu.setBorderColor(BORDURE);
        celluleContenu.setPadding(7);
        table.addCell(celluleContenu);
    }

    private void ajouterCelluleEntete(PdfPTable table, String texte) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)));
        cellule.setBackgroundColor(VERT_PRIMAIRE);
        cellule.setBorderColor(VERT_PRIMAIRE);
        cellule.setPadding(7);
        cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellule);
    }

    private void ajouterCellule(PdfPTable table, String texte, boolean important) {
        Font police = new Font(Font.HELVETICA, 9, important ? Font.BOLD : Font.NORMAL, important ? VERT_PRIMAIRE : TEXTE);
        PdfPCell cellule = new PdfPCell(new Phrase(valeur(texte), police));
        cellule.setBorderColor(BORDURE);
        cellule.setPadding(6);
        cellule.setBackgroundColor(Color.WHITE);
        table.addCell(cellule);
    }

    private void ajouterImagePrincipale(Document document, String cheminImage, Culture culture) throws DocumentException {
        Image image = chargerImageCulture(cheminImage, 510, 160);
        if (image == null) {
            return;
        }

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cellule = new PdfPCell(image, true);
        cellule.setBorderColor(BORDURE);
        cellule.setBackgroundColor(SURFACE_CONTAINER);
        cellule.setPadding(4);
        cellule.setFixedHeight(170);
        cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellule.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellule);
        table.setSpacingAfter(10);
        document.add(table);
    }

    private void ajouterCelluleImage(PdfPTable table, String cheminImage, float largeur, float hauteur) {
        Image image = chargerImageCulture(cheminImage, largeur, hauteur);
        PdfPCell cellule;

        if (image == null) {
            cellule = new PdfPCell(new Phrase("-", new Font(Font.HELVETICA, 9, Font.NORMAL, TEXTE_MUTED)));
        } else {
            cellule = new PdfPCell(image, true);
        }

        cellule.setBorderColor(BORDURE);
        cellule.setBackgroundColor(SURFACE_CONTAINER);
        cellule.setPadding(4);
        cellule.setFixedHeight(56);
        cellule.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellule.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellule);
    }

    private Image chargerImageCulture(String cheminImage, float largeur, float hauteur) {
        if (cheminImage == null || cheminImage.isBlank()) {
            return null;
        }

        try {
            String cheminRessource = cheminImage.startsWith("/") ? "static" + cheminImage : "static/" + cheminImage;
            Image image;
            try (InputStream inputStream = new ClassPathResource(cheminRessource).getInputStream()) {
                image = Image.getInstance(inputStream.readAllBytes());
            }
            image.scaleToFit(largeur, hauteur);
            image.setAlignment(Element.ALIGN_CENTER);
            return image;
        } catch (IOException | DocumentException exception) {
            return null;
        }
    }

    private void ajouterEquipements(Document document, List<?> outils) throws DocumentException {
        ajouterTitreSection(document, "Equipements disponibles");
        if (outils == null || outils.isEmpty()) {
            ajouterMessageVide(document, "Aucun equipement disponible.");
            return;
        }

        PdfPTable table = creerTableau(new float[]{24f, 38f, 20f, 18f});
        ajouterCelluleEntete(table, "Nom");
        ajouterCelluleEntete(table, "Description");
        ajouterCelluleEntete(table, "Localisation");
        ajouterCelluleEntete(table, "Prix/jour");
        for (Object outil : outils) {
            if (outil instanceof Machine machine) {
                ajouterCellule(table, valeur(machine.getNom()), true);
                ajouterCellule(table, valeur(machine.getDescription()), false);
                ajouterCellule(table, valeur(machine.getLocalisation()), false);
                ajouterCellule(table, machine.getPrixJour() == null ? "" : machine.getPrixJour() + " MGA", false);
            }
        }
        document.add(table);
    }

    private void ajouterProduits(Document document, List<?> produits) throws DocumentException {
        ajouterTitreSection(document, "Produits et intrants suggeres");
        if (produits == null || produits.isEmpty()) {
            ajouterMessageVide(document, "Aucun produit disponible.");
            return;
        }

        PdfPTable table = creerTableau(new float[]{24f, 36f, 24f, 16f});
        ajouterCelluleEntete(table, "Produit");
        ajouterCelluleEntete(table, "Description");
        ajouterCelluleEntete(table, "Conseil d'utilisation");
        ajouterCelluleEntete(table, "Prix");
        for (Object produit : produits) {
            if (produit instanceof Produit produitCulture) {
                ajouterCellule(table, valeur(produitCulture.getNom()), true);
                ajouterCellule(table, valeur(produitCulture.getDescription()), false);
                ajouterCellule(table, valeur(produitCulture.getConseilUsage()), false);
                ajouterCellule(table, produitCulture.getPrixUnitaire() == null ? "" : produitCulture.getPrixUnitaire() + " MGA", false);
            }
        }
        document.add(table);
    }

    private void ajouterMessageVide(Document document, String message) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cellule = new PdfPCell(new Phrase(message, new Font(Font.HELVETICA, 10, Font.ITALIC, TEXTE_MUTED)));
        cellule.setBackgroundColor(SURFACE_CONTAINER);
        cellule.setBorderColor(BORDURE);
        cellule.setPadding(10);
        table.addCell(cellule);
        table.setSpacingAfter(10);
        document.add(table);
    }

    private void ajouterPiedDePage(Document document) throws DocumentException {
        Paragraph espace = new Paragraph(" ");
        espace.setSpacingBefore(8);
        document.add(espace);

        Paragraph footer = new Paragraph("VolySaina+ - Guide de plantation", new Font(Font.HELVETICA, 9, Font.ITALIC, TEXTE_MUTED));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private String valeur(String valeur) {
        return valeur == null ? "" : valeur;
    }
}
