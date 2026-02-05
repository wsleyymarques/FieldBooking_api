package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.CreateStoreRequest;
import dev.wesley.fieldbooking.dto.UpdateStoreRequest;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Store;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final UserRepository userRepository;

    @PostMapping("/me")
    public Store createMyStore(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid CreateStoreRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        return storeService.createForUser(user.getId(), req);
    }

    @GetMapping("/me")
    public List<Store> listMyStores(@AuthenticationPrincipal User principal) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        return storeService.listByUser(user.getId());
    }

    @GetMapping("/{storeId}")
    public Store getById(@PathVariable UUID storeId) {
        return storeService.getById(storeId);
    }

    @PutMapping("/{storeId}")
    public Store update(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID storeId,
            @RequestBody @Valid UpdateStoreRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        return storeService.update(storeId, user.getId(), req);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID storeId
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado"));

        storeService.delete(storeId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
