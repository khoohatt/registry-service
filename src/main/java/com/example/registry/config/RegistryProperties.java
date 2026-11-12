package com.example.registry.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "registry")
public record RegistryProperties(Path suppliersFile) {
}
