package ru.practicum.shareit.custom;

public class EmailValidationException extends RuntimeException {

    public EmailValidationException(String message) {
        super(message);
    }
}
