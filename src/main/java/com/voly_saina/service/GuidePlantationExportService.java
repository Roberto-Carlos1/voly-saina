package com.voly_saina.service;

import com.voly_saina.entity.Culture;
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
