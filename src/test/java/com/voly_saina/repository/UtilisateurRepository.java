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
    
    List<Utilisateur> findByRoleCode(String code);
    
    List<Utilisateur> findByStatutCompteCode(String code);
    
    @Query("SELECT u FROM Utilisateur u WHERE u.role.code = :roleCode AND u.statutCompte.code = :statutCode")
    List<Utilisateur> findByRoleCodeAndStatutCompteCode(@Param("roleCode") String roleCode, @Param("statutCode") String statutCode);
    
    boolean existsByEmail(String email);
}
