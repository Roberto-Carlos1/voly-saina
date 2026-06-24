package com.voly_saina.service;

<<<<<<< Updated upstream
=======
import com.voly_saina.entity.OperationMachine;
>>>>>>> Stashed changes
import com.voly_saina.entity.OperationProduit;
import com.voly_saina.repository.OperationProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationProduitService {

    @Autowired
    private OperationProduitRepository operationProduitRepository;

    public List<OperationProduit> findAll() {
        return operationProduitRepository.findAll();
    }

    public Optional<OperationProduit> findById(Long id) {
        return operationProduitRepository.findById(id);
    }

    public void save(OperationProduit OperationProduit) {
        operationProduitRepository.save(OperationProduit);
    }

<<<<<<< Updated upstream
=======
    public List<OperationProduit> findByIdFacture(Long id) {
        return operationProduitRepository.findByIdFacture_IdFacture(id);
    }

>>>>>>> Stashed changes
    public boolean existsById(Long id) {
        return operationProduitRepository.existsById(id);
    }

    public void deleteById(Long id) {
        operationProduitRepository.deleteById(id);
    }
}
