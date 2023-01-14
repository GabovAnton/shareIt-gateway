package ru.practicum.shareit.server;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.request.*;
import ru.practicum.shareit.server.item.Comment;
import ru.practicum.shareit.server.item.CommentRepository;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserDto;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = {"classpath:/schema.sql", "classpath:/SampleData.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShareItTests {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

    private final RequestMapper requestMapper;

    private final RequestService requestService;

    @Test
    void contextLoads() {

    }

    @Test
    public void testDatabaseIsNotEmpty() {

        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers, hasSize(6));

        List<Item> allItems = itemRepository.findAll();
        assertThat(allItems, hasSize(10));

    }

    @Test
    public void createUserShouldNotThrowExceptionOnSave() {

        UserDto userOne = new UserDto(null, "testName", "test@gmail.com", null);

        assertThat(userRepository.save(userMapper.userDtoToUser(userOne)).getName(), equalToIgnoringCase("testName"));

    }

    @Test
    public void createUserWithDuplicateEmailShouldThrowExceptionOnSave() {

        UserDto userOne = new UserDto(null, "testName", "agabov@gmail.com", null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(userMapper.userDtoToUser(userOne));
        });

    }

    @Test
    public void updateUserWithDuplicateEmailShouldThrowExceptionOnSave() {

        User user = userRepository.findById(1L).get();
        user.setEmail("ivan@gmail.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    public void updateUserShouldNotThrowExceptionOnSave() {

        User user = userRepository.findById(1L).get();
        user.setEmail("anton2@gmail.com");

        assertThat(userRepository.save(user).getEmail(), equalToIgnoringCase("anton2@gmail.com"));

    }

    @Test
    public void deleteUserShouldNotThrowExceptionOnSave() {

        User user = userRepository.findById(1L).get();

        userRepository.delete(user);

    }

    @Test
    public void createItemShouldNotThrowExceptionOnSave() {

        Item item = new Item();
        item.setName("Test item");
        item.setAvailable(true);
        item.setDescription("new interesting item");
        item.setOwner(userRepository.findById(1L).get());

        assertThat(itemRepository.save(item).getName(), equalToIgnoringCase("Test item"));

    }

    @Test
    public void getAllItemsForUserOneShouldReturnTwoRecords() {

        List<Item> searchResult = itemRepository.findByOwnerId(1L);

        assertThat(searchResult, hasSize(2));
        assertThat(searchResult.stream().anyMatch(x -> x.getName().equals("Аккумуляторная дрель")), is(true));
    }

    @Test
    public void createCommentOnItem2ByUserShouldNotThrowError() {

        Comment comment = new Comment();
        comment.setItem(itemRepository.findByOwnerId(1L).stream().findFirst().get());
        comment.setText("test comment");
        comment.setAuthor(userRepository.findById(3L).get());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        Set<Comment> itemComments = itemRepository
                .findByOwnerId(1L)
                .stream()
                .filter(x -> x.getId().equals(1L))
                .findFirst()
                .get()
                .getItemComments();

        assertThat(itemComments, hasSize(1));
        assertThat(itemComments.stream().anyMatch(x -> x.getText().equals("test comment")), is(true));

    }

    @Test
    public void fetch10ElementsFrom26OfTotal30ElementShouldReturnExactFrom5To1() {

        List<User> users = new ArrayList<>(userRepository.findAll());
        int day = 1;
        for (int i = 0; i < 30; i++) {
            Request request = new Request();
            request.setCreated(LocalDateTime.now().plusDays(day++));

            int length = 50;
            boolean useLetters = true;
            boolean useNumbers = false;
            String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
            request.setDescription(generatedString);

            Collections.shuffle(users);
            request.setRequester(users.get(0));

            RequestDto requestDto = requestMapper.requestToRequestDto(request);
            requestService.saveRequest(requestDto, users.get(0).getId());

        }

        int unrealId = users.size() + 1;
        List<Long> collect = requestService.getAllFromOthers(26, 10, (long) unrealId).stream().map(
                RequestWithProposalsDto::getId).collect(Collectors.toList());

        assertThat(collect, hasSize(6));
        assertThat(collect, Matchers.containsInRelativeOrder(List.of(5L, 4L, 3L, 2L, 1L).toArray()));

    }

}
