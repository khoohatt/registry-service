package com.example.registry.exception;

public class SupplierLoadException extends RuntimeException {

    public SupplierLoadException(final String message,
                                 final Throwable cause) {
        super(message, cause);
    }

}
