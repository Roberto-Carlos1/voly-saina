package com.voly_saina.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client/statistiques")
@RequiredArgsConstructor
public class ClientStatistiqueController {

    // Les statistiques client sont désormais intégrées à la page profil
    // (onglet "Statistiques") : on y redirige en conservant les filtres.
    @GetMapping
    public String statistiques(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String periode,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("tab", "stats");
        if (periode != null && !periode.isEmpty()) {
            redirectAttributes.addAttribute("periode", periode);
        }
        if (dateDebut != null && !dateDebut.isEmpty()) {
            redirectAttributes.addAttribute("dateDebut", dateDebut);
        }
        if (dateFin != null && !dateFin.isEmpty()) {
            redirectAttributes.addAttribute("dateFin", dateFin);
        }
        return "redirect:/client/profil";
    }
}
