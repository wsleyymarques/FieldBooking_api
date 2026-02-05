package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.BookingCommand;
import dev.wesley.fieldbooking.dto.BookingCommandResponse;
import dev.wesley.fieldbooking.dto.BookingHoldRequest;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Booking;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping("/hold")
    public ResponseEntity<BookingCommandResponse> createHold(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid BookingHoldRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        UUID commandId = UUID.randomUUID();
        BookingCommand command = new BookingCommand(
                commandId,
                req.fieldId(),
                user.getId(),
                req.startAt(),
                req.endAt()
        );

        bookingService.requestHold(command);
        return ResponseEntity.accepted().body(new BookingCommandResponse(commandId));
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable UUID bookingId) {
        return bookingService.getById(bookingId);
    }

    @GetMapping("/command/{commandId}")
    public Booking getByCommand(@PathVariable UUID commandId) {
        return bookingService.getByCommandId(commandId);
    }

    @GetMapping("/me")
    public List<Booking> listMine(@AuthenticationPrincipal User principal) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        return bookingService.listByUser(user.getId());
    }

    @PostMapping("/{bookingId}/confirm")
    public Booking confirm(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID bookingId
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        return bookingService.confirm(user.getId(), bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public Booking cancel(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID bookingId
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));
        return bookingService.cancel(user.getId(), bookingId);
    }
}
