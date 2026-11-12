package com.example.registry.service;

import com.example.registry.model.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SupplierRegistryServiceTest {

    private static final String PHONE = "79001234567";
    private static final String OK_BODY = "{\"status\":\"ok\"}";

    private MockRestServiceServer server;
    private SupplierRegistryService registry;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        registry = new SupplierRegistryService(builder.build());
        registry.setSuppliers(List.of(
            new Supplier(1L, "https://test1.ru", "key-1"),
            new Supplier(2L, "https://test2.ru", "key-2")));
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void phoneIsAvailableWhenSupplierReturnsOkTest() {
        server.expect(requestTo("https://test1.ru/subscribers/" + PHONE))
            .andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

        assertTrue(registry.isAvailable(1L, PHONE));
    }

    @Test
    void phoneIsNotAvailableWhenSupplierReturns404Test() {
        server.expect(requestTo("https://test1.ru/subscribers/" + PHONE))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertFalse(registry.isAvailable(1L, PHONE));
    }

    @Test
    void requestGoesToSelectedSupplierWithItsApiKeyTest() {
        server.expect(requestTo("https://test2.ru/subscribers/" + PHONE))
            .andExpect(header("X-Api-Key", "key-2"))
            .andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));

        assertTrue(registry.isAvailable(2L, PHONE));
    }

    @Test
    void throwsWhenUnknownSupplierTest() {
        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(3L, PHONE));
    }

    @Test
    void setSuppliersReplacesSetTest() {
        registry.setSuppliers(List.of(new Supplier(3L, "https://test3.ru", "key-3")));

        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(1L, PHONE));

        server.expect(requestTo("https://test3.ru/subscribers/" + PHONE))
            .andExpect(header("X-Api-Key", "key-3"))
            .andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));
        assertTrue(registry.isAvailable(3L, PHONE));
    }

    @Test
    void emptyListRemovesAllSuppliersTest() {
        registry.setSuppliers(List.of());

        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(1L, PHONE));
        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(2L, PHONE));
    }

    @Test
    void nullListRemovesAllSuppliersTest() {
        registry.setSuppliers(null);

        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(1L, PHONE));
    }

    @Test
    void supplierWithoutIdIsSkippedTest() {
        registry.setSuppliers(List.of(new Supplier(null, "https://test-null.ru", "key-null"),
            new Supplier(3L, "https://test3.ru", "key-3")));

        assertThrows(IllegalArgumentException.class, () -> registry.isAvailable(null, PHONE));

        server.expect(requestTo("https://test3.ru/subscribers/" + PHONE))
            .andRespond(withSuccess(OK_BODY, MediaType.APPLICATION_JSON));
        assertTrue(registry.isAvailable(3L, PHONE));
    }

}
