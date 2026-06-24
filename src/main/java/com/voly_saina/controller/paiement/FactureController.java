package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Facture;
<<<<<<< Updated upstream
import com.voly_saina.entity.Paiement;
=======
<<<<<<< Updated upstream
=======
import com.voly_saina.entity.OperationMachine;
import com.voly_saina.entity.OperationProduit;
import com.voly_saina.entity.Paiement;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
import com.voly_saina.service.FactureService;
import com.voly_saina.service.OperationMachineService;
import com.voly_saina.service.OperationProduitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

<<<<<<< Updated upstream
    // GET /api/factures
<<<<<<< Updated upstream
    // @GetMapping
    // public ResponseEntity<List<Facture>> getAll() {
    // return ResponseEntity.ok(factureService.findAll());
    // }

=======
=======
    private final OperationMachineService operationMachineService;
    private final OperationProduitService operationProduitService;

    public FactureController(OperationMachineService operationMachineService,
            OperationProduitService operationProduitService) {
        this.operationMachineService = operationMachineService;
        this.operationProduitService = operationProduitService;
    }

>>>>>>> Stashed changes
>>>>>>> Stashed changes
    @GetMapping
    public String getAll(Model model) {
        List<Facture> liste = factureService.findAll();

        model.addAttribute("factures", liste);
        return "facturation/list";
    }

    // GET /api/factures/{id}
    @GetMapping("/{id}")
<<<<<<< Updated upstream
    public String getFactureById(@PathVariable Long id, Model model) {
        Facture facture = factureService.findById(id);

        model.addAttribute("facture", facture);
        return "facturation/detail-facture";
=======
<<<<<<< Updated upstream
    public ResponseEntity<Facture> getById(@PathVariable Long id) {
        return factureService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
=======
    public String getFactureById(@PathVariable Long id, Model model) {
        Facture facture = factureService.findById(id);

        List<OperationMachine> operationMachine = operationMachineService.findByIdFacture(id);
        List<OperationProduit> operationProduit = operationProduitService.findByIdFacture(id);

        model.addAttribute("facture", facture);
        model.addAttribute("operationMachine", operationMachine);
        model.addAttribute("operationProduit", operationProduit);

        return "facturation/detail-facture";
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    }

    // POST /api/factures
    @PostMapping
    public ResponseEntity<Facture> create(@RequestBody Facture facture) {
        Facture saved = factureService.save(facture);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/factures/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Facture> update(@PathVariable Long id, @RequestBody Facture facture) {
        if (!factureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        facture.setIdFacture(id);
        Facture updated = factureService.save(facture);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/factures/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!factureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        factureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
