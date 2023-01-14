package ru.practicum.shareit.server.user;

import java.util.List;

public interface UserService {

    User getUser(long id);

    List<UserDto> getAll();

    User save(User user);

    UserDto update(UserUpdateDto userUpdateDto, Long userToUpdateId);

    boolean delete(Long userToDeleteId);

    Boolean existsById(long userId);
}