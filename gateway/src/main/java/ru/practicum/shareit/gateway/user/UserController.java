package ru.practicum.shareit.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

   // private final UserClient userClient;

    @Autowired
    private UserService userService;

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable long userId) {

        log.info("Getting user {}", userId);
        return userService.getUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {

        log.info("Getting all users");
        return userService.getUsers();
    }

    @PostMapping()
    public UserDto create(@Valid @RequestBody UserDto userDto) {

        log.info("Create user {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("{userToUpdateId}")
    public UserDto update(
            @PathVariable long userToUpdateId, @Valid @RequestBody UserUpdateDto userUpdateDto) {

        return userService.update(userToUpdateId, userUpdateDto);
    }

    @DeleteMapping("{userToDeleteId}")
    public Boolean delete(@PathVariable long userToDeleteId) {
        log.info("Delete user Id {}", userToDeleteId);

        return userService.delete(userToDeleteId);
    }

}
