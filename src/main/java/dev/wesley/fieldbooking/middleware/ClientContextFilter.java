package dev.wesley.fieldbooking.middleware;

import dev.wesley.fieldbooking.error.BadRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClientContextFilter extends OncePerRequestFilter {

    public static final String ANONYMOUS_ID_HEADER = RequestTrackingFilter.ANONYMOUS_ID_HEADER;
    public static final String CLIENT_TYPE_HEADER = RequestTrackingFilter.CLIENT_TYPE_HEADER;

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

        String clientType = request.getHeader(CLIENT_TYPE_HEADER);
        if (clientType == null || clientType.isBlank()) {
            throw new BadRequestException("X-Client-Type is required");
        }

        if (!isAuthenticated()) {
            String anonymousId = request.getHeader(ANONYMOUS_ID_HEADER);
            if (anonymousId == null || anonymousId.isBlank()) {
                throw new BadRequestException("X-Anonymous-Id is required for unauthenticated requests");
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || path.startsWith("/actuator")
                || path.startsWith("/error");
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }
}
