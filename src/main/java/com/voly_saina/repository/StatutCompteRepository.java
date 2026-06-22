package com.voly_saina.repository;

import com.voly_saina.entity.StatutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutCompteRepository extends JpaRepository<StatutCompte, Long> {
}
