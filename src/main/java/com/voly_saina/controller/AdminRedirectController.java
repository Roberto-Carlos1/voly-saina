package com.voly_saina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la racine de l'espace administrateur vers le tableau de bord.
 * L'accès est déjà restreint au rôle RESPONSABLE par la règle "/admin/**"
 * définie dans SecurityConfig, donc aucun contrôle supplémentaire n'est requis.
 */
@Controller
public class AdminRedirectController {

    @GetMapping("/admin")
    public String versDashboard() {
        return "redirect:/admin/dashboard";
    }
}
