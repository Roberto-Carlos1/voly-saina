package com.voly_saina.repository;

import com.voly_saina.entity.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutFactureRepository extends JpaRepository<StatutFacture, Long> {
}
