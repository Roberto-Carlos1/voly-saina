package com.voly_saina.repository;

import com.voly_saina.entity.Facture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByClientIdUtilisateurOrderByDateFactureDesc(Long idClient);
    List<Facture> findByClientIdUtilisateurAndStatutFactureCodeOrderByDateFactureDesc(Long idClient, String statutCode);
    Optional<Facture> findByIdFactureAndClientIdUtilisateur(Long idFacture, Long idClient);
    List<Facture> findByClientIdUtilisateurAndDateFactureBetweenAndStatutFactureCodeIn(Long idClient, java.time.LocalDateTime debut, java.time.LocalDateTime fin, List<String> statuts);
    @Query("select f from Facture f " +
            "where (:nomClient is null or f.client.nom like %:nomClient%)" +
            " and (:idStatut is null or f.statutFacture.idStatutFacture = :idStatut)")
    Page<Facture> filtrerFactures(@Param("nomClient") String nom, @Param("idStatut") Long idStatut,
            Pageable pageable);
}
