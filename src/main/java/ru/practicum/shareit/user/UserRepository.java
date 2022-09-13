package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private Map<Long, User> userMap;
    private Long idCounter;

    public UserRepository() {
        this.userMap = new HashMap<>();
        this.idCounter = 0L;
    }

    List<User> getAll() {
       return userMap.values().stream()
             //  .map(UserMapper::toUserDto)
               .collect(Collectors.toList());
    }

    User getById(Long userId) {
      return userMap.entrySet().stream()
                .filter(user -> userId.equals(user.getKey()))
                .map(Map.Entry::getValue)
              //  .map(UserMapper::toUserDto)
                .findAny().orElse(null);
    }

    User addUser(User user) {
        Long id = ++idCounter;
        user.setId(id);
       // User user = UserMapper.toUser(userDto, id);
        userMap.put(id, user);
        return userMap.get(id);
    }

    User updateUser(Long userId, User user) {
       User userToUpdate = userMap.get(userId);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
       return userMap.get(userId);
    }

    void deleteUser(Long userId) {
        userMap.remove(userId);
    }
}
