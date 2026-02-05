package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.AuthRequest;
import dev.wesley.fieldbooking.dto.AuthResponse;
import dev.wesley.fieldbooking.dto.RegisterRequest;
import dev.wesley.fieldbooking.dto.UpdateAccountRequest;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(
            @AuthenticationPrincipal User main, @RequestBody UpdateAccountRequest request
    ) {
        String email = main.getUsername();

        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        authService.updateAccount(user, request);
        return ResponseEntity.noContent().build();
    }
}
