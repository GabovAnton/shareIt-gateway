package ru.practicum.shareit.server.request;

import java.util.List;

public interface RequestService {

    List<RequestWithProposalsDto> getAll(Integer from, Integer size, Long userId);

    RequestDto saveRequest(RequestDto requestDto, Long userId);

    List<RequestWithProposalsDto> getAllFromOthers(Integer from, Integer size, Long userId);

    RequestWithProposalsDto getRequest(Long requestId, Long userId);

}
