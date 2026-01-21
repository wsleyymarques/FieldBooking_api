package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByProfileId(UUID profileId);

    List<Address> findByStoreId(UUID storeId);

    boolean existsByProfileIdAndZipCode(UUID profileId, String zipCode);

    boolean existsByStoreIdAndZipCode(UUID storeId, String zipCode);
}
