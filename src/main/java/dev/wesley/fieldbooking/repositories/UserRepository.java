// src/main/java/com/fieldbooking/repo/UserRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}

