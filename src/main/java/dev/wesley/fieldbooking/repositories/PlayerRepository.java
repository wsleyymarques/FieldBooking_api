// src/main/java/dev/wesley/fieldbooking/repositories/PlayerRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByProfileId(UUID profileId);

    boolean existsByProfileId(UUID profileId);
}
