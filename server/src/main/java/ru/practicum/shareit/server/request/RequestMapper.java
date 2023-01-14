package ru.practicum.shareit.server.request;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RequestMapper {

    Request requestDtoToRequest(RequestDto requestDto);

    RequestWithProposalsDto requestToRequestWithProposalDto(Request request);

    RequestDto requestToRequestDto(Request request);
}
