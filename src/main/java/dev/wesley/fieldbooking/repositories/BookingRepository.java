package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Booking;
import dev.wesley.fieldbooking.model.Enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByCommandId(UUID commandId);

    List<Booking> findByUserId(UUID userId);

    List<Booking> findByFieldId(UUID fieldId);

    @Query("""
            select (count(b) > 0)
            from Booking b
            where b.field.id = :fieldId
              and b.status <> :cancelled
              and b.startAt < :endAt
              and b.endAt > :startAt
            """)
    boolean existsOverlap(@Param("fieldId") UUID fieldId,
                          @Param("startAt") OffsetDateTime startAt,
                          @Param("endAt") OffsetDateTime endAt,
                          @Param("cancelled") BookingStatus cancelled);
}
