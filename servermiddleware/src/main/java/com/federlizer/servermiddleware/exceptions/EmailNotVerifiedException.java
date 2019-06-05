package com.federlizer.servermiddleware.exceptions;

public class EmailNotVerifiedException extends Exception {
    private String key;

    public EmailNotVerifiedException(String key) {
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
