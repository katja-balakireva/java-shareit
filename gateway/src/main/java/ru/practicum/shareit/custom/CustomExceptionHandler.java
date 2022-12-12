package ru.practicum.shareit.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFound(UserIdValidationException exception) {
        log.warn("Выброшено исключение: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingNotFound(BookingDateException exception) {
        log.warn("Выброшено исключение: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRequestNotFound(UnsupportedStateException exception) {
        log.warn("Выброшено исключение: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable throwable) {
        log.warn("Выброшено исключение: " + throwable.getMessage());
        return new ErrorResponse(throwable.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<String> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        log.warn("Выброшено исключение: " + exception.getMessage());
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        final List<String> errors = new ArrayList<>();
        for (final FieldError err : fieldErrors) {
            errors.add(err.getField() + ": " + err.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        log.warn("Выброшено исключение: " + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}

