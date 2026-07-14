package com.voly_saina.controller.client;

import com.voly_saina.dto.FactureClientDTO;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.dto.PaiementDTO;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.PaiementService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientFactureController {

    private final ClientFactureService clientFactureService;

    private final FactureService factureService;

    private final ModePaiementService modePaiementService;

    private final PaiementService paiementService;

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
        List<FactureClientDTO> factures = clientFactureService.listerFacturesClient(utilisateur.getIdUtilisateur(),
                statut);

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

        Facture f = factureService.findById(idFacture);
        if (f != null && f.getPanier() != null) {
            clientFactureService.detailsFacture(idFacture, utilisateur.getIdUtilisateur(), model);
        } else {
            model.addAttribute("reservations", List.of());
            model.addAttribute("commandes", List.of());
            model.addAttribute("lignes", List.of());
        }

        return "client/factures/detail";
    }

    @GetMapping("/client/factures/paiement")
    public String paimentFacture(
            @RequestParam(value = "idFacture") Long idFacture,
            @RequestParam Long idClient,
            Model model) {

        Facture factureClient = factureService.findById(idFacture);
        FactureClientDTO facture = clientFactureService.voirDetailFacture(factureClient.getIdFacture(), idClient);

        model.addAttribute("facture", facture);
        model.addAttribute("idClient", idClient);

        List<ModePaiement> modePaiements = modePaiementService.findAll();
        model.addAttribute("modes", modePaiements);

        return "client/factures/paiement";
    }

    @PostMapping("/client/factures/achat")
    public String payerFacture(PaiementDTO paiementDTO, Model model, RedirectAttributes redirectAttributes) {
        Facture f;
        try {
            paiementService.payerFacture(paiementDTO);
            f = factureService.findById(Long.valueOf(paiementDTO.getIdFacture()));
            model.addAttribute("statut", f.getStatutFacture());
            model.addAttribute("idClient", f.getClient().getIdUtilisateur());

        } catch (Exception e) {
            // 1. On prépare les paramètres de l'URL pour la redirection
            redirectAttributes.addAttribute("idFacture", Long.valueOf(paiementDTO.getIdFacture()));
            redirectAttributes.addAttribute("idClient", Long.valueOf(paiementDTO.getIdClient()));

            // 2. On ajoute le message d'erreur (il sera stocké temporairement en session)
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());

            // 3. On redirige vers le mapping GET
            return "redirect:/client/factures/paiement";
        }
        return "redirect:/client/factures/" + f.getIdFacture() + "?idClient=" + f.getClient().getIdUtilisateur();
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
    @GetMapping("/api/client/{idClient}/factures")
    public ResponseEntity<List<FactureClientDTO>> getFacturesByClient(@PathVariable Long idClient) {
        List<FactureClientDTO> factures = clientFactureService.listerFacturesClient(idClient, null);
        return ResponseEntity.ok(factures);
    }
}
