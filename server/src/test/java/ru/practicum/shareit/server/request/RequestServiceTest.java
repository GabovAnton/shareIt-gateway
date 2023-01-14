package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserDto;
import ru.practicum.shareit.server.user.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    LocalDateTime currentDate = LocalDateTime.of(2022, 12, 10, 5, 5, 5, 5);

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<Request> requestArgumentCaptor;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService = new RequestServiceImpl(requestRepository, userService, requestMapper

    );

    @Test
    void getAllByWrongUserShouldThrowException() {

        ReflectionTestUtils.setField(requestService, "userService", userService);

        Long userId = 100L;
        when(userService.getUser(anyLong())).thenThrow(
                new EntityNotFoundException("user with id: " + userId + " doesn't exists"));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> {
            requestService.getAll(null,null,100L);
        });
        assertThat(entityNotFoundException.getMessage(), equalTo("user with id: " + userId + " doesn't exists"));

    }

    @Test
    void saveShouldNotThrowException() {

        ReflectionTestUtils.setField(requestService, "requestRepository", requestRepository);
        ReflectionTestUtils.setField(requestService, "userService", userService);
        ReflectionTestUtils.setField(requestService, "requestMapper", requestMapper);
        RequestDto requestDto = makeRequestDto();

        when(requestMapper.requestDtoToRequest(requestDto)).thenReturn(makeRequest());
        when(userService.getUser(anyLong())).thenReturn(makeUser());

        requestService.saveRequest(requestDto, 100L);

        Mockito.verify(requestRepository).save(requestArgumentCaptor.capture());
        Request savedRequest = requestArgumentCaptor.getValue();

        assertThat(savedRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(savedRequest.getId(), equalTo(requestDto.getId()));

    }

    private UserDto makeUserDto() {

        UserDto user = new UserDto();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");
        user.setRegistrationDate(currentDate.minusDays(15));
        return user;
    }

    private User makeUser() {

        User user = new User();
        user.setId(100L);
        user.setName("Artur");
        user.setEmail("artur@gmail.com");

        return user;
    }

    private RequestDto makeRequestDto() {

        return new RequestDto(100L, "simple test description", makeUserDto(), currentDate.minusDays(1)

        );

    }

    private Request makeRequest() {

        Request request = new Request();
        request.setDescription("simple test description");
        request.setRequester(makeUser());
        request.setCreated(currentDate.minusDays(1));
        request.setId(100L);
        return request;
    }

}