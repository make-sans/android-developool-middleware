package com.federlizer.servermiddleware;

public class Result {
    public String value;
    public Exception exception;

    public Result(String value) {
        this.value = value;
    }

    public Result(Exception exception) {
        this.exception = exception;
    }
}
