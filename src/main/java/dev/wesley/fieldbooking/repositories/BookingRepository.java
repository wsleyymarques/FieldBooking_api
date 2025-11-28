// src/main/java/com/fieldbooking/repository/BookingRepository.java
package dev.wesley.fieldbooking.repositories;

import dev.wesley.fieldbooking.model.Booking;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /** Busca reservas que colidem no mesmo Field e ainda ativas (REQUESTED/CONFIRMED) */
    @Query("""
    select b from Booking b
     where b.field.id = :fieldId
       and b.status in (:activeStatuses)
       and ( :startAt < b.endAt and :endAt > b.startAt )
  """)
    List<Booking> findOverlaps(
            @Param("fieldId") UUID fieldId,
            @Param("startAt") OffsetDateTime startAt,
            @Param("endAt") OffsetDateTime endAt,
            @Param("activeStatuses") List<com.fieldbooking.domain.booking.BookingStatus> activeStatuses
    );
}

