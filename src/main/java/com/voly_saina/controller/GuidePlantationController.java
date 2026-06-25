package com.voly_saina.controller;

import com.voly_saina.entity.Culture;
import com.voly_saina.entity.FicheCulture;
import com.voly_saina.service.GuidePlantationExportService;
import com.voly_saina.service.GuidePlantationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/guide-plantation")
public class GuidePlantationController {

    @Autowired
    private GuidePlantationService guidePlantationService;

    @Autowired
    private GuidePlantationExportService guidePlantationExportService;

    @GetMapping({"", "/", "/cultures"})
    public String listerCultures(@RequestParam(required = false) String motCle,
                                 @RequestParam(required = false) String localisation,
                                 @RequestParam(required = false) String saison,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "nom_asc") String tri,
                                 Model model) {
        Map<String, String> filtres = construireFiltres(motCle, localisation, saison);
        Pageable pageable = construirePageable(page, size, tri);
        Page<Culture> culturesPage = guidePlantationService.listerRessourcesPage("culture", filtres, pageable);

        model.addAttribute("culturesPage", culturesPage);
        model.addAttribute("cultures", culturesPage.getContent());
        model.addAttribute("filtres", filtres);
        model.addAttribute("tri", tri);
        model.addAttribute("size", size);
        return "guide-plantation/cultures";
    }

    @GetMapping("/cultures/export/pdf")
    public ResponseEntity<byte[]> exporterCulturesPdf(@RequestParam(required = false) String motCle,
                                                      @RequestParam(required = false) String localisation,
                                                      @RequestParam(required = false) String saison,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "nom_asc") String tri) {
        Page<Culture> culturesPage = rechercherPageCultures(motCle, localisation, saison, page, size, tri);
        byte[] contenu = guidePlantationExportService.exporterCulturesPdf(culturesPage.getContent());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guide-plantation-cultures.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenu);
    }

    @GetMapping("/cultures/export/excel")
    public ResponseEntity<byte[]> exporterCulturesExcel(@RequestParam(required = false) String motCle,
                                                        @RequestParam(required = false) String localisation,
                                                        @RequestParam(required = false) String saison,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "nom_asc") String tri) {
        Page<Culture> culturesPage = rechercherPageCultures(motCle, localisation, saison, page, size, tri);
        byte[] contenu = guidePlantationExportService.exporterCulturesExcel(culturesPage.getContent());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guide-plantation-cultures.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenu);
    }

    @GetMapping("/cultures/{idCulture}")
    public String consulterCulture(@PathVariable Long idCulture, Model model) {
        Culture culture = consulterCultureExistante(idCulture);
        FicheCulture ficheCulture = guidePlantationService.consulterDetail("fiche_culture", idCulture)
                .map(FicheCulture.class::cast)
                .orElse(null);

        model.addAttribute("culture", culture);
        model.addAttribute("ficheCulture", ficheCulture);
        return "guide-plantation/detail-culture";
    }

    @GetMapping("/cultures/{idCulture}/fiche")
    public String consulterFicheCulture(@PathVariable Long idCulture, Model model) {
        Culture culture = consulterCultureExistante(idCulture);
        FicheCulture ficheCulture = guidePlantationService.consulterDetail("fiche_culture", idCulture)
                .map(FicheCulture.class::cast)
                .orElse(null);

        model.addAttribute("culture", culture);
        model.addAttribute("ficheCulture", ficheCulture);
        model.addAttribute("outils", guidePlantationService.listerSuggestions("outil", idCulture));
        model.addAttribute("produits", guidePlantationService.listerSuggestions("produit", idCulture));
        return "guide-plantation/fiche-culture";
    }

    private Culture consulterCultureExistante(Long idCulture) {
        return guidePlantationService.consulterDetail("culture", idCulture)
                .map(Culture.class::cast)
                .orElseThrow(() -> new IllegalArgumentException("Culture introuvable: " + idCulture));
    }

    private Page<Culture> rechercherPageCultures(String motCle, String localisation, String saison,
                                                 int page, int size, String tri) {
        return guidePlantationService.listerRessourcesPage(
                "culture",
                construireFiltres(motCle, localisation, saison),
                construirePageable(page, size, tri));
    }

    private Map<String, String> construireFiltres(String motCle, String localisation, String saison) {
        Map<String, String> filtres = new HashMap<>();
        filtres.put("motCle", motCle);
        filtres.put("localisation", localisation);
        filtres.put("saison", saison);
        return filtres;
    }

    private Pageable construirePageable(int page, int size, String tri) {
        int pageCourante = Math.max(page, 0);
        int taillePage = Math.max(1, Math.min(size, 100));
        Sort.Direction direction = "nom_desc".equals(tri) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(pageCourante, taillePage, Sort.by(direction, "nom"));
    }
}
