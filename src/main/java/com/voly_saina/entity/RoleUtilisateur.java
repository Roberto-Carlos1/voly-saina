package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "role_utilisateur", schema = "voly_saina")
public class RoleUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    @EqualsAndHashCode.Include
    private Long idRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleUtilisateur that = (RoleUtilisateur) o;
        return idRole != null && Objects.equals(idRole, that.idRole);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "libelle", length = 80)
    private String libelle;
}
