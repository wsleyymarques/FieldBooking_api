// src/main/java/com/fieldbooking/domain/booking/Booking.java
package dev.wesley.fieldbooking.model;

import dev.wesley.fieldbooking.model.Enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_booking_field", columnList = "field_id"),
                @Index(name = "idx_booking_user", columnList = "user_id"),
                @Index(name = "idx_booking_period", columnList = "startAt,endAt"),
                @Index(name = "uk_booking_command", columnList = "command_id", unique = true)
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id @GeneratedValue @UuidGenerator
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_field"))
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_booking_user"))
    private UserAccount user;

    @NotNull @Future
    @Column(nullable = false)
    private OffsetDateTime startAt;

    @NotNull @Future
    @Column(nullable = false)
    private OffsetDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.HOLD;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceTotal;

    /** id do comando “reserve” que originou esta booking (para idempotência) */
    @Column(name = "command_id", unique = true)
    private UUID commandId;

    /** quando o HOLD expira; um job varre e cancela holds vencidos */
    @Column(name = "hold_expires_at")
    private OffsetDateTime holdExpiresAt;

    @Version
    private Long version;
}
