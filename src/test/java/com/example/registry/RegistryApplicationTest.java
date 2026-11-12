package com.example.registry;

import com.example.registry.config.RegistryProperties;
import com.example.registry.provider.SupplierProvider;
import com.example.registry.service.SupplierRegistryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "registry.suppliers-file=src/test/resources/suppliers.json")
class RegistryApplicationTest {

    @Autowired
    private SupplierRegistryService registry;
    @Autowired
    private RegistryProperties properties;
    @Autowired
    private SupplierProvider supplierProvider;

    @Test
    void contextLoadsRegistryFromJsonFileTest() {
        assertNotNull(registry);
    }

}
