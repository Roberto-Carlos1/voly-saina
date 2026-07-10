package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.entity.FicheCulture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Produit;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GuidePlantationExportService {

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
        List<String> lignes = new ArrayList<>();
        lignes.add("Guide de plantation - Cultures");
        lignes.add("");

        if (cultures.isEmpty()) {
            lignes.add("Aucune culture disponible.");
        } else {
            for (Culture culture : cultures) {
                lignes.add("Culture: " + valeur(culture.getNom()));
                lignes.add("Description: " + valeur(culture.getDescription()));
                lignes.add("Region / localisation adaptee: " + valeur(culture.getLocalisationRecommandee()));
                lignes.add("Saison adaptee: " + valeur(culture.getSaisonRecommandee()));
                lignes.add("");
            }
        }

        return construirePdfSimple(lignes);
    }

    public byte[] exporterFicheCulturePdf(Culture culture, FicheCulture ficheCulture, List<?> outils, List<?> produits) {
        List<String> lignes = new ArrayList<>();
        lignes.add("Fiche detaillee - " + valeur(culture.getNom()));
        lignes.add("");
        lignes.add("Description: " + valeur(culture.getDescription()));
        lignes.add("Localisation adaptee: " + valeur(culture.getLocalisationRecommandee()));
        lignes.add("Saison adaptee: " + valeur(culture.getSaisonRecommandee()));
        lignes.add("");

        if (ficheCulture == null) {
            lignes.add("Aucune fiche detaillee disponible pour cette culture.");
        } else {
            lignes.add("Periode de plantation: " + valeur(ficheCulture.getPeriodePlantation()));
            lignes.add("Duree avant recolte: " + valeur(ficheCulture.getDureeAvantRecolte()));
            lignes.add("Preparation du sol: " + valeur(ficheCulture.getPreparationSol()));
            lignes.add("Quantite de semence: " + valeur(ficheCulture.getQuantiteSemence()));
            lignes.add("Engrais recommandes: " + valeur(ficheCulture.getEngraisRecommandes()));
            lignes.add("Arrosage: " + valeur(ficheCulture.getArrosage()));
            lignes.add("Maladies courantes: " + valeur(ficheCulture.getMaladiesCourantes()));
            lignes.add("Conseils pratiques: " + valeur(ficheCulture.getConseilsPratiques()));
        }

        lignes.add("");
        lignes.add("Outils recommandes:");
        if (outils == null || outils.isEmpty()) {
            lignes.add("- Aucun outil disponible.");
        } else {
            for (Object outil : outils) {
                if (outil instanceof Machine machine) {
                    lignes.add("- " + valeur(machine.getNom()) + " | " + valeur(machine.getDescription())
                            + " | " + valeur(machine.getLocalisation()));
                }
            }
        }

        lignes.add("");
        lignes.add("Produits suggeres:");
        if (produits == null || produits.isEmpty()) {
            lignes.add("- Aucun produit disponible.");
        } else {
            for (Object produit : produits) {
                if (produit instanceof Produit produitCulture) {
                    lignes.add("- " + valeur(produitCulture.getNom()) + " | " + valeur(produitCulture.getDescription())
                            + " | " + valeur(produitCulture.getConseilUsage()));
                }
            }
        }

        return construirePdfSimple(lignes);
    }

    private byte[] construirePdfSimple(List<String> lignes) {
        StringBuilder contenu = new StringBuilder();
        contenu.append("BT\n/F1 11 Tf\n50 790 Td\n14 TL\n");
        for (String ligne : lignes) {
            contenu.append("(").append(echapperPdf(ligne)).append(") Tj\nT*\n");
        }
        contenu.append("ET\n");

        byte[] contenuBytes = contenu.toString().getBytes(StandardCharsets.ISO_8859_1);
        List<String> objets = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + contenuBytes.length + " >>\nstream\n" + contenu + "endstream"
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<Integer> positions = new ArrayList<>();
        ecrire(outputStream, "%PDF-1.4\n");

        for (int i = 0; i < objets.size(); i++) {
            positions.add(outputStream.size());
            ecrire(outputStream, (i + 1) + " 0 obj\n");
            ecrire(outputStream, objets.get(i));
            ecrire(outputStream, "\nendobj\n");
        }

        int xrefPosition = outputStream.size();
        ecrire(outputStream, "xref\n0 " + (objets.size() + 1) + "\n");
        ecrire(outputStream, "0000000000 65535 f \n");
        for (Integer position : positions) {
            ecrire(outputStream, String.format("%010d 00000 n \n", position));
        }
        ecrire(outputStream, "trailer\n<< /Size " + (objets.size() + 1) + " /Root 1 0 R >>\n");
        ecrire(outputStream, "startxref\n" + xrefPosition + "\n%%EOF");
        return outputStream.toByteArray();
    }

    private void ecrire(ByteArrayOutputStream outputStream, String valeur) {
        try {
            outputStream.write(valeur.getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible d'ecrire l'export PDF", exception);
        }
    }

    private String echapperPdf(String valeur) {
        return valeur(valeur)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private String valeur(String valeur) {
        return valeur == null ? "" : valeur;
    }
}
