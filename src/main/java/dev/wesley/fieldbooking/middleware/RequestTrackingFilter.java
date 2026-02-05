package dev.wesley.fieldbooking.middleware;

import dev.wesley.fieldbooking.dto.TrackingEventRequest;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.TrackingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestTrackingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String SESSION_ID_HEADER = "X-Session-Id";
    public static final String DEVICE_HEADER = "X-Device";
    public static final String ANONYMOUS_ID_HEADER = "X-Anonymous-Id";
    public static final String CLIENT_TYPE_HEADER = "X-Client-Type";

    private final TrackingService trackingService;
    private final UserRepository userRepository;

    public RequestTrackingFilter(TrackingService trackingService, UserRepository userRepository) {
        this.trackingService = trackingService;
        this.userRepository = userRepository;
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

        long start = System.currentTimeMillis();
        String requestId = resolveRequestId(request);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            TrackingEventRequest event = new TrackingEventRequest(
                    "http_request",
                    null,
                    request.getHeader(ANONYMOUS_ID_HEADER),
                    request.getHeader(CLIENT_TYPE_HEADER),
                    resolveUserId(),
                    request.getHeader(SESSION_ID_HEADER),
                    requestId,
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus(),
                    duration,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    request.getHeader(DEVICE_HEADER),
                    null
            );

            try {
                trackingService.trackBackend(event);
            } catch (RuntimeException ignored) {
                // tracking should not break the request flow
            }
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/error");
    }

    private String resolveRequestId(HttpServletRequest request) {
        String existing = request.getHeader(REQUEST_ID_HEADER);
        return (existing == null || existing.isBlank()) ? UUID.randomUUID().toString() : existing;
    }

    private UUID resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            Optional<UserAccount> account = userRepository.findByEmail(user.getUsername());
            return account.map(UserAccount::getId).orElse(null);
        }
        return null;
    }
}
