package com.voly_saina.repository;

import com.voly_saina.entity.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutCommandeRepository extends JpaRepository<StatutCommande, Long> {
}
