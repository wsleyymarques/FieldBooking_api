package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.config.RabbitConfig;
import dev.wesley.fieldbooking.dto.BookingCommand;
import dev.wesley.fieldbooking.model.Booking;
import dev.wesley.fieldbooking.model.Enums.BookingStatus;
import dev.wesley.fieldbooking.model.Enums.FieldStatus;
import dev.wesley.fieldbooking.model.Field;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.BookingRepository;
import dev.wesley.fieldbooking.repositories.FieldRepository;
import dev.wesley.fieldbooking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final Duration HOLD_TTL = Duration.ofMinutes(15);

    private final RabbitTemplate rabbitTemplate;
    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    public UUID requestHold(BookingCommand command) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.BOOKING_EXCHANGE,
                RabbitConfig.BOOKING_COMMAND_ROUTING_KEY,
                command
        );
        return command.commandId();
    }

    @Transactional
    public void createHoldFromCommand(BookingCommand command) {
        if (command == null) return;

        if (bookingRepository.findByCommandId(command.commandId()).isPresent()) {
            return; // idempotente
        }

        Field field = fieldRepository.findById(command.fieldId())
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));

        if (field.getStatus() != FieldStatus.AVAILABLE) {
            throw new IllegalArgumentException("Field not available");
        }

        if (command.startAt() == null || command.endAt() == null ||
                !command.endAt().isAfter(command.startAt())) {
            throw new IllegalArgumentException("Invalid booking time range");
        }

        boolean overlap = bookingRepository.existsOverlap(
                command.fieldId(),
                command.startAt(),
                command.endAt(),
                BookingStatus.CANCELLED
        );
        if (overlap) {
            throw new IllegalArgumentException("Time slot not available");
        }

        UserAccount user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = new Booking();
        booking.setField(field);
        booking.setUser(user);
        booking.setStartAt(command.startAt());
        booking.setEndAt(command.endAt());
        booking.setStatus(BookingStatus.HOLD);
        booking.setCommandId(command.commandId());
        booking.setHoldExpiresAt(OffsetDateTime.now().plus(HOLD_TTL));
        booking.setPriceTotal(calculatePrice(field, command.startAt(), command.endAt()));

        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking getById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Transactional(readOnly = true)
    public Booking getByCommandId(UUID commandId) {
        return bookingRepository.findByCommandId(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Transactional(readOnly = true)
    public List<Booking> listByUser(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public Booking confirm(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Booking not owned by this user");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Booking cannot be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancel(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Booking not owned by this user");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    private BigDecimal calculatePrice(Field field, OffsetDateTime startAt, OffsetDateTime endAt) {
        if (field.getPricePerHour() == null) return null;
        long minutes = Duration.between(startAt, endAt).toMinutes();
        if (minutes <= 0) return BigDecimal.ZERO;
        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return field.getPricePerHour().multiply(hours);
    }
}
