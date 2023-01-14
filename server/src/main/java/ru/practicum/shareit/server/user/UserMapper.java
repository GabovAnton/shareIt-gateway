package ru.practicum.shareit.server.user;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.server.item.Comment;
import ru.practicum.shareit.server.item.Item;

import java.util.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromUserUpdateDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

    @AfterMapping
    default void linkItems(@MappingTarget User user) {

        Set<Item> items = user.getItems();
        if (items != null) {
            items.stream().peek(item -> item.setOwner(user));
        }

    }

    @AfterMapping
    default void linkComments(@MappingTarget User user) {

        Set<Comment> comments = user.getComments();
        if (comments != null) {
            comments.stream().peek(Comment -> Comment.setAuthor(user));
        }
    }

}
