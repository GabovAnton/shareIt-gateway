package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.item.*;
import ru.practicum.shareit.server.user.*;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.BookingSearchFactory;


import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = {"classpath:/schema.sql", "classpath:/SampleData.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RequestIntegrationTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 9, 17, 5, 5, 5, 5);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserMapper userMapper;

    private final UserService userService = new UserServiceImpl(userRepository, userMapper);

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    private final ItemService itemService = new ItemServiceImpl(itemRepository, userService, itemMapper, commentMapper,
                                                                commentRepository, bookingRepository
    );

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private RequestRepository requestRepository;

    private final RequestService requestService = new RequestServiceImpl(requestRepository, userService, requestMapper);

    @Autowired
    private BookingSearchFactory bookingSearchFactory;

    @Test
    public void getAllFromOthers_Fetch10ElementsFrom26OfTotal30ElementShouldReturnExactFrom5To1()
            throws InterruptedException {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        ReflectionTestUtils.setField(requestService, "requestRepository", requestRepository);
        ReflectionTestUtils.setField(requestService, "userService", userService);
        ReflectionTestUtils.setField(requestService, "entityManager", entityManager);

        List<User> users = new ArrayList<>(userRepository.findAll());
        for (int i = 0; i < 30; i++) {
            Request request = new Request();

            int length = 50;
            boolean useLetters = true;
            boolean useNumbers = false;
            String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
            request.setDescription(generatedString);

            Collections.shuffle(users);
            request.setRequester(users.get(0));

            RequestDto requestDto = requestMapper.requestToRequestDto(request);
            requestService.saveRequest(requestDto, users.get(0).getId());
            Thread.sleep(100);

        }

        int unrealId = users.size() + 1;
        List<RequestWithProposalsDto> allFromOthers = requestService.getAllFromOthers(26, 10, (long) unrealId);
        List<Long> collect = allFromOthers.stream().map(RequestWithProposalsDto::getId).collect(Collectors.toList());

        assertThat(collect, hasSize(6));
        assertThat(collect, Matchers.containsInRelativeOrder(List.of(5L, 4L, 3L, 2L, 1L).toArray()));

    }

    @Test
    public void getRequestShouldReturnRequestWithProposalsDto() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        ReflectionTestUtils.setField(requestService, "requestRepository", requestRepository);
        ReflectionTestUtils.setField(requestService, "userService", userService);
        ReflectionTestUtils.setField(requestService, "entityManager", entityManager);

        RequestWithProposalsDto request = requestService.getRequest(1L, 1L);

        assertThat(request.getDescription(), equalTo("I wanna this item"));

    }

    @Test
    public void getAllRequestsForUser6ShouldReturnExact31() throws InterruptedException {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        ReflectionTestUtils.setField(requestService, "requestRepository", requestRepository);
        ReflectionTestUtils.setField(requestService, "userService", userService);
        ReflectionTestUtils.setField(requestService, "entityManager", entityManager);

        for (int i = 0; i < 30; i++) {
            Request request = new Request();

            int length = 50;
            boolean useLetters = true;
            boolean useNumbers = false;
            String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
            request.setDescription(generatedString);

            request.setRequester(userRepository.findById(6L).get());

            RequestDto requestDto = requestMapper.requestToRequestDto(request);
            requestService.saveRequest(requestDto, 6L);
            Thread.sleep(100);

        }

        List<RequestWithProposalsDto> allFromOthers = requestService.getAll(null,null,6L);
        List<Long> collect = allFromOthers.stream().map(RequestWithProposalsDto::getId).collect(Collectors.toList());

        assertThat(collect, hasSize(31));

    }

}
