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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        
        if (utilisateur == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }

        String role = "ROLE_" + utilisateur.getRole().getCode().toUpperCase();
        
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