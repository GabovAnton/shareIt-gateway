package ru.practicum.shareit.gateway.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import java.util.List;

@FeignClient(value = "userClient", path = "/users", url = "${SHAREIT_SERVER_URL}")
public interface UserClientFeign  {

    @GetMapping()
    List<UserDto> getUsers();

    @GetMapping(path= "{id}")
    UserDto getUser(@PathVariable("id") Long id);

    @PostMapping()
    UserDto create(@RequestBody UserDto userDto);

    @PatchMapping(path = "{userToUpdateId}")
    UserDto update(@PathVariable("userToUpdateId") Long userToUpdateId,
             @RequestBody UserUpdateDto userUpdateDto);

    @DeleteMapping(path = "{id}")
    Boolean delete(@PathVariable("id") Long id);
}
