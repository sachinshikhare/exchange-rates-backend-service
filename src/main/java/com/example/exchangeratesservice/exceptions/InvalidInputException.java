package com.example.exchangeratesservice.exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(final String msg) {
        super(msg);
    }
}
