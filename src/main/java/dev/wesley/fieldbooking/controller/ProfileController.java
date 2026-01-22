package dev.wesley.fieldbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wesley.fieldbooking.dto.UpdateProfileRequest;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMe(
            @AuthenticationPrincipal User principal,
            @RequestPart("data") String data,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws Exception {

        String email = principal.getUsername();
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        UpdateProfileRequest req = objectMapper.readValue(data, UpdateProfileRequest.class);

        var updated = profileService.updateByUserId(user.getId(), req, avatar);
        return ResponseEntity.ok(updated);
    }
}
