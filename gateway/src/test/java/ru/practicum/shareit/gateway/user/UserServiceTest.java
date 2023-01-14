package ru.practicum.shareit.gateway.user;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@WireMockTest(httpPort = 8089)
@SpringBootTest(properties = "SHAREIT_SERVER_URL=http://localhost:8089")

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    LocalDateTime currentDate = LocalDateTime
            .of(2022, 12, 10, 5, 5, 5, 5);
    @Autowired
    private UserClientFeign userClientFeign;

    @Test
    void getUsersShouldReturnUsersList() {
        stubFor(get("/users")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("users-response.json")));
        List<UserDto> users = userClientFeign.getUsers();

        assertThat(users).isNotEmpty()
                .hasSize(1)
                .extracting(UserDto::getName)
                .contains("Artur");
    }

    @Test
    void getUserShouldReturnDto() {
        stubFor(get("/users/1")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("oneUser-response.json")));

        UserDto user = userClientFeign.getUser(1L);
        assertThat(user).isNotNull()
                .extracting(UserDto::getName)
                .isEqualTo("Artur");
    }

    @Test
    void createShouldReturnNewDto() {
        stubFor(post("/users")
                .withRequestBody(equalToJson("{\"name\": \"Artur\",\"email\": \"artur@gmail.com\"}", true, true))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("oneUser-response.json")));
        UserDto user = userClientFeign.create(makeUserDto());

        assertThat(user).isNotNull()
                .extracting(UserDto::getName)
                .isEqualTo("Artur");
    }

    @Test
    void updateShouldReturnUpdatedDto() {
        stubFor( com.github.tomakehurst.wiremock.client.WireMock.patch(urlEqualTo("/users/1"))
                .withRequestBody(equalToJson("{\"id\" : null,\"items\" : null,\"name\" : \"Arturchik\",\"email\" : \"artur@gmail.com\",\"registrationDate\" : null,\"comments\" : null}", true, true))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("updateUser-response.json")));
        UserDto user = userClientFeign.update(1L,makeUserUpdateDto());

        assertThat(user).isNotNull()
                .extracting(UserDto::getName)
                .isEqualTo("Arturchik");
    }

    @Test
    void deleteShoudReturnTrue() {
        stubFor( com.github.tomakehurst.wiremock.client.WireMock.delete("/users/1")
                .willReturn(ok("true").withHeader("Content-Type", "application/json")));
    assertThat(userClientFeign.delete(1L)).isTrue();
    }

    private UserDto makeUserDto() {
       UserDto user = new UserDto();
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        return user;
    }

    private UserUpdateDto makeUserUpdateDto() {
        UserUpdateDto user = new UserUpdateDto();
        user.setName("Arturchik");
        user.setEmail("artur@gmail.com");
        return user;
    }
}