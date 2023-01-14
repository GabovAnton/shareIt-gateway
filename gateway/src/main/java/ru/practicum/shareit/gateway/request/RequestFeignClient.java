package ru.practicum.shareit.gateway.request;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.dto.RequestWithProposalsDto;
import ru.practicum.shareit.gateway.request.dto.RequestDto;

import java.util.List;

@FeignClient(value = "requestClient", path = "/requests", url = "${SHAREIT_SERVER_URL}")
@Headers("X-Sharer-User-Id: {userId}")
public interface RequestFeignClient {

    @GetMapping()
    List<RequestWithProposalsDto> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam Integer from,
            @RequestParam Integer size);

    @GetMapping("/all")
    List<RequestWithProposalsDto> getAllFromOthers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size);

    @PostMapping()
    RequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody RequestDto requestDto);

    @GetMapping("{requestId}")
    RequestWithProposalsDto getRequestById(
            @PathVariable("requestId") Long requestId, @RequestHeader("X-Sharer-User-Id") long userId);

}
