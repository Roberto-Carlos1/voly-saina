package com.voly.saina.repository;

import com.voly.saina.entity.FicheCulture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FicheCultureRepository extends JpaRepository<FicheCulture, Long> {
    Optional<FicheCulture> findByCultureIdCulture(Long cultureId);
    List<FicheCulture> findByValideTrue();
}