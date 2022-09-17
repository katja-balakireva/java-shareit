package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
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
        List<UserDto> result = userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Получен список из {} пользователей: {}", result.size(), result);
        return result;
    }

    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId);
        validate(user);
        UserDto result = UserMapper.toUserDto(user);
        log.info("Получена пользователь с id {}: {}", userId, result);
        return result;
    }

    public UserDto addUser(UserDto userDto) {
        validate(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        User userToAdd = userRepository.addUser(user);
        UserDto result = UserMapper.toUserDto(userToAdd);
        log.info("Добавлен новый пользователь {}: ", result);
        return result;
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        validate(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        User userToUpdate = userRepository.updateUser(userId, user);
        validate(userToUpdate);
        UserDto result = UserMapper.toUserDto(userToUpdate);
        log.info("Информация о пользователе с id {} обновлена: {}", userId, result);
        return result;
    }

    public void deleteUser(Long userId) {
        User user = userRepository.getById(userId);
        validate(user);
        log.info("Пользователь с id {} удалён", userId);
        userRepository.deleteUser(userId);
    }

    /* VALIDATION METHODS */

    private void validate(String email) {
        for (User user : userRepository.getAll()) {
            if (user.getEmail().equals(email)) {
                log.warn("Выброшено исключение DuplicateEmailException");
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
    }

    private void validate(User userToValidate) {
        if (userToValidate == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }

        if (userRepository.getById(userToValidate.getId()) == null) {
            log.warn("Выброшено исключение UserNotFoundException");
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
