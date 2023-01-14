package ru.practicum.shareit.gateway.item;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemPatchDto;

import java.util.List;

@FeignClient(value = "itemClient", path = "/items", url = "${SHAREIT_SERVER_URL}")
@Headers("X-Sharer-User-Id: {userId}")
public interface ItemFeignClient {

    @GetMapping("{itemId}")
    ItemDto getItem(
            @PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") long userId);

    @GetMapping()
    List<ItemDto> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam Integer from,
            @RequestParam Integer size);

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto);

    @PatchMapping("{itemId}")
    ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody ItemPatchDto itemPatchDto);

   @GetMapping("/search?from={from}&size={size}&text={text}")
    //@GetMapping("/search/{text}")
    List<ItemDto> search(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam Integer from,
            @RequestParam Integer size,
            @PathVariable("text") String text);

    @PostMapping("{itemId}/comment")
    CommentDto postComment(
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId);

}
