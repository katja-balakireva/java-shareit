package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    @Qualifier("DefaultUserService")
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void testAddUser() throws Exception {
        UserDto userToAdd = new UserDto(1L, "name_1", "email_1@test.com");
        when(userService.addUser(any(UserDto.class))).thenReturn(userToAdd);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userToAdd.getId()))
                .andExpect(jsonPath("$.name").value(userToAdd.getName()))
                .andExpect(jsonPath("$.email").value(userToAdd.getEmail()))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userToUpdate = new UserDto(1L, "name_X", "email_X@test.com");
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userToUpdate);

        mockMvc.perform(patch("/users/{userId}", userToUpdate.getId())
                        .content(mapper.writeValueAsString(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userToUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userToUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userToUpdate.getEmail()))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, times(1)).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    void testGetAll() throws Exception {
        UserDto firstUser = new UserDto(1L, "name_1", "email_1@test.com");
        UserDto secondUser = new UserDto(2L, "name_2", "email_2@test.com");
        UserDto thirdUser = new UserDto(3L, "name_3", "email_3@test.com");

        List<UserDto> users = new ArrayList<>(List.of(firstUser, secondUser, thirdUser));

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(users))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(users.get(1).getEmail()))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetById() throws Exception {
        UserDto userToAdd = new UserDto(1L, "name_1", "email_1@test.com");
        when(userService.getById(anyLong())).thenReturn(userToAdd);

        mockMvc.perform(get("/users/{userId}", userToAdd.getId())
                        .content(mapper.writeValueAsString(userToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userToAdd.getId()))
                .andExpect(jsonPath("$.name").value(userToAdd.getName()))
                .andExpect(jsonPath("$.email").value(userToAdd.getEmail()))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, times(1)).getById(anyLong());
    }

    @Test
    void testDeleteUser() throws Exception {
        Long toDelete = 1L;
        mockMvc.perform(delete("/users/{userId}", toDelete))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(userService, times(1)).deleteUser(anyLong());
    }
}
