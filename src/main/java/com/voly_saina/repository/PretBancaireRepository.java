package com.voly_saina.repository;

import com.voly_saina.entity.PretBancaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PretBancaireRepository extends JpaRepository<PretBancaire, Long> {
}
