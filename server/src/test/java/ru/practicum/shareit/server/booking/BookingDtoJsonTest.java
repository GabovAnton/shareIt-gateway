package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.item.ItemNextBookingDto;
import ru.practicum.shareit.server.item.CommentDto;
import ru.practicum.shareit.server.item.ItemDto;
import ru.practicum.shareit.server.item.ItemLastBookingDto;
import ru.practicum.shareit.server.user.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<BookingDto> result = json.write(makeBookingDto());

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-12-08T05:05:05");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-12-12T05:05:05");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");

        assertThat(result).extractingJsonPathValue("$.booker").extracting("name").isEqualTo("Artur");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("email").isEqualTo("artur@gmail.com");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("registrationDate").isEqualTo(
                "2022-11-25T05:05:05");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("id").isEqualTo(100);

        assertThat(result).extractingJsonPathValue("$.item").extracting("id").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item").extracting("name").isEqualTo("thing");
        assertThat(result).extractingJsonPathValue("$.item").extracting("description").isEqualTo("just simple thing");
        assertThat(result).extractingJsonPathValue("$.item").extracting("available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.item").extracting("requestId").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking").extracting("id").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking").extracting("bookerId").isEqualTo(100);

        assertThat(result).extractingJsonPathValue("$.item.nextBooking").extracting("id").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking").extracting("bookerId").isEqualTo(100);

        assertThat(result).extractingJsonPathValue("$.item.comments[0]").extracting("id").isEqualTo(100);
        assertThat(result).extractingJsonPathValue("$.item.comments[0]").extracting("text").isEqualTo("good thing");
        assertThat(result).extractingJsonPathValue("$.item.comments[0]").extracting("authorId").isEqualTo(101);
        assertThat(result).extractingJsonPathValue("$.item.comments[0]").extracting("authorName").isEqualTo("Artur");
        assertThat(result).extractingJsonPathValue("$.item.comments[0]").extracting("created").isEqualTo(
                "2022-11-25T05:05:05");

        assertThat(result).extractingJsonPathValue("$.booker").extracting("name").isEqualTo("Artur");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("email").isEqualTo("artur@gmail.com");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("registrationDate").isEqualTo(
                "2022-11-25T05:05:05");
        assertThat(result).extractingJsonPathValue("$.booker").extracting("id").isEqualTo(100);

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

}