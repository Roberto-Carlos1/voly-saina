package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.entity.FicheCulture;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Produit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GuidePlantationService {

    @Autowired
    private CultureService cultureService;

    @Autowired
    private FicheCultureService ficheCultureService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private ProduitService produitService;

    @Transactional(readOnly = true)
    public List<Culture> listerRessources(String typeRessource, Map<String, String> filtres) {
        if (!"culture".equals(typeRessource)) {
            throw new IllegalArgumentException("Type de ressource non pris en charge: " + typeRessource);
        }

        String motCle = nettoyerFiltre(filtres.get("motCle"));
        String nom = nettoyerFiltre(filtres.get("nom"));
        String description = nettoyerFiltre(filtres.get("description"));
        String saison = nettoyerFiltre(filtres.get("saison"));
        String localisation = nettoyerFiltre(filtres.get("localisation"));

        if (nom == null) {
            nom = motCle;
        }
        if (description == null) {
            description = motCle;
        }

        return cultureService.listerCulturesDisponibles(nom, description, saison, localisation);
    }

    @Transactional(readOnly = true)
    public Page<Culture> listerRessourcesPage(String typeRessource, Map<String, String> filtres, Pageable pageable) {
        if (!"culture".equals(typeRessource)) {
            throw new IllegalArgumentException("Type de ressource non pris en charge: " + typeRessource);
        }

        String motCle = nettoyerFiltrePourRecherche(filtres.get("motCle"));
        String saison = nettoyerFiltrePourRecherche(filtres.get("saison"));
        String localisation = nettoyerFiltre(filtres.get("localisation"));

        localisation = nettoyerFiltrePourRecherche(localisation);

        return cultureService.rechercherCulturesDisponibles(motCle, saison, localisation, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<?> consulterDetail(String typeRessource, Long idCulture) {
        if ("culture".equals(typeRessource)) {
            return cultureService.findById(idCulture);
        }

        if ("fiche_culture".equals(typeRessource)) {
            return ficheCultureService.findByCultureId(idCulture);
        }

        throw new IllegalArgumentException("Type de ressource non pris en charge: " + typeRessource);
    }

    @Transactional(readOnly = true)
    public List<?> listerSuggestions(String typeSuggestion, Long idCulture) {
        consulterDetail("culture", idCulture)
                .orElseThrow(() -> new IllegalArgumentException("Culture introuvable: " + idCulture));

        if ("outil".equals(typeSuggestion)) {
            return machineService.findDisponibles();
        }

        if ("produit".equals(typeSuggestion)) {
            return produitService.findActifs();
        }

        throw new IllegalArgumentException("Type de suggestion non pris en charge: " + typeSuggestion);
    }

    @SuppressWarnings("unchecked")
    public Optional<Culture> consulterDetailCulture(Long idCulture) {
        return (Optional<Culture>) consulterDetail("culture", idCulture);
    }

    @SuppressWarnings("unchecked")
    public Optional<FicheCulture> consulterDetailFicheCulture(Long idCulture) {
        return (Optional<FicheCulture>) consulterDetail("fiche_culture", idCulture);
    }

    @SuppressWarnings("unchecked")
    public List<Machine> listerSuggestionsOutils(Long idCulture) {
        return (List<Machine>) listerSuggestions("outil", idCulture);
    }

    @SuppressWarnings("unchecked")
    public List<Produit> listerSuggestionsProduits(Long idCulture) {
        return (List<Produit>) listerSuggestions("produit", idCulture);
    }

    private String nettoyerFiltre(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        return valeur.trim();
    }

    private String nettoyerFiltrePourRecherche(String valeur) {
        String filtre = nettoyerFiltre(valeur);
        return filtre == null ? "" : filtre;
    }
}
