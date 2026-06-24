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
import java.util.Map;

@Controller
@RequestMapping("/guide-plantation")
public class GuidePlantationController {

    @Autowired
    private GuidePlantationService guidePlantationService;

    @GetMapping({"", "/", "/cultures"})
    public String listerCultures(@RequestParam(required = false) String localisation,
                                 Model model) {
        Map<String, String> filtres = new HashMap<>();
        filtres.put("localisation", localisation);

        model.addAttribute("cultures", guidePlantationService.listerRessources("culture", filtres));
        model.addAttribute("filtres", filtres);
        return "guide-plantation/cultures";
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
