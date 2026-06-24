package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.repository.CultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CultureService {

    @Autowired
    private CultureRepository cultureRepository;

    public List<Culture> findAll() {
        return cultureRepository.findAll();
    }

    public Optional<Culture> findById(Long id) {
        return cultureRepository.findById(id);
    }

    public Culture save(Culture culture) {
        return cultureRepository.save(culture);
    }

    public boolean existsById(Long id) {
        return cultureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        cultureRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Culture> listerCulturesDisponibles(String nom, String description, String saison, String localisation) {
        return cultureRepository.findByActifTrue().stream()
                .filter(culture -> contient(culture.getNom(), nom))
                .filter(culture -> contient(culture.getDescription(), description))
                .filter(culture -> contient(culture.getSaisonRecommandee(), saison))
                .filter(culture -> contient(culture.getLocalisationRecommandee(), localisation))
                .toList();
    }

    private boolean contient(String valeur, String filtre) {
        if (filtre == null || filtre.isBlank()) {
            return true;
        }
        if (valeur == null) {
            return false;
        }
        return valeur.toLowerCase().contains(filtre.toLowerCase());
    }
}
