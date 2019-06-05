package com.federlizer.servermiddleware.exceptions;

public class NotFoundException extends Exception {
    private String key;

    public NotFoundException(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + System.lineSeparator() + key;
    }
}
