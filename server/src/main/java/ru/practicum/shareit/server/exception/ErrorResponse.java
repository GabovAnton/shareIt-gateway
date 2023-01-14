package ru.practicum.shareit.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ErrorResponse {

    @Getter
    String error;
}

