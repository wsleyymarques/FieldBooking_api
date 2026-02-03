package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.CreateMyAddressRequest;
import dev.wesley.fieldbooking.dto.CreateAddressRequest;
import dev.wesley.fieldbooking.dto.UpdateAddressRequest;
import dev.wesley.fieldbooking.model.Address;
import dev.wesley.fieldbooking.model.UserAccount;
import dev.wesley.fieldbooking.repositories.UserRepository;
import dev.wesley.fieldbooking.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;

    @PostMapping("/me")
    public Address createMyAddress(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid CreateMyAddressRequest req
    ) {
        String email = principal.getUsername();

        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        return addressService.createForUserId(user.getId(), req);
    }

    @PostMapping
    public Address create(
            @AuthenticationPrincipal User principal,
            @RequestBody @Valid CreateAddressRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        return addressService.createForUser(user.getId(), req);
    }

    @PutMapping("/{addressId}")
    public Address update(
            @AuthenticationPrincipal User principal,
            @PathVariable UUID addressId,
            @RequestBody @Valid UpdateAddressRequest req
    ) {
        UserAccount user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        return addressService.updateOwned(user.getId(), addressId, req);
    }
}
