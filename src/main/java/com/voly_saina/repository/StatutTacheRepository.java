package com.voly_saina.repository;

import com.voly_saina.entity.StatutTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutTacheRepository extends JpaRepository<StatutTache, Long> {
}
