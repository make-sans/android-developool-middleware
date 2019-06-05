package com.federlizer.servermiddleware.models;

public class Account {
    public String token;
    public String username;
    public String email;

    public Account(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }
}
