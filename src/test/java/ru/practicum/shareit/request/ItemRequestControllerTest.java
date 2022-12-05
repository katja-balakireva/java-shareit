package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @MockBean
    @Qualifier("DefaultRequestService")
    private RequestService requestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static final LocalDateTime CREATED = LocalDateTime.of(2023,
            5, 24, 8, 30, 10);

    private static ItemDto testItemDto;
    private static User testOwner;
    private static User testRequester;
    private static ItemRequestInfoDto testRequestInfoDto;
    private static ItemRequest testRequest;


    @BeforeAll
    static void setUp() {
        testOwner = new User(1L, "OwnerName", "ownerEmail@test.com");
        testRequester = new User(2L, "RequesterName", "requesterEmail@test.com");
        testItemDto = new ItemDto(1L, 1L, "TestItem", "TestDescription", true);
        testRequest = new ItemRequest(1L, "TestDescription", CREATED, testRequester);
        testRequestInfoDto = new ItemRequestInfoDto(testRequest.getId(), testRequest.getDescription(),
                testRequest.getCreated(), Collections.singletonList(testItemDto));
    }

    @Test
    void testAddItemRequest() throws Exception {
        when(requestService.addItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(testRequestInfoDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testRequestInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRequestInfoDto.getId()))
                .andExpect(jsonPath("$.description").value(testRequestInfoDto.getDescription()))
                .andExpect(jsonPath("$.created").value(testRequestInfoDto.getCreated().toString()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(requestService, times(1)).addItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void testGetByRequestId() throws Exception {
        when(requestService.getByRequestId(anyLong(), anyLong()))
                .thenReturn(testRequestInfoDto);

        mockMvc.perform(get("/requests/{requestId}", testRequestInfoDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testRequestInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRequestInfoDto.getId()))
                .andExpect(jsonPath("$.description").value(testRequestInfoDto.getDescription()))
                .andExpect(jsonPath("$.created").value(testRequestInfoDto.getCreated().toString()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(requestService, times(1)).getByRequestId(anyLong(), anyLong());
    }

    @Test
    void testGetAllByUserId() throws Exception {
        when(requestService.getAllByUserId(anyLong(), any()))
                .thenReturn(Collections.singletonList(testRequestInfoDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testRequestInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRequestInfoDto.getId()))
                .andExpect(jsonPath("$[0].description").value(testRequestInfoDto.getDescription()))
                .andExpect(jsonPath("$[0].created").value(testRequestInfoDto.getCreated().toString()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(requestService, times(1)).getAllByUserId(anyLong(), any());
    }

    @Test
    void testGetAllRequestsNotOwner() throws Exception {
        when(requestService.getAllRequestsNotOwner(anyLong(), any()))
                .thenReturn(Collections.singletonList(testRequestInfoDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testRequestInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRequestInfoDto.getId()))
                .andExpect(jsonPath("$[0].description").value(testRequestInfoDto.getDescription()))
                .andExpect(jsonPath("$[0].created").value(testRequestInfoDto.getCreated().toString()))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(requestService, times(1)).getAllRequestsNotOwner(anyLong(), any());
    }
}
