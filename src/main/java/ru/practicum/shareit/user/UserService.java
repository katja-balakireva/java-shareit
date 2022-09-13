package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.EmailValidationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(Long userId) {
        User result = userRepository.getById(userId);
        if (result == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else return result;
    }

    public User addUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new EmailValidationException("Неверный или отсутствующий email");
        }
        for (User u : getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
        userRepository.addUser(user);
        return user;
    }

    public User updateUser(Long userId, User user) {
        User result = userRepository.getById(userId);

        for (User u : getAll()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }

        if (result == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            userRepository.updateUser(userId, user);
        }
        return userRepository.getById(userId);
    }

    public void deleteUser(Long userId) {
        User result = userRepository.getById(userId);
        if (result == null) {
            throw new UserNotFoundException("Пользователь не найден");
        } else userRepository.deleteUser(userId);
    }
}

