package com.voly_saina.controller.client;

import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.ClientFactureService;
import com.voly_saina.service.client.ClientProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
    private final ClientProfilService clientProfilService;

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        return clientProfilService.getUtilisateurByEmail(user.getUsername());
    }

    @GetMapping("/client/factures")
    public String listerFactures(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String statut,
            Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        List<FactureClientDTO> factures = clientFactureService.listerFacturesClient(utilisateur.getIdUtilisateur(), statut);
        
        model.addAttribute("factures", factures);
        model.addAttribute("filtreStatut", statut);
        model.addAttribute("idClient", utilisateur.getIdUtilisateur());
        
        return "client/factures/list";
    }

    @GetMapping("/client/factures/{idFacture}")
    public String voirDetailFacture(
            @PathVariable Long idFacture,
            @AuthenticationPrincipal User user,
            Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        FactureClientDTO facture = clientFactureService.voirDetailFacture(idFacture, utilisateur.getIdUtilisateur());
        
        model.addAttribute("facture", facture);
        model.addAttribute("idClient", utilisateur.getIdUtilisateur());
        
        return "client/factures/detail";
    }

    @GetMapping("/client/factures/{idFacture}/pdf")
    public ResponseEntity<byte[]> exporterFacturePDF(
            @PathVariable Long idFacture,
            @AuthenticationPrincipal User user) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        byte[] pdfBytes = clientFactureService.exporterFacturePDF(idFacture, utilisateur.getIdUtilisateur());
        
        FactureClientDTO facture = clientFactureService.voirDetailFacture(idFacture, utilisateur.getIdUtilisateur());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facture-" + facture.getNumero() + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
