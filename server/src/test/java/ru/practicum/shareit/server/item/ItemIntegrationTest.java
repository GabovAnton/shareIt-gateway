package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

@Sql(scripts = {
        "classpath:/schema.sql",
        "classpath:/SampleData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemIntegrationTest {

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
    private ItemMapper itemMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserMapper userMapper;

    private final UserService userService = new UserServiceImpl(userRepository, userMapper);

    private final ItemService itemService = new ItemServiceImpl(itemRepository,
            userService,
            itemMapper,
            commentMapper,
            commentRepository,
            bookingRepository);

    @Test
    public void getItemDtoShouldReturnItemDto() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);

        ItemDto itemDto = itemService.getItemDto(1L, 1L);

        assertThat(itemDto.getName(), equalTo("Аккумуляторная дрель"));

    }

    @Test
    public void searchShouldReturnListItemDto() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "entityManager", entityManager);

        List<ItemDto> itemDtos = itemService.search(0, 20, "АкКумулЯторная");

        assertThat(itemDtos, hasSize(2));

        assertThat(itemDtos.get(0).getName(), equalTo("Аккумуляторная дрель"));
        assertThat(itemDtos.get(1).getName(), equalTo("Отвертка"));

    }

    @Test
    public void getAllShouldReturnListItemDto() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "entityManager", entityManager);

        List<ItemDto> itemDtos = itemService.getAll(0, 20, 1L);

        assertThat(itemDtos, hasSize(2));

    }

    @Test
    public void updateShouldThrowShareItValidationException() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "entityManager", entityManager);

        ItemPatchDto itemPatchDto = makeItemPatchDto();

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            itemService.update(itemPatchDto, 6);
        });
        assertThat(entityNotFoundException.getMessage(),
                equalTo("error while trying to update item which belongs to another user"));

    }

    @Test
    public void saveCommentShouldReturnEntity() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "entityManager", entityManager);

        Comment comment = itemService.saveComment(4L, 1L, makeCommentDto());

        assertThat(comment.getText(), equalTo("новый комментарий"));

    }

    @Test
    public void updateItemByMapperShouldReturnCorrectEntity() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(itemService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(itemService, "commentMapper", commentMapper);
        ReflectionTestUtils.setField(itemService, "entityManager", entityManager);

        ItemPatchDto itemPatchDto = makeItemPatchDto();
        Item itemToUpdate = itemRepository.findById(itemPatchDto.getId()).get();
        Item item = itemMapper.updateItemFromItemDto(itemPatchDto, itemToUpdate);

        assertThat(item.getName(), equalTo("simple thing"));

    }

    private ItemPatchDto makeItemPatchDto() {

        return new ItemPatchDto(1L, "simple thing", "just simple thig, nothing interesting", true, 100L);
    }

    private CommentDto makeCommentDto() {

        CommentDto commentDto = new CommentDto();
        commentDto.setText("новый комментарий");
        commentDto.setCreated(currentDate.minusDays(15));
        commentDto.setAuthorId(6L);
        commentDto.setAuthorName("Petr");

        return commentDto;

    }

}

