package com.voly_saina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Encodeur compatible avec les nouveaux mots de passe BCrypt et les anciens mots de passe en clair.
     * Cela évite de bloquer les comptes déjà présents dans la base pendant les tests.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }

                if (encodedPassword.startsWith("$2a$")
                        || encodedPassword.startsWith("$2b$")
                        || encodedPassword.startsWith("$2y$")) {
                    return bcrypt.matches(rawPassword, encodedPassword);
                }

                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/connexion", "/login", "/inscription", "/signup",
                    "/access-denied", "/css/**", "/js/**", "/images/**", "/webjars/**"
                ).permitAll()
                .requestMatchers("/profil", "/profil/**").authenticated()
                .requestMatchers("/client/**", "/catalogue/**", "/panier/**").hasAnyRole("CLIENT", "GESTIONNAIRE", "RESPONSABLE", "EMPLOYE")
                .requestMatchers("/admin/**").hasRole("RESPONSABLE")
                .anyRequest().permitAll()
            )
            .formLogin(login -> login
                .loginPage("/connexion")
                .loginProcessingUrl("/connexion")
                .usernameParameter("identifiant")
                .passwordParameter("motDePasse")
                .defaultSuccessUrl("/profil", true)
                .failureUrl("/connexion?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/connexion?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
