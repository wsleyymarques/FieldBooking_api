package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, UUID> {
}