package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {

        return ResponseEntity.ok(itemService.getItemDto(itemId, userId));

    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> getAll(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        return ResponseEntity.ok(itemService.getAll(from, size, userId));
    }

    @PostMapping()
    public ItemDto create(
            @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {

        Item savedItem = itemService.save(ItemMapper.INSTANCE.itemDtoToItem(itemDto), userId);
        return ItemMapper.INSTANCE.itemToItemDto(savedItem);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(
            @PathVariable long itemId,
            @Valid @RequestBody ItemPatchDto itemPatchDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        itemPatchDto.setId(itemId);
        return itemService.update(itemPatchDto, userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> searchByQuery(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        return itemService.search(from, size, text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto postComment(
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        Comment comment = itemService.saveComment(itemId, userId, commentDto);
        return CommentMapper.INSTANCE.commentToCommentDto(comment);
    }

}
