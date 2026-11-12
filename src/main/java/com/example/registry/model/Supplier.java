package com.example.registry.model;

public record Supplier(Long id, String baseUrl, String apiKey) {

    @Override
    public String toString() {
        return "Supplier[id=" + id + ", baseUrl=" + baseUrl + ", apiKey=***]";
    }

}
