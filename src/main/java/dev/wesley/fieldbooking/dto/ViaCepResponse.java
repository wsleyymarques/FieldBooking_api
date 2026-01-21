package dev.wesley.fieldbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViaCepResponse(

        @JsonProperty("cep")
        String zipCode,

        @JsonProperty("logradouro")
        String street,

        @JsonProperty("complemento")
        String complement,

        @JsonProperty("bairro")
        String neighborhood,

        @JsonProperty("localidade")
        String city,

        @JsonProperty("uf")
        String state,

        @JsonProperty("erro")
        Boolean error
) {}
