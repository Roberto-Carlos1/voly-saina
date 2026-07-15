package com.voly_saina.repository;

import com.voly_saina.entity.OperationProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationProduitRepository extends JpaRepository<OperationProduit, Long> {
}
