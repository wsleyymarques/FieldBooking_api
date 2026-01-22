// src/main/java/dev/wesley/fieldbooking/repositories/ProfileRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
