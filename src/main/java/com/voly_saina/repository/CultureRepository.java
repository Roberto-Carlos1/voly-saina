package com.voly_saina.repository;

import com.voly_saina.entity.Culture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CultureRepository extends JpaRepository<Culture, Long> {
    Optional<Culture> findByNom(String nom);
    List<Culture> findByActifTrue();

    @Query("""
            SELECT DISTINCT c.localisationRecommandee
            FROM Culture c
            WHERE c.actif = true
            AND c.localisationRecommandee IS NOT NULL
            AND c.localisationRecommandee <> ''
            ORDER BY c.localisationRecommandee
            """)
    List<String> findLocalisationsDisponibles();

    @Query("""
            SELECT DISTINCT c.saisonRecommandee
            FROM Culture c
            WHERE c.actif = true
            AND c.saisonRecommandee IS NOT NULL
            AND c.saisonRecommandee <> ''
            ORDER BY c.saisonRecommandee
            """)
    List<String> findSaisonsDisponibles();

    @Query("""
            SELECT c
            FROM Culture c
            WHERE c.actif = true
            AND (
                LOWER(COALESCE(c.nom, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.description, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.saisonRecommandee, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.localisationRecommandee, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
            )
            AND LOWER(COALESCE(c.saisonRecommandee, '')) LIKE LOWER(CONCAT('%', :saison, '%'))
            AND LOWER(COALESCE(c.localisationRecommandee, '')) LIKE LOWER(CONCAT('%', :localisation, '%'))
            ORDER BY c.nom ASC
            """)
    Page<Culture> rechercherCulturesDisponiblesTrieesAsc(
            @Param("motCle") String motCle,
            @Param("saison") String saison,
            @Param("localisation") String localisation,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Culture c
            WHERE c.actif = true
            AND (
                LOWER(COALESCE(c.nom, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.description, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.saisonRecommandee, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
                OR LOWER(COALESCE(c.localisationRecommandee, '')) LIKE LOWER(CONCAT('%', :motCle, '%'))
            )
            AND LOWER(COALESCE(c.saisonRecommandee, '')) LIKE LOWER(CONCAT('%', :saison, '%'))
            AND LOWER(COALESCE(c.localisationRecommandee, '')) LIKE LOWER(CONCAT('%', :localisation, '%'))
            ORDER BY c.nom DESC
            """)
    Page<Culture> rechercherCulturesDisponiblesTrieesDesc(
            @Param("motCle") String motCle,
            @Param("saison") String saison,
            @Param("localisation") String localisation,
            Pageable pageable);
}
