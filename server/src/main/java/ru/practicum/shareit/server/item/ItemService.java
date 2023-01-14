package ru.practicum.shareit.server.item;

import java.util.List;

public interface ItemService {

    ItemDto getItemDto(long id, long userId);

    List<ItemDto> getAll(Integer from, Integer size, long userId);

    Item map(long id);

    Item save(Item item, long userId);

    Comment saveComment(Long itemId, Long userId, CommentDto commentDto);

    List<ItemDto> search(Integer from, Integer size, String text);

    ItemDto update(ItemPatchDto itemDto, long userId);

    Boolean isItemAvailable(long itemId);

}
