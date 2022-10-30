package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultUserService")
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Получен список из {} пользователей: {}", result.size(), result);
        return result;
    }

    public UserDto getById(Long userId) {
        User user = validateAndReturnUser(userId);
        UserDto result = UserMapper.toUserDto(user);
        log.info("Получен пользователь с id {}: {}", userId, result);
        return result;
    }

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userToAdd = userRepository.save(user);
        UserDto result = UserMapper.toUserDto(userToAdd);
        log.info("Добавлен новый пользователь {}: ", result);
        return result;
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User userInDB = validateAndReturnUser(userId);

        if (userDto.getEmail() != null) {
            userInDB.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userInDB.setName(userDto.getName());
        }
        User userToUpdate = userRepository.save(userInDB);
        UserDto result = UserMapper.toUserDto(userToUpdate);
        log.info("Информация о пользователе с id {} обновлена: {}", userId, result);
        return result;
    }

    public void deleteUser(Long userId) {
        User user = validateAndReturnUser(userId);
        log.info("Пользователь с id {} удалён", userId);
        userRepository.delete(user);
    }

    private User validateAndReturnUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            return user.get();
        }
    }
}