package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.EntityNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public User getUser(long id) {

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException(
                                          "user with id: " + id + " doesn't exists"));
        log.debug("user with id: {} requested: {}", id, user);

        return user;
    }

    @Override
    public List<UserDto> getAll() {

        List<UserDto> userList = userRepository.findAll()
                                               .stream()
                                               .filter(Objects::nonNull)
                                               .map(UserMapper.INSTANCE::userToUserDto)
                                               .collect(Collectors.collectingAndThen(Collectors.toList(),
                                                                                     Collections::unmodifiableList));

        log.debug("all items requested: {}", userList.size());

        return userList;
    }

    @Override
    public User save(User user) {

        User savedUser = userRepository.save(user);

        log.debug("new user created: {}", savedUser);

        return savedUser;
    }

    @Override
    public UserDto update(UserUpdateDto userUpdateDto, Long userToUpdateId) {

        User userToUpdate = userRepository.findById(userToUpdateId)
                                          .orElseThrow(() -> new EntityNotFoundException(
                                                  "user id: " + userUpdateDto.getId() + " not found"));

        userMapper.updateUserFromUserUpdateDto(userUpdateDto, userToUpdate);

        User save = userRepository.save(userToUpdate);

        log.debug("user with id: {} updated: {}", userToUpdateId, save);

        return userMapper.userToUserDto(save);
    }

    @Override
    public boolean delete(Long userToDeleteId) {

        userRepository.deleteById(userToDeleteId);

        log.debug("user with id: {} deleted", userToDeleteId);

        return true;
    }

    @Override
    public Boolean existsById(long userId) {

        return userRepository.existsById(userId);
    }

}
