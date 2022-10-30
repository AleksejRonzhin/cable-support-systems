package ru.rsreu.cable.exceptions;

public class IncorrectFileException extends Throwable {
    private final String message;

    public IncorrectFileException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}