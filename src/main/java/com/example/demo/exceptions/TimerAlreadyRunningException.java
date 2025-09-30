package com.example.demo.exceptions;

public class TimerAlreadyRunningException extends RuntimeException {
    public TimerAlreadyRunningException(String message) {
        super(message);
    }
}
