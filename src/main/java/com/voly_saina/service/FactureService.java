package com.voly_saina.service;

import com.voly_saina.entity.Facture;
import com.voly_saina.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //Numero de facture
    public String generateNumeroFacture(long id) {
        String prefix = "FAC-";
        String year = String.valueOf(LocalDate.now().getYear());
        return prefix + year + "-" + id;
    }
}
