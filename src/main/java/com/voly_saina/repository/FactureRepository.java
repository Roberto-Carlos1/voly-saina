package com.voly_saina.repository;

import com.voly_saina.entity.Facture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    @Query("select f from Facture f " +
            "where (:nomClient is null or f.client.nom like %:nomClient%)" +
            " and (:idStatut is null or f.statutFacture.idStatutFacture = :idStatut)")
    Page<Facture> filtrerFactures(@Param("nomClient") String nom, @Param("idStatut") Long idStatut,
            Pageable pageable);
}
