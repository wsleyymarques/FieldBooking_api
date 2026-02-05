package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.CreateAddressRequest;
import dev.wesley.fieldbooking.dto.CreateMyAddressRequest;
import dev.wesley.fieldbooking.dto.UpdateAddressRequest;
import dev.wesley.fieldbooking.error.BadRequestException;
import dev.wesley.fieldbooking.error.ForbiddenException;
import dev.wesley.fieldbooking.error.NotFoundException;
import dev.wesley.fieldbooking.model.Address;
import dev.wesley.fieldbooking.model.Profile;
import dev.wesley.fieldbooking.model.Store;
import dev.wesley.fieldbooking.repositories.AddressRepository;
import dev.wesley.fieldbooking.repositories.ProfileRepository;
import dev.wesley.fieldbooking.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final ProfileRepository profileRepository;
    private final StoreRepository storeRepository;
    private final CepService cepService;

    @Transactional
    public Address createForUser(UUID userId, CreateAddressRequest req) {

        // 1️⃣ Regra: profile OU store (XOR)
        if ((req.profileId() == null && req.storeId() == null) ||
                (req.profileId() != null && req.storeId() != null)) {
            throw new BadRequestException(
                    "You must provide either profileId or storeId"
            );
        }

        Address address = new Address();

        // 2️⃣ Preenche campos manuais
        address.setCountry(
                req.country() != null ? req.country() : "BR"
        );
        address.setNumber(req.number());
        address.setComplement(req.complement());
        address.setLatitude(req.latitude());
        address.setLongitude(req.longitude());

        // 3️⃣ ViaCEP (se tiver zipCode)
        if (req.zipCode() != null && !req.zipCode().isBlank()) {
            var cep = normalizeZipCode(req.zipCode());
            var r = cepService.lookup(cep);

            if (r == null || Boolean.TRUE.equals(r.error())) {
                throw new BadRequestException("Invalid ZIP code");
            }

            address.setZipCode(cep);
            address.setStreet(r.street());
            address.setNeighborhood(r.neighborhood());
            address.setCity(r.city());
            address.setState(r.state());
        } else {
            // 4️⃣ Endereço parcial permitido
            address.setZipCode(null);
            address.setStreet(req.street());
            address.setNeighborhood(req.neighborhood());
            address.setCity(req.city());
            address.setState(req.state());
        }

        // 5️⃣ Vincula o dono
        Profile ownerProfile = requireProfileByUserId(userId);
        if (req.profileId() != null) {
            if (!ownerProfile.getId().equals(req.profileId())) {
                throw new ForbiddenException("Profile not owned by this user");
            }
            address.setProfile(ownerProfile);
            address.setStore(null);
        } else {
            Store store = storeRepository.findById(req.storeId())
                    .orElseThrow(() -> new NotFoundException("Store not found"));
            if (store.getProfile() == null || !store.getProfile().getId().equals(ownerProfile.getId())) {
                throw new ForbiddenException("Store not owned by this profile");
            }
            address.setStore(store);
            address.setProfile(null);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateOwned(UUID userId, UUID addressId, UpdateAddressRequest req) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        Profile ownerProfile = requireProfileByUserId(userId);
        if (address.getStore() != null) {
            Store store = address.getStore();
            if (store.getProfile() == null || !store.getProfile().getId().equals(ownerProfile.getId())) {
                throw new ForbiddenException("Store not owned by this profile");
            }
        }
        if (address.getProfile() != null) {
            if (!address.getProfile().getId().equals(ownerProfile.getId())) {
                throw new ForbiddenException("Profile not owned by this user");
            }
        }

        // ✅ Não deixa mudar dono no update (segurança/consistência)
        // (store/profile ficam como estão)

        // country
        if (req.country() != null) address.setCountry(req.country());

        // number / complement / lat / lng
        if (req.number() != null) address.setNumber(req.number());
        if (req.complement() != null) address.setComplement(req.complement());
        if (req.latitude() != null) address.setLatitude(req.latitude());
        if (req.longitude() != null) address.setLongitude(req.longitude());

        // Se veio zipCode, tenta ViaCEP e atualiza os campos principais
        if (req.zipCode() != null) {
            String normalized = normalizeZipCode(req.zipCode());

            if (!normalized.isBlank()) {
                var r = cepService.lookup(normalized);
                if (Boolean.TRUE.equals(r.error())) {
                    throw new BadRequestException("Invalid ZIP code");
                }

                address.setZipCode(normalized);
                address.setStreet(r.street());
                address.setNeighborhood(r.neighborhood());
                address.setCity(r.city());
                address.setState(r.state());

                // Se quiser forçar BR quando usa CEP:
                if (address.getCountry() == null) address.setCountry("BR");
            } else {
                // usuário limpou o CEP
                address.setZipCode(null);
            }
        }

        // Se NÃO veio zipCode (ou veio null), permite editar manualmente street/neighborhood/city/state
        // (Se você quiser bloquear edição manual quando existe CEP, você pode mudar isso.)
        if (req.street() != null) address.setStreet(req.street());
        if (req.neighborhood() != null) address.setNeighborhood(req.neighborhood());
        if (req.city() != null) address.setCity(req.city());
        if (req.state() != null) address.setState(req.state());

        return addressRepository.save(address);
    }

    private String normalizeZipCode(String zip) {
        return zip.replaceAll("\\D", "");
    }



    @Transactional
    public Address createForUserId(UUID userId, CreateMyAddressRequest req) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        CreateAddressRequest internal = new CreateAddressRequest(
                req.country(),
                req.zipCode(),
                req.street(),
                req.number(),
                req.complement(),
                req.neighborhood(),
                req.city(),
                req.state(),
                req.latitude(),
                req.longitude(),
                profile.getId(),  // profileId
                null              // storeId
        );

        return createForUser(userId, internal);
    }

    private Profile requireProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }


}

