package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.CreateFieldRequest;
import dev.wesley.fieldbooking.dto.UpdateFieldRequest;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Field;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.FieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;
    private final UserRepository userRepository;

    @PostMapping
    public Field create(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid CreateFieldRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        return fieldService.createForUser(user.getId(), req);
    }

    @GetMapping("/{fieldId}")
    public Field getById(@PathVariable UUID fieldId) {
        return fieldService.getById(fieldId);
    }

    @GetMapping("/store/{storeId}")
    public List<Field> listByStore(@PathVariable UUID storeId) {
        return fieldService.listByStore(storeId);
    }

    @PutMapping("/{fieldId}")
    public Field update(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID fieldId,
            @RequestBody @Valid UpdateFieldRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        return fieldService.update(user.getId(), fieldId, req);
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID fieldId
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        fieldService.delete(user.getId(), fieldId);
        return ResponseEntity.noContent().build();
    }
}
