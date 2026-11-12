package com.example.registry.provider;

import com.example.registry.exception.SupplierLoadException;
import com.example.registry.model.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JsonFileSupplierProvider implements SupplierProvider {

    private final File file;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Supplier> suppliers() {
        try {
            return objectMapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            throw new SupplierLoadException("Не удалось прочитать файл поставщиков: " + file, e);
        }
    }

}
