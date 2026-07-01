package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.repository.CultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<String> findLocalisationsDisponibles() {
        return cultureRepository.findLocalisationsDisponibles();
    }

    @Transactional(readOnly = true)
    public List<String> findSaisonsDisponibles() {
        return cultureRepository.findSaisonsDisponibles();
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

    @Transactional(readOnly = true)
    public Page<Culture> rechercherCulturesDisponibles(String motCle, String saison, String localisation, Pageable pageable) {
        Pageable pagination = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if (estTriDesc(pageable)) {
            return cultureRepository.rechercherCulturesDisponiblesTrieesDesc(motCle, saison, localisation, pagination);
        }
        return cultureRepository.rechercherCulturesDisponiblesTrieesAsc(motCle, saison, localisation, pagination);
    }

    private boolean estTriDesc(Pageable pageable) {
        Sort.Order triNom = pageable.getSort().getOrderFor("nom");
        return triNom != null && triNom.isDescending();
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
