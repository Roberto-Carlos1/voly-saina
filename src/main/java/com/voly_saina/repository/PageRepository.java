package com.voly_saina.repository;

import com.voly_saina.entity.Pages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Pages, Long> {
}
