package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    List<Store> findByProfileId(UUID profileId);
}

