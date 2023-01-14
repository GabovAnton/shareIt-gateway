package ru.practicum.shareit.server.request;

import lombok.Builder;

@Builder
public class ItemProposalDto {

    private final Long id;

    private final Long itemId;

    private final String itemName;

    private final Long requesterId;
}
