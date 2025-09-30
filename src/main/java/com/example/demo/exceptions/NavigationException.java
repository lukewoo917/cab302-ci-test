package com.example.demo.exceptions;

/** Thrown when a view transition fails (bad FXML, controller error, etc.) */
public class NavigationException extends RuntimeException {
    public NavigationException(String message, Throwable cause) { super(message, cause); }
    public NavigationException(String message) { super(message); }
}

