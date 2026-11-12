package com.example.registry.client;

import com.example.registry.client.dto.StatusResponse;
import com.example.registry.config.Constants;
import com.example.registry.model.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@RequiredArgsConstructor
public class SupplierClient {

    private final RestClient restClient;
    private final Supplier supplier;

    public boolean isAvailable(final String phone) {
        try {
            StatusResponse response = restClient.get()
                .uri(supplier.baseUrl() + Constants.SUBSCRIBERS_PATH, phone)
                .header(Constants.API_KEY_HEADER, supplier.apiKey())
                .retrieve()
                .body(StatusResponse.class);
            return Optional.ofNullable(response)
                .map(StatusResponse::status)
                .map(Constants.STATUS_OK::equals)
                .orElse(false);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

}
