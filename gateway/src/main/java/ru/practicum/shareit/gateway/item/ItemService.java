package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemPatchDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"items"})
public class ItemService {

    private final ItemFeignClient itemFeignClient;

    //@Cacheable(key = "{#userId + #itemId }")
    public ItemDto getItem(
            long itemId, long userId) {

        return itemFeignClient.getItem(itemId, userId);
    }

    @Cacheable(value = "items")
    public List<ItemDto> getAll(
            long userId, Integer from, Integer size) {

        return itemFeignClient.getAll(userId, from, size);
    }

    @CachePut(key = "{#userId + #result.id}")
    public ItemDto create(long userId, ItemDto itemDto) {

        return itemFeignClient.create(userId, itemDto);
    }

    @CacheEvict(value = "items")
    public ItemDto update(
            long userId, long itemId, ItemPatchDto itemPatchDto) {

        return itemFeignClient.update(userId, itemId, itemPatchDto);
    }

    //@Cacheable(key = "{ #userId + #text}")
    public List<ItemDto> search(
            long userId, Integer from, Integer size, String text) {

        return itemFeignClient.search(userId, from, size, text);
    }

/*
    @CacheEvict(key = "{ #userId + #itemId}")
*/
    //@CacheEvict(key = "{ #userId + #itemId}")
    @CacheEvict(value = "items")
    public CommentDto postComment(
            Long itemId, CommentDto commentDto, Long userId) {

        return itemFeignClient.postComment(itemId, commentDto, userId);
    }

}
