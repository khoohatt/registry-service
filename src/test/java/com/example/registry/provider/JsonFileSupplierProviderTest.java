package com.example.registry.provider;

import com.example.registry.exception.SupplierLoadException;
import com.example.registry.model.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFileSupplierProviderTest {

    @Test
    void readsSuppliersFromFile() throws IOException {
        JsonFileSupplierProvider provider = new JsonFileSupplierProvider(resource("provider/suppliers.json"));

        List<Supplier> suppliers = provider.suppliers();

        assertEquals(List.of(new Supplier(1L, "https://test1.ru", "key-1"),
            new Supplier(2L, "https://test2.ru", "key-2")), suppliers);
    }

    @Test
    void readsEmptyList() throws IOException {
        JsonFileSupplierProvider provider = new JsonFileSupplierProvider(resource("provider/empty-suppliers.json"));

        assertTrue(provider.suppliers().isEmpty());
    }

    @Test
    void throwsWhenJsonIsInvalid() throws IOException {
        JsonFileSupplierProvider provider = new JsonFileSupplierProvider(resource("provider/invalid-suppliers.json"));

        assertThrows(SupplierLoadException.class, provider::suppliers);
    }

    @Test
    void throwsWhenFileIsMissing() {
        JsonFileSupplierProvider provider = new JsonFileSupplierProvider(new File("does-not-exist.json"));

        assertThrows(SupplierLoadException.class, provider::suppliers);
    }

    private static File resource(String path) throws IOException {
        return new ClassPathResource(path).getFile();
    }

}
