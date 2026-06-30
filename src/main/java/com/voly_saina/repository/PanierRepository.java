package com.voly_saina.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.EtatMachine;

@Repository
public interface PanierRepository extends JpaRepository<EtatMachine, Long> {
    @Query("select sum(p.commande.montantTotal) as montantTotal from Panier p where p.client.idUtilisateur = :id_client")
    public BigDecimal sommeMontantReservation(@Param("id_client") Long idClient);

    @Query("select sum(p.reservationMachine.prixTotal) as montantTotal from Panier p where p.client.idUtilisateur = :id_client")
    public BigDecimal sommeMontantCommande(@Param("id_client") Long idClient);

}
