package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.UpdatePlayerRequest;
import dev.wesley.fieldbooking.error.BadRequestException;
import dev.wesley.fieldbooking.error.ConflictException;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Player;
import dev.wesley.fieldbooking.model.Profile;
import dev.wesley.fieldbooking.model.Enums.SkillLevel;
import dev.wesley.fieldbooking.repositories.PlayerRepository;
import dev.wesley.fieldbooking.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ProfileRepository profileRepository;

    /**
     * Cria o Player (1:1) para um Profile existente.
     * Id do player = id do profile (MapsId).
     */
    @Transactional
    public Player createForProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        // se já existe, retorna (ou lança erro — você escolhe)
        if (playerRepository.existsById(profileId)) {
            return playerRepository.findById(profileId)
                    .orElseThrow(() -> new ConflictException("Player not found but existsById returned true"));
        }

        Player player = new Player();
        player.setProfile(profile);
        player.setSkillLevel(SkillLevel.BEGINNER);
        // goals/assists/matchesPlayed já ficam 0 via defaults do entity

        return playerRepository.save(player);
    }

    /**
     * Atualiza dados do Player baseado no userId (via profile).
     * Isso é bom pra manter segurança: user só mexe no próprio player.
     */
    @Transactional
    public Player updateByUserId(UUID userId, UpdatePlayerRequest req) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Player player = playerRepository.findById(profile.getId())
                .orElseThrow(() -> new NotFoundException("Player not found"));

        applyUpdate(player, req);
        return playerRepository.save(player);
    }

    private void applyUpdate(Player player, UpdatePlayerRequest req) {
        if (req == null) throw new BadRequestException("Request is required");

        // Aqui você decide se é PUT (seta null) ou PATCH (só altera se != null)
        if (req.skillLevel() != null) player.setSkillLevel(req.skillLevel());
        if (req.preferredPosition() != null) player.setPreferredPosition(req.preferredPosition());
        if (req.rating() != null) player.setRating(req.rating());

        if (req.goals() != null) player.setGoals(req.goals());
        if (req.assists() != null) player.setAssists(req.assists());
        if (req.matchesPlayed() != null) player.setMatchesPlayed(req.matchesPlayed());
    }
}
