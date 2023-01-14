package ru.practicum.shareit.server.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String s) {
        super(s);
    }
}