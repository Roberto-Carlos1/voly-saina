package com.voly_saina.repository;

import com.voly_saina.entity.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutCompteRepository extends JpaRepository<StatutCompte, Long> {

    Optional<StatutCompte> findByCode(String code);
}
