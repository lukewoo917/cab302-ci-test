package com.example.demo.exceptions;

public class TimerNotRunningException extends RuntimeException {
    public TimerNotRunningException(String message) {
        super(message);
    }
}
