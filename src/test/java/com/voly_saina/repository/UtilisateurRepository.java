package com.voly_saina.repository;

import com.voly_saina.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    List<Utilisateur> findByRole(String role);
    
    List<Utilisateur> findByStatut(String statut);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND u.statut = :statut")
    List<Utilisateur> findByRoleAndStatut(@Param("role") String role, @Param("statut") String statut);
    
    boolean existsByEmail(String email);
}