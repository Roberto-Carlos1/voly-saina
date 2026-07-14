package com.voly_saina.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.StatutFacture;

@Repository
public interface StatutFactureRepository extends JpaRepository<StatutFacture, Long> {
    Optional<StatutFacture> findByCode(String code);
}
