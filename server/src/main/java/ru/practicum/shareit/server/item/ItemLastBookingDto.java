package ru.practicum.shareit.server.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemLastBookingDto {

    private final Long id;

    private final Long bookerId;
}
