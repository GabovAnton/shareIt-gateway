package ru.practicum.shareit.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;
    @InjectMocks
    UserController userController;
    LocalDateTime currentDate = LocalDateTime
            .of(2022, 12, 10, 5, 5, 5, 5);
    @Autowired
    private MockMvc mvc;

    @Test
    void getUserByIdwhenInvoked() {

        ReflectionTestUtils.setField(userController, "userService", userService);
        User user = makeUser();
        UserDto userDto = makeUserDto();
        when(userService.getUser(anyLong()))
                .thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUserById(100L);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(userDto.getName(), equalTo(Objects.requireNonNull(response.getBody()).getName()));
    }

    @Test
    void getAllShouldReturnList() throws Exception {

        UserDto userDto = makeUserDto();
        List<UserDto> expectedItems = List.of(userDto);
        when(userService.getAll())
                .thenReturn(expectedItems);
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", equalTo(userDto.getName())))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class));
    }

    @Test
    void createShouldReturnSameObject() throws Exception {
        UserDto userDto = makeUserDto();
        User user = makeUser();

        when(userService.save(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "100L"))
                .andDo(print())
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(userDto.getName())));
    }

    @Test
    void updateShouldReturnUpdatedEntity() throws Exception {
        UserDto userDto = makeUserDto();
        UserUpdateDto userUpdateDto = makeUserUpdateDto();

        when(userService.update(any(), anyLong()))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{userToUpdateId}", 100L)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "100"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo(userDto.getName())));

    }

    @Test
    void delete() throws Exception {

        when(userService.delete(anyLong()))
                .thenReturn(true);

        mvc.perform(MockMvcRequestBuilders
                        .delete("/users/{userToDeleteId}", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "100"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$", is(true)));

    }


    private User makeUser() {
        User user = new User();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");

        return user;
    }

    private UserDto makeUserDto() {
        UserDto user = new UserDto();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        user.setRegistrationDate(currentDate.minusDays(15));
        return user;
    }

    private UserUpdateDto makeUserUpdateDto() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("arturchik@gmail.com");
        userUpdateDto.setName("Arturchik");
        return userUpdateDto;
    }
}