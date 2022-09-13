package ru.practicum.shareit.user;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

@Component
@NoArgsConstructor
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
    public static User toUser(UserDto userDto, Long userId) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }
}
