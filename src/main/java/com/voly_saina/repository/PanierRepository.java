package com.voly_saina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.Panier;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {
    Panier findFirstByClientIdUtilisateurOrderByIdPanierDesc(Long idClient);

    @Query("SELECT p FROM Panier p WHERE p.client.idUtilisateur = :idClient AND p.actif = true ORDER BY p.idPanier DESC LIMIT 1")
    Panier findPanierActifPlusRecent(@Param("idClient") Long idClient);
}
