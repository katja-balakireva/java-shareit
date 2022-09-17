package ru.practicum.shareit.exceptions;

public class ControllerException extends RuntimeException {

    public ControllerException() {
    }

    @Override
    public String getMessage() {
        return "Ошибка валидации поля класса";
    }
}