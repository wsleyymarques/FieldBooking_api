// src/main/java/dev/wesley/fieldbooking/service/CepService.java
package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CepService {

    private final RestTemplate restTemplate;

    public ViaCepResponse lookup(String zipCode) {
        String cep = normalizeZipCode(zipCode);

        // ViaCEP usa 8 dígitos
        if (cep.length() != 8) {
            return errorResponse();
        }

        String url = "https://viacep.com.br/ws/{cep}/json/";

        try {
            ViaCepResponse resp = restTemplate.getForObject(url, ViaCepResponse.class, cep);

            if (resp == null) return errorResponse();
            if (Boolean.TRUE.equals(resp.error())) return resp;

            return resp;

        } catch (RestClientException ex) {
            // timeout, DNS, 4xx/5xx, etc.
            return errorResponse();
        }
    }

    private String normalizeZipCode(String zip) {
        if (zip == null) return "";
        return zip.replaceAll("\\D", "");
    }

    private ViaCepResponse errorResponse() {
        return new ViaCepResponse(null, null, null, null, null, null, true);
    }
}
