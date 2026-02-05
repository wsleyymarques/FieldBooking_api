package dev.wesley.fieldbooking.middleware;

import dev.wesley.fieldbooking.service.ApiConfigService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClientKeyFilter extends OncePerRequestFilter {

    public static final String SECURITY_KEY_HEADER = "SecurityKey";

    private final ApiConfigService apiConfigService;

    public ClientKeyFilter(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(SECURITY_KEY_HEADER);
        apiConfigService.validateSecurityKey(key);

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.startsWith("/actuator")
                || path.startsWith("/error");
    }
}
