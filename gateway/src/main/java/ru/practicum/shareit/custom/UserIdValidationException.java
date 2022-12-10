package ru.practicum.shareit.custom;

public class UserIdValidationException extends RuntimeException {
    public UserIdValidationException(String message) {
        super(message);
    }
}