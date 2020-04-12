package com.example.exchangeratesservice.exceptions;

public class ExternalApiCallFailedException extends RuntimeException {
    public ExternalApiCallFailedException(final String msg) {
        super(msg);
    }
}
