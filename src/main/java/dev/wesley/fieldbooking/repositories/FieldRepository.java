// src/main/java/com/example/repository/FieldRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
    List<Field> findByStoreId(UUID storeId);
}

