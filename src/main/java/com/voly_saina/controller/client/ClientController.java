package com.voly_saina.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client")
public class ClientController {

    @GetMapping("/accueil")
    public String accueil(@RequestParam Long idClient, Model model) {
        // TODO: remplacer par l'utilisateur connecté via Spring Security
        model.addAttribute("idClient", idClient);
        return "client/accueil";
    }
}
