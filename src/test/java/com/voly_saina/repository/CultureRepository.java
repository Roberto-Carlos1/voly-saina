package com.voly.saina.repository;

import com.voly.saina.entity.Culture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CultureRepository extends JpaRepository<Culture, Long> {
    Optional<Culture> findByNom(String nom);
    List<Culture> findByActifTrue();
}