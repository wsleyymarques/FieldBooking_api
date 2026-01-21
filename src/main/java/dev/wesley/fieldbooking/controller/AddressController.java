package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.UpdateAddressRequest;
import dev.wesley.fieldbooking.model.Address;
import dev.wesley.fieldbooking.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PutMapping("/{addressId}")
    public Address update(@PathVariable UUID addressId,
                          @RequestBody @Valid UpdateAddressRequest req) {
        return addressService.update(addressId, req);
    }
}
