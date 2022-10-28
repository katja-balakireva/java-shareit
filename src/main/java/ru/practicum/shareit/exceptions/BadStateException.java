package ru.practicum.shareit.exceptions;

public class BadStateException extends RuntimeException{
    public BadStateException(String message) {
        super(message);
    }
}
