package com.voly_saina.repository;

import com.voly_saina.entity.view.FactureFille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureFilleRepository extends JpaRepository<FactureFille, String> {
    List<FactureFille> findByIdFacture(Integer idFacture);
}
