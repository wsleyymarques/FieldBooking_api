package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.CreateFieldRequest;
import dev.wesley.fieldbooking.dto.UpdateFieldRequest;
import dev.wesley.fieldbooking.error.BadRequestException;
import dev.wesley.fieldbooking.error.ForbiddenException;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Field;
import dev.wesley.fieldbooking.model.Profile;
import dev.wesley.fieldbooking.model.Store;
import dev.wesley.fieldbooking.repositories.FieldRepository;
import dev.wesley.fieldbooking.repositories.ProfileRepository;
import dev.wesley.fieldbooking.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final StoreRepository storeRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public Field createForUser(UUID userId, CreateFieldRequest req) {
        if (req == null) throw new BadRequestException("Request is required");

        Profile profile = requireProfileByUserId(userId);
        Store store = storeRepository.findById(req.storeId())
                .orElseThrow(() -> new NotFoundException("Store not found"));

        if (store.getProfile() == null || !store.getProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("Store not owned by this profile");
        }

        Field field = new Field();
        field.setStore(store);
        field.setName(req.name());
        field.setType(req.type());
        field.setSurface(req.surface());
        field.setPricePerHour(req.pricePerHour());
        field.setSizeLabel(req.sizeLabel());
        field.setIndoor(Boolean.TRUE.equals(req.indoor()));
        field.setLighting(Boolean.TRUE.equals(req.lighting()));
        field.setLockerRoom(Boolean.TRUE.equals(req.lockerRoom()));

        return fieldRepository.save(field);
    }

    @Transactional(readOnly = true)
    public Field getById(UUID fieldId) {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NotFoundException("Field not found"));
    }

    @Transactional(readOnly = true)
    public List<Field> listByStore(UUID storeId) {
        return fieldRepository.findByStoreId(storeId);
    }

    @Transactional
    public Field update(UUID userId, UUID fieldId, UpdateFieldRequest req) {
        if (req == null) throw new BadRequestException("Request is required");

        Field field = requireOwnedField(userId, fieldId);

        if (req.name() != null) field.setName(req.name());
        if (req.type() != null) field.setType(req.type());
        if (req.surface() != null) field.setSurface(req.surface());
        if (req.pricePerHour() != null) field.setPricePerHour(req.pricePerHour());
        if (req.sizeLabel() != null) field.setSizeLabel(req.sizeLabel());
        if (req.indoor() != null) field.setIndoor(req.indoor());
        if (req.lighting() != null) field.setLighting(req.lighting());
        if (req.lockerRoom() != null) field.setLockerRoom(req.lockerRoom());
        if (req.status() != null) field.setStatus(req.status());

        return fieldRepository.save(field);
    }

    @Transactional
    public void delete(UUID userId, UUID fieldId) {
        Field field = requireOwnedField(userId, fieldId);
        fieldRepository.delete(field);
    }

    private Field requireOwnedField(UUID userId, UUID fieldId) {
        Profile profile = requireProfileByUserId(userId);
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new NotFoundException("Field not found"));

        Store store = field.getStore();
        if (store.getProfile() == null || !store.getProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("Store not owned by this profile");
        }
        return field;
    }

    private Profile requireProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }
}
