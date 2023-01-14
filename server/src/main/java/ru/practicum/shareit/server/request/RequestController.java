package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("")
    public List<RequestWithProposalsDto> getAll(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return requestService.getAll(from, size, userId);

    }

    @PostMapping()
    public RequestDto create(
            @RequestBody RequestDto requestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {

        return requestService.saveRequest(requestDto, userId);
    }

    @GetMapping("/all")
    public List<RequestWithProposalsDto> getAllFromOthers(
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return requestService.getAllFromOthers(from, size, userId);
    }

    @GetMapping("{requestId}")
    public RequestWithProposalsDto getBookingById(
            @PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {

        return requestService.getRequest(requestId, userId);
    }

}
