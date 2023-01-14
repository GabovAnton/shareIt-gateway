package ru.practicum.shareit.server.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.item.CommentDto;
import ru.practicum.shareit.server.item.ItemDto;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto implements Serializable {

    private Long id;

    private Set<ItemDto> items;

    private Set<CommentDto> Comments;

    private String name;

    private String email;

    private String registrationDate;

}