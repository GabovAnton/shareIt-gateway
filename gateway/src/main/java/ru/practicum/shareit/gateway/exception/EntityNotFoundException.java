package ru.practicum.shareit.gateway.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String s) {
        super(s);
    }
}