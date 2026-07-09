package com.voly_saina.repository;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.PanierDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PanierDetailsRepository extends JpaRepository<PanierDetails, Long> {
    @Query("select sum(p.commande.montantTotal) as montantTotal from PanierDetails p where p.panier.idPanier = :id_panier")
    public BigDecimal sommeMontantCommande(@Param("id_panier") Long idPanier);

    @Query("select sum(p.reservationMachine.prixTotal) as montantTotal from PanierDetails p where p.panier.idPanier = :id_panier")
    public BigDecimal sommeMontantReservation(@Param("id_panier") Long idPanier);

    public List<PanierDetails> findByPanierIdPanier(Long idPanier);

    public List<PanierDetails> findByPanierIdPanierAndCommandeIdCommandeIsNotNull(Long idPanier);

    public List<PanierDetails> findByPanierIdPanierAndReservationMachineIdReservationIsNotNull(Long idPanier);

    public PanierDetails findByCommandeIdCommande(Long idCommande);

    public Optional<PanierDetails> findByReservationMachineIdReservation(Long reservationId);

}
