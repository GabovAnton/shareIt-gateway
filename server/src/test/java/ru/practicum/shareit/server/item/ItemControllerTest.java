package ru.practicum.shareit.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Autowired
    private MockMvc mvc;

    @Test
    void getItemByIdwhenInvoked() {

        ReflectionTestUtils.setField(itemController, "itemService", itemService);

        ItemDto itemDto = makeItemDto();

        when(itemService.getItemDto(anyLong(), anyLong())).thenReturn(itemDto);

        ResponseEntity<ItemDto> response = itemController.getItemById(100L, 100L);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));

        assertThat(itemDto, equalTo(response.getBody()));
    }

    @Test
    void getAllShouldReturnList() throws Exception {

        ItemDto itemDto = makeItemDto();

        List<ItemDto> expectedItems = List.of(itemDto);

        when(itemService.getAll(anyInt(), anyInt(), anyLong())).thenReturn(expectedItems);

        mvc
                .perform(get("/items")
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.ALL)
                                 .param("from", "5")
                                 .param("size", "5")
                                 .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", equalTo(itemDto.getName())))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void createShouldReturnSameObject() throws Exception {

        ItemDto itemDto = makeItemDto();

        Item item = makeItem();

        when(itemService.save(any(), anyLong())).thenReturn(item);

        mvc
                .perform(post("/items")
                                 .content(mapper.writeValueAsString(itemDto))
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(itemDto.getName())));

    }

    @Test
    void createByWrongPersonThenEntityNotFoundExceptionThrown() throws Exception {

        ItemDto itemDto = makeItemDto();
        when(itemService.save(any(), anyLong())).thenThrow(new EntityNotFoundException("foo"));

        mvc
                .perform(post("/items")
                                 .content(mapper.writeValueAsString(itemDto))
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .header("X-Sharer-User-Id", "1"))

                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));

    }

    @Test
    void updateShouldReturnUpdatedEntity() throws Exception {

        ItemDto itemDto = makeItemDto();

        ItemPatchDto itemPatchDto = makePathDto();

        when(itemService.update(any(), anyLong())).thenReturn(itemDto);

        mvc
                .perform(patch("/items/{itemId}", 1L)
                                 .content(mapper.writeValueAsString(itemPatchDto))
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(itemDto.getName())));

    }

    @Test
    void searchByQueryShouldReturnListOfEntity() throws Exception {

        ItemDto itemDto = makeItemDto();

        List<ItemDto> expectedItems = List.of(itemDto);

        when(itemService.search(anyInt(), anyInt(), anyString())).thenReturn(expectedItems);

        mvc
                .perform(get("/items/search/")
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .param("from", "5")
                                 .param("size", "5")
                                 .param("text", "отВертКа")
                                 .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", equalTo(itemDto.getName())));
    }

    @Test
    void postCommentShouldReturnSameObject() throws Exception {

        Comment comment = makeComment();

        CommentDto commentDto = makeCommentDto();

        when(itemService.saveComment(any(), anyLong(), any())).thenReturn(comment);

        mvc
                .perform(post("/items/{itemId}/comment", 100L)
                                 .content(mapper.writeValueAsString(commentDto))
                                 .characterEncoding(StandardCharsets.UTF_8)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", equalTo(comment.getText())));

    }

    private ItemDto makeItemDto() {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(100L);
        itemDto.setName("thing");
        itemDto.setDescription("just simple thing");
        itemDto.setAvailable(true);
        itemDto.setRequestId(100L);
        itemDto.setLastBooking(new ItemLastBookingDto(100L, 100L));
        itemDto.setNextBooking(new ItemNextBookingDto(100L, 100L));

        CommentDto commentDto = new CommentDto(100L, "good thing", 101L, "Artur", LocalDateTime
                .now()
                .minusDays(100));

        itemDto.setComments(Set.of(commentDto));
        itemDto.setOwner(new UserDto());

        return itemDto;
    }

    private Item makeItem() {

        Item item = new Item();
        item.setId(100L);
        item.setName("thing");
        item.setDescription("just simple thing");
        item.setAvailable(true);
        item.setRequestId(100L);
        item.setOwner(new User());
        Booking bookingOne = makeBooking();
        Booking bookingTwo = makeBooking();
        bookingTwo.setStart(currentDate.plusDays(5));
        bookingTwo.setEnd(currentDate.plusDays(15));

        Set<Booking> itemBookings = Set.of(bookingOne, bookingTwo);

        item.setItemBookings(itemBookings);

        item.setItemComments(Set.of(makeComment()));

        return item;
    }

    private Booking makeBooking() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setStart(currentDate.minusDays(2));
        booking.setEnd(currentDate.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(new Item());
        booking
                .getBooker()
                .setId(100L);
        booking
                .getItem()
                .setId(100L);
        booking
                .getItem()
                .setOwner(makeUser());
        booking
                .getItem()
                .getOwner()
                .setId(100L);

        return booking;
    }

    private Comment makeComment() {

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("good thing");
        comment.setAuthor(makeUser());
        return comment;
    }

    private CommentDto makeCommentDto() {

        return new CommentDto(100L, "good thing", 100L, "Artur", currentDate.minusDays(1));

    }

    private User makeUser() {

        User user = new User();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        return user;
    }

    private ItemPatchDto makePathDto() {

        return new ItemPatchDto(100L, "UpdatedName", "updated description", true, 100L);

    }

}