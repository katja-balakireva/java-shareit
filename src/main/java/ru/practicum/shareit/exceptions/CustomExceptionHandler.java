package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String message = "Пользователь не найден";
        return handleExceptionInternal(ex, message,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {EmailValidationException.class})
    public ResponseEntity<Object> handleEmailValidation(RuntimeException ex, WebRequest request) {
        String message = "Валидация email не пройдена";
        return handleExceptionInternal(ex, message,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {DuplicateEmailException.class})
    public ResponseEntity<Object> handleDuplicateEmail(RuntimeException ex, WebRequest request) {
        String message = "Пользователь с таким email уже существует";
        return handleExceptionInternal(ex, message,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {ValidateOwnershipException.class})
    public ResponseEntity<Object> handleOwnershipException(RuntimeException ex, WebRequest request) {
        String message = "Нет доступа к изменению объекта";
        return handleExceptionInternal(ex, message,
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
