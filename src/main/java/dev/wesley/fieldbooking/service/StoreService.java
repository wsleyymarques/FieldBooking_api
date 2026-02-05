package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.CreateStoreRequest;
import dev.wesley.fieldbooking.dto.UpdateStoreRequest;
import dev.wesley.fieldbooking.error.BadRequestException;
import dev.wesley.fieldbooking.error.ForbiddenException;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Profile;
import dev.wesley.fieldbooking.model.Store;
import dev.wesley.fieldbooking.repositories.ProfileRepository;
import dev.wesley.fieldbooking.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public Store createForUser(UUID userId, CreateStoreRequest req) {
        if (req == null) throw new BadRequestException("Request is required");

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Store store = new Store();
        store.setProfile(profile);
        store.setName(req.name());
        store.setCnpj(req.cnpj());
        store.setContactEmail(req.contactEmail());
        store.setContactPhone(req.contactPhone());
        store.setOpeningHours(req.openingHours());

        if (req.active() != null) store.setActive(req.active());
        if (req.amenities() != null) store.setAmenities(new HashSet<>(req.amenities()));

        return storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    public List<Store> listByUser(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        return storeRepository.findByProfileId(profile.getId());
    }

    @Transactional(readOnly = true)
    public Store getById(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found"));
    }

    @Transactional
    public Store update(UUID storeId, UUID userId, UpdateStoreRequest req) {
        if (req == null) throw new BadRequestException("Request is required");

        Store store = requireOwnedStore(storeId, userId);

        if (req.name() != null) store.setName(req.name());
        if (req.cnpj() != null) store.setCnpj(req.cnpj());
        if (req.contactEmail() != null) store.setContactEmail(req.contactEmail());
        if (req.contactPhone() != null) store.setContactPhone(req.contactPhone());
        if (req.openingHours() != null) store.setOpeningHours(req.openingHours());
        if (req.active() != null) store.setActive(req.active());

        if (req.amenities() != null) {
            store.setAmenities(new HashSet<>(req.amenities()));
        }

        return storeRepository.save(store);
    }

    @Transactional
    public void delete(UUID storeId, UUID userId) {
        Store store = requireOwnedStore(storeId, userId);
        storeRepository.delete(store);
    }

    private Store requireOwnedStore(UUID storeId, UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found"));

        if (store.getProfile() == null || !store.getProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("Store not owned by this profile");
        }

        return store;
    }
}
