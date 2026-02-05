package dev.wesley.fieldbooking.config;

import dev.wesley.fieldbooking.middleware.ClientKeyFilter;
import dev.wesley.fieldbooking.service.ApiConfigService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class DynamicCorsConfiguration {

    private final ApiConfigService apiConfigService;

    public DynamicCorsConfiguration(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return this::buildCorsConfiguration;
    }

    private CorsConfiguration buildCorsConfiguration(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            return null;
        }

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Request-Id",
                "X-Session-Id",
                "X-Device",
                "X-Anonymous-Id",
                "X-Client-Type",
                "SecurityKey"
        ));

        String key = request.getHeader(ClientKeyFilter.SECURITY_KEY_HEADER);
        List<String> allowed = apiConfigService.resolveAllowedOrigins(key);

        if (allowed.isEmpty()) {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())
                    && apiConfigService.isOriginAllowedForAnyKey(origin)) {
                config.setAllowedOrigins(List.of(origin));
            } else {
                return null;
            }
        } else if (allowed.stream().anyMatch(origin::equalsIgnoreCase)) {
            config.setAllowedOrigins(List.of(origin));
        } else {
            return null;
        }

        return config;
    }
}
