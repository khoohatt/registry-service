package com.example.registry.client;

import com.example.registry.client.dto.StatusResponse;
import com.example.registry.model.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("Тесты HTTP-клиента SupplierClient")
class SupplierClientTest {

    private static final String BASE_URL = "https://test.ru";
    private static final String PHONE = "79001234567";
    private static final String REQUEST_URL = BASE_URL + "/subscribers/" + PHONE;
    private static final String API_KEY = "key";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer mockServer;
    private SupplierClient supplierClient;

    @BeforeEach
    void setUp() {
        final RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        supplierClient = new SupplierClient(builder.build(), new Supplier(1L, BASE_URL, API_KEY));
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    @DisplayName("200 и status=ok: номер доступен")
    void whenIsAvailableAndStatusOkThenReturnTrueTest() throws Exception {
        mockServer.expect(requestTo(REQUEST_URL))
            .andRespond(withSuccess(json(new StatusResponse("ok")), MediaType.APPLICATION_JSON));

        final boolean result = supplierClient.isAvailable(PHONE);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Запрос отправляется методом GET с API-ключом в заголовке")
    void whenIsAvailableThenSendGetRequestWithApiKeyTest() throws Exception {
        mockServer.expect(requestTo(REQUEST_URL))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-Api-Key", API_KEY))
            .andRespond(withSuccess(json(new StatusResponse("ok")), MediaType.APPLICATION_JSON));

        supplierClient.isAvailable(PHONE);
    }

    @Test
    @DisplayName("404: номер недоступен")
    void whenIsAvailableAndNotFoundThenReturnFalseTest() {
        mockServer.expect(requestTo(REQUEST_URL))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final boolean result = supplierClient.isAvailable(PHONE);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("200 со статусом, отличным от ok: номер недоступен")
    void whenIsAvailableAndOtherStatusThenReturnFalseTest() throws Exception {
        mockServer.expect(requestTo(REQUEST_URL))
            .andRespond(withSuccess(json(new StatusResponse("blocked")), MediaType.APPLICATION_JSON));

        final boolean result = supplierClient.isAvailable(PHONE);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("200 с пустым телом: номер недоступен")
    void whenIsAvailableAndEmptyBodyThenReturnFalseTest() {
        mockServer.expect(requestTo(REQUEST_URL))
            .andRespond(withSuccess());

        final boolean result = supplierClient.isAvailable(PHONE);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("5xx пробрасывается вызывающему коду")
    void whenIsAvailableAndServerErrorThenThrowExceptionTest() {
        mockServer.expect(requestTo(REQUEST_URL))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> supplierClient.isAvailable(PHONE))
            .isInstanceOf(HttpServerErrorException.InternalServerError.class);
    }

    private String json(final Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

}
