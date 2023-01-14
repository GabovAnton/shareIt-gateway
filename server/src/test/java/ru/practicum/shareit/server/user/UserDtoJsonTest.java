package ru.practicum.shareit.server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {

        JsonContent<UserDto> result = json.write(makeUserDto());

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(100);
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Artur");
        assertThat(result)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo("artur@gmail.com");
        assertThat(result)
                .extractingJsonPathStringValue("$.registrationDate")
                .isEqualTo("2022-11-25T05:05:05");
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
