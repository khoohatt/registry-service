package com.example.registry.service;

import com.example.registry.client.SupplierClient;
import com.example.registry.model.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SupplierRegistryService {

    private final RestClient restClient;

    private volatile Map<Long, SupplierClient> clients = Map.of();

    public void setSuppliers(final List<Supplier> suppliers) {
        Map<Long, SupplierClient> newClients = new HashMap<>();
        Optional.ofNullable(suppliers)
            .orElse(Collections.emptyList())
            .stream()
            .filter(val -> val.id() != null)
            .forEach(val -> newClients.put(val.id(), new SupplierClient(restClient, val)));
        clients = newClients;
        log.info("Загружены поставщики: {}", suppliers);
    }

    public boolean isAvailable(final Long supplierId,
                               final String phone) {
        return findClient(supplierId)
            .map(client -> client.isAvailable(phone))
            .orElseThrow(() -> new IllegalArgumentException("Неизвестный поставщик: " + supplierId));
    }

    private Optional<SupplierClient> findClient(final Long supplierId) {
        return Optional.ofNullable(clients.get(supplierId));
    }

}
