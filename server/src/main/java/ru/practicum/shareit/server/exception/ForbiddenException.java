package ru.practicum.shareit.server.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String s) {
        super(s);
    }
}

