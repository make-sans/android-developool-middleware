package com.federlizer.servermiddleware.exceptions;

public class UserAlreadyExistsException extends Exception {
    private String key;

    public UserAlreadyExistsException(String key) {
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
