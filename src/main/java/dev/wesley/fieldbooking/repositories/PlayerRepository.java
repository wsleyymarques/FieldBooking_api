// src/main/java/com/fieldbooking/repo/PlayerRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {}

