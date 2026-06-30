package com.voly_saina.config;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository
                .findByEmailOrTelephone(identifiant, identifiant)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + identifiant));

        if (utilisateur.getStatutCompte() != null
                && !"actif".equalsIgnoreCase(utilisateur.getStatutCompte().getCode())) {
            throw new UsernameNotFoundException("Compte inactif ou bloqué : " + identifiant);
        }

        String roleCode = utilisateur.getRole() != null && utilisateur.getRole().getCode() != null
                ? utilisateur.getRole().getCode().toUpperCase()
                : "CLIENT";

        String role = "ROLE_" + roleCode;

        return User.builder()
            .username(utilisateur.getEmail())
            .password(utilisateur.getMotDePasse())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
