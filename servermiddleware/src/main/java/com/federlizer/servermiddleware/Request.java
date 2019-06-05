package com.federlizer.servermiddleware;

public class Request {
    private String requestMethod;
    private String route;

    public Request(String requestMethod, String route) {
        this.requestMethod = requestMethod;
        this.route = route;
    }

    public String RequestMethod() {
        return requestMethod;
    }

    public String Route() {
        return route;
    }
}
