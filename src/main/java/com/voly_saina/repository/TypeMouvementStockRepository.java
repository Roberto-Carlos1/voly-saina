package com.voly_saina.repository;

import com.voly_saina.entity.TypeMouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeMouvementStockRepository extends JpaRepository<TypeMouvementStock, Long> {
}
