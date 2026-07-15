package com.voly_saina.repository;

import com.voly_saina.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    @Query("SELECT lc FROM LigneCommande lc WHERE lc.commande.client.idUtilisateur = :idClient " +
           "AND lc.commande.dateCommande BETWEEN :debut AND :fin " +
           "AND lc.commande.statutCommande.code IN :statuts")
    List<LigneCommande> findLignesCommandeClient(
        @Param("idClient") Long idClient,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin,
        @Param("statuts") List<String> statuts);

    List<LigneCommande> findByCommandeIdCommande(Long idCommande);
}
