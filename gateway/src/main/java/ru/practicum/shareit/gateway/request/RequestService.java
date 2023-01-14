package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.gateway.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.gateway.request.dto.RequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"requests"})
public class RequestService {

    private final RequestFeignClient requestFeignClient;

/*
    @Cacheable(key = "{#userId }")
*/
    public List<RequestWithProposalsDto> getAll(
            long userId, Integer from, Integer size) {

        return requestFeignClient.getAll(userId, from, size);
    }

/*
    @Cacheable(key = "{#userId }")
*/
    public List<RequestWithProposalsDto> getAllFromOthers(
            long userId, Integer from, Integer size) {

        return requestFeignClient.getAllFromOthers(userId, from, size);
    }

/*    @Caching(evict = {
            @CacheEvict(value = "items", key = "{#userId + #requestDto.itemId }"),},
            put = {@CachePut(key = "{#userId + #requestDto.id }")})*/
    @CacheEvict(value = "items")
    public RequestDto create(
            long userId, RequestDto requestDto) {

        return requestFeignClient.create(userId, requestDto);
    }

/*
    @Cacheable(key = "{#userId + #requestId }")
*/
    public RequestWithProposalsDto getRequestById(
            long requestId, long userId) {

        return requestFeignClient.getRequestById(requestId, userId);
    }

}
