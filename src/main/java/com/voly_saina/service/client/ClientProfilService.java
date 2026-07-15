package com.voly_saina.service.client;

import com.voly_saina.dto.ClientProfilQuickStatsDTO;
import com.voly_saina.entity.Commande;
import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutCompte;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.CommandeRepository;
import com.voly_saina.repository.LigneCommandeRepository;
import com.voly_saina.repository.ProfilUtilisateurRepository;
import com.voly_saina.repository.ReservationMachineRepository;
import com.voly_saina.repository.StatutCompteRepository;
import com.voly_saina.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientProfilService {

    private static final List<String> STATUTS_RESERVATION_DEPENSE = List.of("validee", "en_cours", "terminee");
    private static final List<String> STATUTS_COMMANDE_DEPENSE = List.of("validee", "preparee", "en_livraison", "livree");

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilUtilisateurRepository profilUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationMachineRepository reservationMachineRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final StatutCompteRepository statutCompteRepository;

    public ClientProfilService(UtilisateurRepository utilisateurRepository,
                              ProfilUtilisateurRepository profilUtilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              ReservationMachineRepository reservationMachineRepository,
                              CommandeRepository commandeRepository,
                              LigneCommandeRepository ligneCommandeRepository,
                              StatutCompteRepository statutCompteRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.profilUtilisateurRepository = profilUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationMachineRepository = reservationMachineRepository;
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.statutCompteRepository = statutCompteRepository;
    }

    @Transactional(readOnly = true)
    public Utilisateur getUtilisateur(Long idUtilisateur) {
        return utilisateurRepository.findById(idUtilisateur)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    @Transactional(readOnly = true)
    public Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public ProfilUtilisateur getProfilUtilisateur(Long idUtilisateur) {
        return profilUtilisateurRepository.findByUtilisateurIdUtilisateur(idUtilisateur)
            .orElse(new ProfilUtilisateur());
    }

    @Transactional
    public Utilisateur updateUtilisateur(Long idUtilisateur, Utilisateur utilisateurUpdate) {
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        
        utilisateur.setNom(utilisateurUpdate.getNom());
        utilisateur.setTelephone(utilisateurUpdate.getTelephone());
        // Email ne peut pas être modifié pour des raisons de sécurité
        
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public ProfilUtilisateur updateProfilUtilisateur(Long idUtilisateur, ProfilUtilisateur profilUpdate) {
        ProfilUtilisateur profil = getProfilUtilisateur(idUtilisateur);
        
        if (profil.getIdProfil() == null) {
            // Créer un nouveau profil s'il n'existe pas
            Utilisateur utilisateur = getUtilisateur(idUtilisateur);
            profil.setUtilisateur(utilisateur);
        }
        
        profil.setGenre(profilUpdate.getGenre());
        profil.setAge(profilUpdate.getAge());
        profil.setCsp(profilUpdate.getCsp());
        profil.setLocalisation(profilUpdate.getLocalisation());
        
        return profilUtilisateurRepository.save(profil);
    }

    @Transactional
    public boolean updateMotDePasse(Long idUtilisateur, String oldPassword, String newPassword) {
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        
        if (!passwordEncoder.matches(oldPassword, utilisateur.getMotDePasse())) {
            return false;
        }
        
        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.save(utilisateur);
        return true;
    }

    @Transactional
    public void deleteCompte(Long idUtilisateur) {
        // Soft delete : désactiver le compte plutôt que supprimer
        Utilisateur utilisateur = getUtilisateur(idUtilisateur);
        StatutCompte statutInactif = statutCompteRepository.findByCode("inactif")
            .orElseThrow(() -> new IllegalStateException("Statut de compte 'inactif' introuvable"));
        utilisateur.setStatutCompte(statutInactif);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional(readOnly = true)
    public List<ReservationMachine> listerReservationsClient(Long idUtilisateur) {
        return reservationMachineRepository.findByClientIdUtilisateur(idUtilisateur).stream()
            .sorted(Comparator.comparing(ReservationMachine::getDateDebut,
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Commande> listerCommandesClient(Long idUtilisateur) {
        return commandeRepository.findByClientIdUtilisateur(idUtilisateur).stream()
            .sorted(Comparator.comparing(Commande::getDateCommande,
                    Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> compterArticlesParCommande(List<Commande> commandes) {
        return commandes.stream().collect(Collectors.toMap(
            Commande::getIdCommande,
            c -> ligneCommandeRepository.findByCommandeIdCommande(c.getIdCommande()).size()
        ));
    }

    public ClientProfilQuickStatsDTO calculerQuickStats(List<ReservationMachine> reservations,
                                                        List<Commande> commandes,
                                                        int nbFactures) {
        BigDecimal totalLocations = reservations.stream()
            .filter(r -> r.getStatutReservation() != null
                    && STATUTS_RESERVATION_DEPENSE.contains(r.getStatutReservation().getCode()))
            .map(ReservationMachine::getPrixTotal)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommandes = commandes.stream()
            .filter(c -> c.getStatutCommande() != null
                    && STATUTS_COMMANDE_DEPENSE.contains(c.getStatutCommande().getCode()))
            .map(Commande::getMontantTotal)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ClientProfilQuickStatsDTO(
            reservations.size(),
            commandes.size(),
            nbFactures,
            totalLocations.add(totalCommandes)
        );
    }
}