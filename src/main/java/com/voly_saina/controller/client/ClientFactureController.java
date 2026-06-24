package com.voly_saina.controller.client;

import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.service.client.ClientFactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientFactureController {

    private final ClientFactureService clientFactureService;

    @GetMapping("/client/factures")
    public String listerFactures(
            @RequestParam Long idClient,
            @RequestParam(required = false) String statut,
            Model model) {
        // TODO: remplacer par l'utilisateur connecté via Spring Security
        List<FactureClientDTO> factures = clientFactureService.listerFacturesClient(idClient, statut);
        
        model.addAttribute("factures", factures);
        model.addAttribute("filtreStatut", statut);
        model.addAttribute("idClient", idClient);
        
        return "client/factures/list";
    }

    @GetMapping("/client/factures/{idFacture}")
    public String voirDetailFacture(
            @PathVariable Long idFacture,
            @RequestParam Long idClient,
            Model model) {
        // TODO: remplacer par l'utilisateur connecté via Spring Security
        FactureClientDTO facture = clientFactureService.voirDetailFacture(idFacture, idClient);
        
        model.addAttribute("facture", facture);
        model.addAttribute("idClient", idClient);
        
        return "client/factures/detail";
    }

    @GetMapping("/client/factures/{idFacture}/pdf")
    public ResponseEntity<byte[]> exporterFacturePDF(
            @PathVariable Long idFacture,
            @RequestParam Long idClient) {
        // TODO: remplacer par l'utilisateur connecté via Spring Security
        byte[] pdfBytes = clientFactureService.exporterFacturePDF(idFacture, idClient);
        
        FactureClientDTO facture = clientFactureService.voirDetailFacture(idFacture, idClient);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facture-" + facture.getNumero() + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
