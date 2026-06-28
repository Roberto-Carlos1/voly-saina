package com.voly_saina.service;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.dto.FactureDTO;
import com.voly_saina.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    public Facture findById(Long id) {
        return factureRepository.findById(id).orElse(null);
    }

    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public boolean existsById(Long id) {
        return factureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        factureRepository.deleteById(id);
    }

    public Page<Facture> findByPage(Pageable pageable) {
        Page<Facture> page = factureRepository.findAll(pageable);
        List<Long> ids = new ArrayList<>();

        for (Facture facture : page.getContent()) {
            ids.add(facture.getIdFacture());
        }

        if (ids.isEmpty()) {
            return page;
        }

        List<Facture> result = new ArrayList<>();

        // for (Long id : ids) {
        // Facture verif = factureRepository.findById(id).orElse(null);
        // if (verif != null) {
        // result.add(verif);
        // }
        // }

        List<Facture> liste = factureRepository.findAll();
        for (Long id : ids) {
            for (Facture facture : liste) {
                if (facture.getIdFacture() == id) {
                    result.add(facture);
                }
            }
        }

        return new PageImpl<>(result, pageable, page.getTotalElements());
    }

    public Page<Facture> filtreFacture(FactureDTO facture, Pageable pageable) {
        Long idStatut = null;
        if (facture.getIdStatut() != null && !facture.getIdStatut().isEmpty()) {
            idStatut = Long.parseLong(facture.getIdStatut());
        }

        return factureRepository.filtrerFactures(facture.getNomClient(), idStatut, pageable);
    }

    //Numero de facture
    public String generateNumeroFacture(long id) {
        String prefix = "FAC-";
        String year = String.valueOf(LocalDate.now().getYear());
        return prefix + year + "-" + id;
    }
}
