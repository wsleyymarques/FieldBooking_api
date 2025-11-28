// src/main/java/com/fieldbooking/repo/ManagerRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {}

