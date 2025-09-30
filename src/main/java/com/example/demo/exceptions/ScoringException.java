package com.example.demo.exceptions;

/** Thrown when scoring cannot be computed (bad input sizes, nulls, etc.) */
public class ScoringException extends RuntimeException {
    public ScoringException(String message) { super(message); }
}