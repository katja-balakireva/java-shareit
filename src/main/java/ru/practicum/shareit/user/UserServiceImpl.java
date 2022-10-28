package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultUserService")
@Slf4j
@Transactional
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
//        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
//                "Пользователь не найден"));
        UserDto result = UserMapper.toUserDto(user);
        log.info("Получен пользователь с id {}: {}", userId, result);
        return result;
    }

   // @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userToAdd = userRepository.save(user);
        UserDto result = UserMapper.toUserDto(userToAdd);
        log.info("Добавлен новый пользователь {}: ", result);
        return result;
    }

   // @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {

        User userInDB = validateAndReturnUser(userId);

//        User userInDB = userRepository.findById(userId).get();
//        validate(userInDB);
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
   //  @Transactional
    public void deleteUser(Long userId) {
        User user = validateAndReturnUser(userId);

//                User user = userRepository.findById(userId).get();
//        validate(user);
        log.info("Пользователь с id {} удалён", userId);
        userRepository.delete(user);
    }

//    private void validate(User userToValidate) {
//        if (userToValidate == null) {
//            log.warn("Пользователь не найден: {}", userToValidate);
//            throw new UserNotFoundException("Пользователь не найден");
//        }
//
//        if (userRepository.findById(userToValidate.getId()).isEmpty()) {
//            log.warn("Пользователь не найден: {}", userToValidate);
//            throw new UserNotFoundException("Пользователь не найден");
//        }
//    }

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
