package com.voly_saina.repository;

import com.voly_saina.entity.RemboursementPret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemboursementPretRepository extends JpaRepository<RemboursementPret, Long> {
}
