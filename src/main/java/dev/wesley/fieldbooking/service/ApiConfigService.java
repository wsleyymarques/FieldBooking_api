package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.error.ForbiddenException;
import dev.wesley.fieldbooking.model.ApiConfig;
import dev.wesley.fieldbooking.repositories.ApiConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiConfigService {

    private final ApiConfigRepository repository;

    public void validateSecurityKey(String securityKey) {
        if (securityKey == null || securityKey.isBlank()) {
            throw new ForbiddenException("SecurityKey is required");
        }
        if (!repository.existsBySecurityKeyAndActiveTrue(securityKey)) {
            throw new ForbiddenException("Invalid SecurityKey");
        }
    }

    public List<String> resolveAllowedOrigins(String securityKey) {
        if (securityKey == null || securityKey.isBlank()) return Collections.emptyList();
        Optional<ApiConfig> config = repository.findBySecurityKeyAndActiveTrue(securityKey);
        return config.map(c -> splitOrigins(c.getAllowedOrigins())).orElseGet(Collections::emptyList);
    }

    public boolean isOriginAllowedForAnyKey(String origin) {
        if (origin == null || origin.isBlank()) return false;
        return repository.findByActiveTrue().stream()
                .map(ApiConfig::getAllowedOrigins)
                .map(this::splitOrigins)
                .flatMap(List::stream)
                .anyMatch(origin::equalsIgnoreCase);
    }

    private List<String> splitOrigins(String allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.isBlank()) return Collections.emptyList();
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
