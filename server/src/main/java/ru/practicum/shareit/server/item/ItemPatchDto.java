package ru.practicum.shareit.server.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemPatchDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
