package com.voly_saina.repository;

import com.voly_saina.entity.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleUtilisateurRepository extends JpaRepository<RoleUtilisateur, Long> {

    Optional<RoleUtilisateur> findByCode(String code);
}
