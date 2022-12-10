package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "UserName", "user@user.com");
        testUserDto = new UserDto(2L, "UserName_X", "userX@userX.com");
    }

    @Test
    void testToUserDto() {
        UserDto result = UserMapper.toUserDto(testUser);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void testToUser() {
        User result = UserMapper.toUser(testUserDto);

        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getName(), result.getName());
        assertEquals(testUserDto.getEmail(), result.getEmail());
    }
}
