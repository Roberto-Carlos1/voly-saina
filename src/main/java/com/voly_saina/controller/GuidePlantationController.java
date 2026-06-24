package com.voly_saina.controller;

import com.voly_saina.entity.Culture;
import com.voly_saina.entity.FicheCulture;
import com.voly_saina.service.GuidePlantationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/guide-plantation")
public class GuidePlantationController {

    @Autowired
    private GuidePlantationService guidePlantationService;

    @GetMapping({"", "/", "/cultures"})
    public String listerCultures(@RequestParam(required = false) String nom,
                                 @RequestParam(required = false) String localisation,
                                 @RequestParam(required = false) String region,
                                 @RequestParam(required = false) String saison,
                                 @RequestParam(required = false) String description,
                                 Model model) {
        Map<String, String> filtres = new HashMap<>();
        filtres.put("nom", nom);
        filtres.put("localisation", localisation);
        filtres.put("region", region);
        filtres.put("saison", saison);
        filtres.put("description", description);

        model.addAttribute("cultures", guidePlantationService.listerRessources("culture", filtres));
        model.addAttribute("filtres", filtres);
        return "guide-plantation/cultures";
    }

    @GetMapping("/fiche-culture")
    public String consulterPremiereFicheCulture() {
        List<Culture> cultures = guidePlantationService.listerRessources("culture", new HashMap<>());
        if (cultures.isEmpty()) {
            return "redirect:/guide-plantation/cultures";
        }

        return "redirect:/guide-plantation/cultures/" + cultures.get(0).getIdCulture() + "/fiche";
    }

    @GetMapping("/cultures/{idCulture}")
    public String consulterCulture(@PathVariable Long idCulture, Model model) {
        Culture culture = guidePlantationService.consulterDetailCulture(idCulture)
                .orElseThrow(() -> new IllegalArgumentException("Culture introuvable: " + idCulture));

        model.addAttribute("culture", culture);
        model.addAttribute("ficheCulture", guidePlantationService.consulterDetailFicheCulture(idCulture).orElse(null));
        return "guide-plantation/detail-culture";
    }

    @GetMapping("/cultures/{idCulture}/fiche")
    public String consulterFicheCulture(@PathVariable Long idCulture, Model model) {
        Culture culture = guidePlantationService.consulterDetailCulture(idCulture)
                .orElseThrow(() -> new IllegalArgumentException("Culture introuvable: " + idCulture));
        FicheCulture ficheCulture = guidePlantationService.consulterDetailFicheCulture(idCulture).orElse(null);

        model.addAttribute("culture", culture);
        model.addAttribute("ficheCulture", ficheCulture);
        model.addAttribute("outils", guidePlantationService.listerSuggestionsOutils(idCulture));
        model.addAttribute("produits", guidePlantationService.listerSuggestionsProduits(idCulture));
        return "guide-plantation/fiche-culture";
    }
}
