package com.voly_saina.repository;

import com.voly_saina.entity.FicheCulture;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FicheCultureRepository extends JpaRepository<FicheCulture, Long> {
    @EntityGraph(attributePaths = "culture")
    Optional<FicheCulture> findByCultureIdCulture(Long cultureId);
    List<FicheCulture> findByValideTrue();
}
