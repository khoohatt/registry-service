package com.example.registry.config;

import com.example.registry.provider.JsonFileSupplierProvider;
import com.example.registry.provider.SupplierProvider;
import com.example.registry.service.SupplierRegistryService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RegistryProperties.class)
public class RegistryConfig {

    @Bean
    public RestClient restClient(final RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public SupplierProvider supplierProvider(final RegistryProperties properties) {
        return new JsonFileSupplierProvider(properties.suppliersFile().toFile());
    }

    @Bean
    public SupplierRegistryService supplierRegistryService(final RestClient restClient,
                                                           final SupplierProvider supplierProvider) {
        SupplierRegistryService registryService = new SupplierRegistryService(restClient);
        registryService.setSuppliers(supplierProvider.suppliers());
        return registryService;
    }

}
