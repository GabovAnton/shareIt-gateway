package ru.practicum.shareit.server.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Autowired
    private JacksonTester<RequestDto> json;

    @Autowired
    private JacksonTester<UserDto> UserDtoJson;

    @Test
    void testRequestDto() throws Exception {

        JsonContent<RequestDto> result = json.write(makeRequestDto());
        JsonContent<UserDto> userDtoJsonContent = UserDtoJson.write(makeUserDto());

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("simple test description");
        assertThat(result)
                .extractingJsonPathValue("$.requester")
                .extracting("name")
                .isEqualTo("Artur");
        assertThat(result)
                .extractingJsonPathValue("$.requester")
                .extracting("email")
                .isEqualTo("artur@gmail.com");
        assertThat(result)
                .extractingJsonPathValue("$.requester")
                .extracting("registrationDate")
                .isEqualTo("2022-11-25T05:05:05");
        assertThat(result)
                .extractingJsonPathValue("$.requester")
                .extracting("id")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2022-12-09T05:05:05");
    }

    private UserDto makeUserDto() {

        UserDto user = new UserDto();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        user.setRegistrationDate(currentDate.minusDays(15));
        return user;
    }

    private RequestDto makeRequestDto() {

        return new RequestDto(100L, "simple test description", makeUserDto(), currentDate.minusDays(1)

        );

    }

}

