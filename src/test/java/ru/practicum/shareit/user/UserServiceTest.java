package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.custom.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private static User testUser;
    private static User updatedTestUser;

    @BeforeAll
    static void setUp() {
        testUser = new User(1L, "name_1", "email_1@test.com");
        updatedTestUser = new User(1L, "name_Upd", "email_Upd@test.com");
    }

    @Test
    void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        UserDto userToAdd = UserMapper.toUserDto(testUser);
        UserDto result = userService.addUser(UserMapper.toUserDto(testUser));

        assertNotNull(result);
        assertEquals(userToAdd.getId(), result.getId());
        assertEquals(userToAdd.getName(), result.getName());
        assertEquals(userToAdd.getEmail(), result.getEmail());
    }


    @Test
    void testUpdateUser() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(updatedTestUser.getId(),
                UserMapper.toUserDto(testUser)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedTestUser);

        UserDto userToUpdate = UserMapper.toUserDto(updatedTestUser);
        UserDto result = userService.updateUser(testUser.getId(), UserMapper.toUserDto(testUser));

        assertNotNull(result);
        assertEquals(userToUpdate.getId(), result.getId());
        assertEquals(userToUpdate.getName(), result.getName());
        assertEquals(userToUpdate.getEmail(), result.getEmail());


    }

    @Test
    void testGetById() {
        assertThrows(UserNotFoundException.class, () -> userService.getById(testUser.getId()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        UserDto result = userService.getById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void testGetAll() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(List.of(UserMapper.toUserDto(testUser)).size(), result.size());
    }

    @Test
    void testDelete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        userService.deleteUser(testUser.getId());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}
