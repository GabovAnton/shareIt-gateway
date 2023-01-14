package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemService itemService = new ItemServiceImpl(itemRepository, userService, itemMapper, commentMapper,

                                                          commentRepository, bookingRepository
    );

    @Test
    void getItemDtoWrongItemShouldThrowException() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);

        Long ItemId = 100L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemDto(100L, 100L);
        });
        assertThat(entityNotFoundException.getMessage(), equalTo("item with id: " + ItemId + " doesn't exists"));
    }

    @Test
    void getItemDto() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);

        Long itemId = 100L;
        when(itemRepository.findById(anyLong())).thenThrow(
                new EntityNotFoundException("item with id: " + itemId + " doesn't exists"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            itemService.getItemDto(100L, 100L);
        });
        assertThat(entityNotFoundException.getMessage(), equalTo("item with id: " + itemId + " doesn't exists"));
    }

    @Test
    void mapWrongItemShouldThrowException() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);

        Long itemId = 100L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            itemService.map(100L);
        });
        assertThat(entityNotFoundException.getMessage(), equalTo("item with id: " + itemId + " doesn't exists"));
    }

    @Test
    void saveShouldNotThrowException() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        Item item = makeItem();
        when(itemRepository.save(any())).thenReturn(item);
        itemService.save(item, 100L);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getId(), equalTo(item.getId()));

    }

    @Test
    void saveCommentShouldThrowExceptionOnBlankText() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        Comment comment = makeComment();
        CommentDto commentDto = CommentMapper.INSTANCE.commentToCommentDto(comment);

        assertThat(comment.getText(), equalTo(commentDto.getText()));
        commentDto.setText("");

        ForbiddenException ForbiddenException = assertThrows(ForbiddenException.class, () -> {
            itemService.saveComment(100L, 100L, commentDto);
        });

        assertThat(ForbiddenException.getMessage(), equalTo("Comment should not be empty"));
        verify(commentRepository, never()).save(comment);

    }

    @Test
    void saveCommentShouldThrowExceptionOnUnfinishedBooking() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);
        ReflectionTestUtils.setField(itemService, "bookingRepository", bookingRepository);
        Comment comment = makeComment();
        CommentDto commentDto = CommentMapper.INSTANCE.commentToCommentDto(comment);

        assertThat(comment.getText(), equalTo(commentDto.getText()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(makeItem()));
        when(userService.getUser(anyLong())).thenReturn(makeUser());
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            itemService.saveComment(100L, 100L, commentDto);
        });

        assertThat(forbiddenException.getMessage(),
                   equalTo("error while trying to add comment to item which hasn't  finished booking by user")
        );
        verify(commentRepository, never()).save(comment);

    }

    @Test
    void updateShouldThrowExceptionOnUpdateItemWhichIsNotBelongToUser() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(itemService, "userService", userService);

        ItemPatchDto itemPatchDto = makeItemPatchDto();

        Item item = makeItem();
        User user = makeUser();
        item.setOwner(user);

        item.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            itemService.update(itemPatchDto, 200L);
        });

        assertThat(entityNotFoundException.getMessage(),
                   equalTo("error while trying to update item which belongs to another user")
        );

        verify(itemRepository, never()).save(any());

    }

    @Test
    void isItemAvailableShouldThrowExceptionOnUnavailableItem() {

        ReflectionTestUtils.setField(itemService, "itemRepository", itemRepository);

        Long itemId = 100L;

        ItemShortAvailability itemShortAvailability = () -> false;

        when(itemRepository.isItemAvailable(anyLong())).thenReturn(Optional.of(itemShortAvailability));

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> {
            itemService.isItemAvailable(itemId);
        });

        assertThat(forbiddenException.getMessage(), equalTo("item with id: " + itemId + " is unavailable for booking"));
        verify(itemRepository, never()).save(any());

    }

    private User makeUser() {

        User user = new User();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");

        return user;
    }

    private Comment makeComment() {

        Comment comment = new Comment();
        comment.setAuthor(makeUser());
        comment.setCreated(currentDate.minusDays(15));
        comment.setText("good thing");

        return comment;

    }

    private Item makeItem() {

        Item item = new Item();
        item.setId(100L);
        Comment comment = makeComment();
        comment.setItem(item);
        item.setItemComments(Set.of(comment));
        item.setName("simple thing");
        item.setDescription("just simple thig, nothing interesting");
        item.setAvailable(true);
        return item;
    }

    private ItemPatchDto makeItemPatchDto() {

        return new ItemPatchDto(100L, "simple thing", "just simple thig, nothing interesting", true, 100L);
    }

}