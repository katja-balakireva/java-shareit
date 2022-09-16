package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //code 404
    public Map<String, String> handleUserNotFoundException(final UserNotFoundException e) {
        return Map.of("not found", "Пользователь не найден");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //code 400
    public Map<String, String> handleEmailValidationException(final EmailValidationException e) {
        return Map.of("bad request", "Валидация email не пройдена");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //code 500
    public Map<String, String> handleDuplicateEmailException(final DuplicateEmailException e) {
        return Map.of("internal server error", "Пользователь с таким email уже существует");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN) //code 403
    public Map<String, String> handleValidateOwnershipException(final ValidateOwnershipException e) {
        return Map.of("forbidden", "Нет доступа");
    }
}
