package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.ApiConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiConfigRepository extends JpaRepository<ApiConfig, UUID> {
    boolean existsBySecurityKeyAndActiveTrue(String securityKey);
    java.util.Optional<ApiConfig> findBySecurityKeyAndActiveTrue(String securityKey);
    java.util.List<ApiConfig> findByActiveTrue();
}
