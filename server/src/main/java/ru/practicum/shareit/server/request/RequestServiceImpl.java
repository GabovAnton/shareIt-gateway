package ru.practicum.shareit.server.request;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.QItem;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.item.ItemDto;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserService userService;

    private final RequestMapper requestMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RequestWithProposalsDto> getAll(Integer from, Integer size, Long userId) {

        QRequest qRequest = QRequest.request;

        User user = userService.getUser(userId);

        JPAQuery<Request> query = new JPAQuery<>(entityManager);

        long TotalItems = requestRepository.count() + 1;

        int offset = from != null ? (from > 1 ? --from : from) : 0;

        List<RequestWithProposalsDto>
                requests =
                query
                        .from(qRequest)
                        .where(qRequest.requester.id.eq(user.getId()))
                        .orderBy(qRequest.created.desc())
                        .limit(size != null ? size : TotalItems)
                        .offset(offset)
                        .fetch()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(requestMapper::requestToRequestWithProposalDto)
                        .collect(Collectors.toList());

        return requests.stream().peek(x -> x.setItems(getItemsDto(x.getId()))).collect(Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList));

    }

    @Override
    public RequestDto saveRequest(RequestDto requestDto, Long userId) {

        Request request = requestMapper.requestDtoToRequest(requestDto);

        request.setRequester(userService.getUser(userId));

        request.setCreated(LocalDateTime.now());

        Request save = requestRepository.save(request);

        return requestMapper.requestToRequestDto(save);
    }

    @Override
    public List<RequestWithProposalsDto> getAllFromOthers(Integer from, Integer size, Long userId) {

        QRequest request = QRequest.request;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        long TotalItems = requestRepository.count() + 1;

        int offset = from != null ? (from > 1 ? --from : from) : 0;

        List<RequestWithProposalsDto>
                requests =
                queryFactory
                        .selectFrom(request)
                        .where(request.requester.id.notIn(userId))
                        .orderBy(request.created.desc())
                        .limit(size != null ? size : TotalItems)
                        .offset(offset)
                        .fetch()
                        .stream()
                        .map(requestMapper::requestToRequestWithProposalDto)
                        .collect(Collectors.toList());

        return requests.stream().peek(x -> x.setItems(getItemsDto(x.getId()))).collect(Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList));

    }

    @Override
    public RequestWithProposalsDto getRequest(Long requestId, Long userId) {

        QRequest qRequest = QRequest.request;
        if (!requestRepository.existsById(requestId)) {
            throw new EntityNotFoundException("request with id: " + requestId + " not found");
        }
        userService.getUser(userId);

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        RequestWithProposalsDto request = requestMapper.requestToRequestWithProposalDto(queryFactory
                .selectFrom(qRequest)
                .where(qRequest.id.eq(requestId))
                .fetchOne());
        request.setItems(getItemsDto(request.getId()));
        return request;

    }

    private List<ItemDto> getItemsDto(Long requestId) {

        JPAQuery<Item> query = new JPAQuery<>(entityManager);
        QItem qItem = QItem.item;
        return query
                .from(qItem)
                .where(qItem.requestId.eq(requestId))
                .fetch()
                .stream()
                .map(ItemMapper.INSTANCE::itemToItemDto)
                .collect(Collectors.toList());
    }

}
