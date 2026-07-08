package com.voly_saina.service;

import com.voly_saina.entity.Facture;
import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.Paiement;
import com.voly_saina.entity.StatutFacture;
import com.voly_saina.entity.dto.PaiementDTO;
import com.voly_saina.exception.PaiementException;
import com.voly_saina.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    private final FactureService factureService;
    private final ModePaiementService modePaiementService;
    private final StatutFactureService statutFactureService;

    public PaiementService(FactureService factureService, ModePaiementService modePaiementService,
            StatutFactureService statutFactureService) {
        this.factureService = factureService;
        this.modePaiementService = modePaiementService;
        this.statutFactureService = statutFactureService;
    }

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public Optional<Paiement> findById(Long id) {
        return paiementRepository.findById(id);
    }

    public Paiement save(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public boolean existsById(Long id) {
        return paiementRepository.existsById(id);
    }

    public void deleteById(Long id) {
        paiementRepository.deleteById(id);
    }

    public List<Paiement> findByFacture(Long id) {
        return paiementRepository.findByIdFacture(id);
    }

    public void payerFacture(PaiementDTO paiementDTO) throws Exception {
        Long id = Long.parseLong(paiementDTO.getIdFacture());
        double montant;
        try {
            montant = Double.parseDouble(paiementDTO.getMontant());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le montant ne doit contenir que des chiffres");
        }
        
        BigDecimal m = BigDecimal.valueOf(montant);
        Facture f = factureService.findById(id);

        if (montant < 0) {
            throw new PaiementException(PaiementException.MONTANT_NEGATIF);
        }


        // 3. Validation de la date
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime date = LocalDateTime.parse(paiementDTO.getDate(), format);

        if (f.getDateFacture().isAfter(date)) {
            throw new PaiementException(PaiementException.DATE_ANTERIEUR);
        }

        String mode = paiementDTO.getModePaiement();
        ModePaiement modePaiement = modePaiementService.findById(Long.parseLong(mode));

        Paiement p = new Paiement();
        p.setFacture(f);
        p.setMontant(m);
        p.setDatePaiement(date);
        p.setModePaiement(modePaiement);

        this.save(p);

        f.setMontantPaye(f.getMontantPaye().add(m));
        if (f.getMontantPaye() == f.getMontantTotal()) {
            StatutFacture statut = statutFactureService.findById(2L).orElse(null);
            f.setStatutFacture(statut);
        }
        factureService.save(f);
    }

}
