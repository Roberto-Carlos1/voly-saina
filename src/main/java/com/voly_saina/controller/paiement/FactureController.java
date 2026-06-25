package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.Pages;
import com.voly_saina.entity.StatutFacture;
import com.voly_saina.entity.dto.FactureDTO;
import com.voly_saina.service.FactureService;
import com.voly_saina.service.PageService;
import com.voly_saina.service.StatutFactureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Controller
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

    private final PageService pageService;
    private final StatutFactureService statutFactureService;

    public FactureController(PageService pageService, StatutFactureService statutFacture) {
        this.pageService = pageService;
        this.statutFactureService = statutFacture;
    }

    @GetMapping
    public String getAll(@RequestParam(defaultValue = "0") int page, Model model) {
        List<StatutFacture> status = statutFactureService.findAll();

        Pages config = pageService.getConfiguration();
        int size = config.getNombre();

        Pageable pageable = PageRequest.of(page, size);
        Page<Facture> facturePage = factureService.findByPage(pageable);
        List<Facture> factures = facturePage.getContent();

        model.addAttribute("factures", factures);
        model.addAttribute("statuts", status);
        model.addAttribute("totalPages", facturePage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "facturation/list";
    }

    @PostMapping("/pages")
    public String nombrePages(@RequestParam("pages") int page, RedirectAttributes attributes) {
        Pages p = pageService.findById(1L);
        if (page <= 0) {
            attributes.addFlashAttribute("error", "Entrez un nombre de pages valide");
            return "redirect:/api/factures";
        } else {
            p.setNombre(page);
            pageService.save(p);
        }
        return "redirect:/api/factures";
    }

    // GET /api/factures/{id}
    @GetMapping("/{id}")
    public String getFactureById(@PathVariable Long id, Model model) {
        Facture facture = factureService.findById(id);
        
        model.addAttribute("facture", facture);
        return "facturation/detail-facture";
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

    @PostMapping("/filtre")
    public ResponseEntity<Page<Facture>> filtreFacture(FactureDTO facturedto) {

        // List<Machine> liste = machineService.filtrerMachine(typeID, name);
        Pages config = pageService.getConfiguration();
        Pageable pageable = PageRequest.of(facturedto.getNumeroPage(), config.getNombre());

        Page<Facture> factures = factureService.filtreFacture(facturedto, pageable);
        return ResponseEntity.ok(factures);
    }
}
