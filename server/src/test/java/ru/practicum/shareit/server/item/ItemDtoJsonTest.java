package ru.practicum.shareit.server.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.booking.BookingDto;
import ru.practicum.shareit.server.user.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {

        JsonContent<ItemDto> result = json.write(makeItemDto());

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(100);

        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("thing");

        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("just simple thing");

        assertThat(result)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);

        assertThat(result)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathValue("$.lastBooking")
                .extracting("id")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathValue("$.lastBooking")
                .extracting("bookerId")
                .isEqualTo(100);

        assertThat(result)
                .extractingJsonPathValue("$.nextBooking")
                .extracting("id")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathValue("$.nextBooking")
                .extracting("bookerId")
                .isEqualTo(100);

    }

    private BookingDto makeBookingDto() {

        BookingDto dto = new BookingDto();
        dto.setId(100L);
        dto.setStart(currentDate.minusDays(2));
        dto.setEnd(currentDate.plusDays(2));
        dto.setStatus(BookingStatus.REJECTED);
        dto.setItem(makeItemDto());
        dto.setBooker(makeUserDto());
        return dto;
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

        CommentDto commentDto = new CommentDto(100L, "good thing", 101L, "Artur", currentDate.minusDays(15));

        itemDto.setComments(Set.of(commentDto));
        itemDto.setOwner(new UserDto());

        return itemDto;
    }

    private UserDto makeUserDto() {

        UserDto user = new UserDto();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        user.setRegistrationDate(currentDate.minusDays(15));
        return user;
    }

    private CommentDto makeCommentDto() {

        return new CommentDto(100L, "good thing", 101L, "Artur", currentDate.minusDays(1));
    }

}
