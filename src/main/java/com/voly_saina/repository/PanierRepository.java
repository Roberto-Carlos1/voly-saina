package com.voly_saina.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voly_saina.entity.Panier;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {
}
