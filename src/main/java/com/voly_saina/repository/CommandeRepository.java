package com.voly_saina.repository;

import com.voly_saina.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByClientIdUtilisateurAndDateCommandeBetweenAndStatutCommandeCodeIn(Long idClient, LocalDateTime debut, LocalDateTime fin, List<String> statuts);

    List<Commande> findByClientIdUtilisateur(Long idCLient);

}
