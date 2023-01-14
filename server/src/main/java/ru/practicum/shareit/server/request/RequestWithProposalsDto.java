package ru.practicum.shareit.server.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.server.item.ItemDto;
import ru.practicum.shareit.server.user.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class RequestWithProposalsDto implements Serializable {

    private final Long id;

    private final String description;

    private final UserDto requester;

    private final LocalDateTime created;

    private List<ItemDto> items;

}