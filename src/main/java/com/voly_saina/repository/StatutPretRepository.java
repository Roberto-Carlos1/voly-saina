package com.voly_saina.repository;

import com.voly_saina.entity.StatutPret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutPretRepository extends JpaRepository<StatutPret, Long> {
}
