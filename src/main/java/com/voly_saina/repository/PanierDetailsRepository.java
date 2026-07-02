package com.voly_saina.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.PanierDetails;

@Repository
public interface PanierDetailsRepository extends JpaRepository<PanierDetails, Long> {
    @Query("select sum(p.commande.montantTotal) as montantTotal from PanierDetails p where p.panier.idPanier = :id_panier")
    public BigDecimal sommeMontantReservation(@Param("id_panier") Long idPanier);

    @Query("select sum(p.reservationMachine.prixTotal) as montantTotal from PanierDetails p where p.panier.idPanier = :id_panier")
    public BigDecimal sommeMontantCommande(@Param("id_panier") Long idPanier);

    @Query("SELECT SUM(r.prixTotal) FROM ReservationMachine r " +
        "JOIN PanierDetails pd ON pd.reservationMachine = r " +
        "WHERE pd.panier.idPanier = :panierId AND r.statutReservation.code = 'en_attente'")
    BigDecimal sumReservationsByPanier(@Param("panierId") Long panierId);

    List<PanierDetails> findByPanierIdPanier(Long panierId);
    
    Optional<PanierDetails> findByReservationMachineIdReservation(Long reservationId);
}
