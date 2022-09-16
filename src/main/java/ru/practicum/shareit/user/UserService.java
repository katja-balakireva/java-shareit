package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.EmailValidationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else return UserMapper.toUserDto(user);
    }

    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new EmailValidationException("Неверный или отсутствующий email");
        }
        for (User u : userRepository.getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
        User userToAdd = userRepository.addUser(user);
        return UserMapper.toUserDto(userToAdd);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {

        User user = UserMapper.toUser(userDto);

        for (User u : userRepository.getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
        if (userRepository.getById(userId) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
           User userToUpdate = userRepository.updateUser(userId, user);
           return UserMapper.toUserDto(userToUpdate);
        }
    }

    public void deleteUser(Long userId) {
        User result = userRepository.getById(userId);
        if (result == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else userRepository.deleteUser(userId);
    }
}

