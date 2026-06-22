package com.voly_saina.repository;

import com.voly_saina.entity.TacheEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TacheEmployeRepository extends JpaRepository<TacheEmploye, Long> {
}
