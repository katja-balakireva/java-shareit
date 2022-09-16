package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private Set<User> users;
    private Long idCounter;

    public UserRepository() {
        this.users = new LinkedHashSet<>();
        this.idCounter = 0L;
    }

    public User addUser(User user) {
        Long userId = ++idCounter;
        user.setId(userId);
        users.add(user);
        return getById(userId);
    }

    public User getById(Long userId) {
        return users.stream()
                .filter(user -> userId.equals(user.getId()))
                .findAny().orElse(null);
    }

    public List<User> getAll() {
        return users.stream()
                .collect(Collectors.toList());
    }

    public User updateUser(Long userId, User user) {
        User userToUpdate = getById(userId);
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    public void deleteUser(Long userId) {
        User userToDelete = getById(userId);
        users.remove(userToDelete);
    }
}
