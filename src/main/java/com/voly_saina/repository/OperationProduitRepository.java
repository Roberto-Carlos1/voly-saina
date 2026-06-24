package com.voly_saina.repository;

<<<<<<< Updated upstream
import com.voly_saina.entity.OperationProduit;
=======
import com.voly_saina.entity.OperationMachine;
import com.voly_saina.entity.OperationProduit;

import java.util.List;

>>>>>>> Stashed changes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationProduitRepository extends JpaRepository<OperationProduit, Long> {
<<<<<<< Updated upstream
=======
    public List<OperationProduit> findByIdFacture_IdFacture(Long id);

>>>>>>> Stashed changes
}
