package com.voly_saina.repository;

import com.voly_saina.entity.Paiement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    @Query("select p from Paiement p where p.facture.idFacture = :idFacture order by p.datePaiement")
    public List<Paiement> findByIdFacture(@Param("idFacture") Long id);
}
