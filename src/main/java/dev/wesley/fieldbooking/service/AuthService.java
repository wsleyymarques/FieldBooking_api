package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.AuthRequest;
import dev.wesley.fieldbooking.dto.AuthResponse;
import dev.wesley.fieldbooking.dto.RegisterRequest;
import dev.wesley.fieldbooking.dto.UpdateAccountRequest;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static String[] splitName(String fullName) {
        if (fullName == null) return new String[]{"", ""};

        String normalized = fullName.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) return new String[]{"", ""};

        String[] parts = normalized.split(" ", 2);
        String first = parts[0];
        String last = (parts.length > 1) ? parts[1] : "";

        return new String[]{first, last};
    }


    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String jwt = jwtService.generateToken((org.springframework.security.core.userdetails.User) authentication.getPrincipal());
        return new AuthResponse(jwt);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ja cadastrado");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        String firstName = null;
        String lastName = null;

        if (request.firstName() != null && !request.firstName().isBlank()) {
            firstName = request.firstName().trim();
            lastName = (request.lastName() != null && !request.lastName().isBlank())
                    ? request.lastName().trim()
                    : null;
        }
        else if (request.name() != null && !request.name().isBlank()) {
            String[] nameParts = splitName(request.name());
            firstName = nameParts[0];
            lastName = nameParts[1].isBlank() ? null : nameParts[1];
        }
        else {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        UserAccount user = UserAccount.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.email())
                .passwordHash(passwordHash)
                .phone(request.phone())
                .active(true)
                .build();

        userRepository.save(user);

        var springUser = new User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.List.of(() -> "ROLE_USER")
        );

        String jwt = jwtService.generateToken(springUser);
        return new AuthResponse(jwt);
    }


    public void updateAccount(UserAccount user ,UpdateAccountRequest request) {

        if (request.email() != null && !Objects.equals(request.email(), user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("Email ja cadastrado");
            }
            user.setEmail(request.email());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            if (request.currentPassword() == null || request.currentPassword().isBlank()) {
                throw new IllegalArgumentException("Senha atual obrigatoria para alterar a senha");
            }
            boolean matches = passwordEncoder.matches(
                    request.currentPassword(),
                    user.getPasswordHash()
            );
            if (!matches) {
                throw new IllegalArgumentException("Senha atual incorreta");
            }

            String newHash = passwordEncoder.encode(request.newPassword());
            user.setPasswordHash(newHash);
        }

        userRepository.save(user);
    }
}