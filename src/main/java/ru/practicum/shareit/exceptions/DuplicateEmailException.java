package ru.practicum.shareit.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
    }

    public DuplicateEmailException(String message) {
        super(message);
    }
}
