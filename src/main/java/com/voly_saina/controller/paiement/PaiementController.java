package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.Paiement;
import com.voly_saina.entity.dto.PaiementDTO;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.PaiementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final FactureService factureService;
    private final ModePaiementService modePaiementService;

    // GET /api/paiements
    // @GetMapping
    // public ResponseEntity<List<Paiement>> getAll() {
    // return ResponseEntity.ok(paiementService.findAll());
    // }

    public PaiementController(PaiementService paiementService, FactureService factureService,
            ModePaiementService modePaiementService) {
        this.paiementService = paiementService;
        this.factureService = factureService;
        this.modePaiementService = modePaiementService;
    }

    @GetMapping
    public String getAll(Model model) {
        List<Paiement> liste = paiementService.findAll();

        model.addAttribute("paiements", liste);

        return "paiements/list";
    }

    // GET /api/paiements/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getById(@PathVariable Long id) {
        return paiementService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/reste/{id}")
    public String getResteById(@PathVariable Long id, Model model) {
        Facture facture = factureService.findById(id);
        model.addAttribute("facture", facture);

        List<ModePaiement> listeMode = modePaiementService.findAll();
        model.addAttribute("modes", listeMode);

        return "paiements/form-reste";
    }

    @PostMapping("/restePayee")
    public String payerReste(PaiementDTO paiementDTO) {
        Long id = Long.parseLong(paiementDTO.getIdFacture());
        double montant = Double.parseDouble(paiementDTO.getMontant());
        BigDecimal m = BigDecimal.valueOf(montant);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime date = LocalDateTime.parse(paiementDTO.getDate(), format);

        String mode = paiementDTO.getModePaiement();
        ModePaiement modePaiement = modePaiementService.findById(Long.parseLong(mode));

        Facture f = factureService.findById(id);

        Paiement p = new Paiement();
        p.setFacture(f);
        p.setMontant(m);
        p.setDatePaiement(date);
        p.setModePaiement(modePaiement);

        paiementService.save(p);

        f.setMontantPaye(f.getMontantPaye().add(m));
        if (f.getMontantPaye() == f.getMontantTotal()) {
            f.setIdFacture(2L);
        }
        factureService.save(f);

        return "redirect:/api/factures/" + id;
    }

    // POST /api/paiements
    @PostMapping
    public ResponseEntity<Paiement> create(@RequestBody Paiement paiement) {
        Paiement saved = paiementService.save(paiement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/paiements/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Paiement> update(@PathVariable Long id, @RequestBody Paiement paiement) {
        if (!paiementService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paiement.setIdPaiement(id);
        Paiement updated = paiementService.save(paiement);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/paiements/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!paiementService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paiementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/facture/{id}")
    public String historiquePaiementFacture(@PathVariable Long id, Model model) {
        Facture facture = factureService.findById(id);

        List<Paiement> paiements = paiementService.findByFacture(id);

        model.addAttribute("paiements", paiements);
        model.addAttribute("facture", facture);

        return "paiements/historique";
    }
}
