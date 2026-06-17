package com.voly.saina.repository;

import com.voly.saina.entity.ProfilUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilUtilisateurRepository extends JpaRepository<ProfilUtilisateur, Long> {
    Optional<ProfilUtilisateur> findByUtilisateurIdUtilisateur(Long utilisateurId);
}