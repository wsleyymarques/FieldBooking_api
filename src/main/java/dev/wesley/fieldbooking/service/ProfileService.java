package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.UpdateProfileRequest;
import dev.wesley.fieldbooking.model.Profile;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.ProfileRepository;
import dev.wesley.fieldbooking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlayerService playerService;
    private final SupabaseStorageService storageService;

    @Transactional
    public Profile createForUser(UUID userId) {
        return createForUser(userId, null);
    }

    @Transactional
    public Profile createForUser(UUID userId, MultipartFile avatar) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Profile already exists for this user");
        }

        Profile profile = new Profile();
        profile.setUser(user);

        // avatar opcional
        if (avatar != null && !avatar.isEmpty()) {
            try {
                var upload = storageService.upload("avatars", userId + "/avatar.webp", avatar, true);
                profile.setAvatarUrl(upload.publicUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to upload avatar", e);
            }
        }

        Profile saved = profileRepository.save(profile);

        playerService.createForProfile(saved.getId());
        return saved;
    }

    @Transactional
    public Profile updateByUserId(UUID userId, UpdateProfileRequest req) {
        return updateByUserId(userId, req, null);
    }

    @Transactional
    public Profile updateByUserId(UUID userId, UpdateProfileRequest req, MultipartFile avatar) {
        if (req == null) throw new IllegalArgumentException("Request is required");

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        if (req.birthDate() != null) profile.setBirthDate(req.birthDate());
        if (req.bio() != null) profile.setBio(req.bio());

        if (avatar != null && !avatar.isEmpty()) {
            try {
                var upload = storageService.upload("avatars", userId + "/avatar.webp", avatar, true);
                profile.setAvatarUrl(upload.publicUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to upload avatar", e);
            }
        }

        return profileRepository.save(profile);
    }
}
