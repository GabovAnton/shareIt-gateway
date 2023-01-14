package ru.practicum.shareit.gateway.booking;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.practicum.shareit.gateway.booking.dto.BookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingStatus;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;
import ru.practicum.shareit.server.booking.Booking;


import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@WireMockTest(httpPort = 8089)
@SpringBootTest(properties = "SHAREIT_SERVER_URL=http://localhost:8089")

@ExtendWith(SpringExtension.class)
class BookingServiceTest {
    LocalDateTime currentDate = LocalDateTime
            .of(2022, 12, 10, 5, 5, 5, 5);

    /*@Autowired
    CacheManager cacheManager;*/
    @Autowired
    BookingFeignClient bookingFeignClient;

    @Test
    void createShouldReturnNewDto() {
        stubFor(post("/bookings")
                .withRequestBody(equalToJson("{\"name\": \"Artur\",\"email\": \"artur@gmail.com\"}", true, true))
                .withHeader("X-Sharer-User-Id", new StringValuePattern("1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("oneUser-response.json")));
        UserDto user = bookingFeignClient.create(makeUserDto());

        assertThat(user).isNotNull()
                .extracting(UserDto::getName)
                .isEqualTo("Artur");
    }


    private BookingDto makeBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setBooker(makeUserDto());
        bookingDto.setStart(currentDate.minusDays(2));
        bookingDto.setEnd(currentDate.plusDays(2));
        bookingDto.setStatus(String.valueOf(BookingStatus.WAITING));
        bookingDto.getBooker().setId(100L);
        bookingDto.getItem().setId(100L);
        bookingDto.getItem().setOwner(makeUserDto());
        bookingDto.getItem().getOwner().setId(100L);

        return bookingDto;
    }

    private UserDto makeUserDto() {
        UserDto user = new UserDto();
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        return user;
    }
}

