package ru.practicum.shareit.item;

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
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    @Qualifier("DefaultItemService")
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private static User testOwner;
    private static ItemInfoDto testItemInfoDto;
    private static CommentDto testComment;

    @BeforeAll
    static void setUp() {
        testOwner = new User(1L, "OwnerName", "ownerEmail@test.com");
        testComment = new CommentDto(1L, "CommentText", "CommentAuthor", LocalDateTime.of(2022,
                11, 24, 8, 30, 10));
        ItemInfoDto.ItemBookingDto lastBooking = new ItemInfoDto.ItemBookingDto(1L, 1L);
        ItemInfoDto.ItemBookingDto nextBooking = new ItemInfoDto.ItemBookingDto(2L, 1L);
        testItemInfoDto = new ItemInfoDto(1L, "ItemNameX", "ItemDescriptionX",
                true, testOwner, lastBooking,
                nextBooking, Arrays.asList(testComment), 2L);
    }

    @Test
    void testAddItem() throws Exception {

        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(testItemInfoDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItemInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItemInfoDto.getId()))
                .andExpect(jsonPath("$.name").value(testItemInfoDto.getName()))
                .andExpect(jsonPath("$.description").value(testItemInfoDto.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemInfoDto.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(testItemInfoDto.getOwner().getId()))
                .andDo(MockMvcResultHandlers.print());

        verify(itemService,
                times(1)).addItem(anyLong(),
                any(ItemDto.class));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(testItemInfoDto);

        mockMvc.perform(patch("/items/{itemId}", testItemInfoDto.getId())
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(testItemInfoDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON));

        verify(itemService, times(1)).updateItem(anyLong(),
                anyLong(), any(ItemDto.class));
    }

    @Test
    void testGetById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(testItemInfoDto);

        mockMvc.perform(get("/items/{itemId}", testItemInfoDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItemInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItemInfoDto.getId()))
                .andExpect(jsonPath("$.name").value(testItemInfoDto.getName()))
                .andExpect(jsonPath("$.description").value(testItemInfoDto.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemInfoDto.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(testItemInfoDto.getOwner().getId()))
                .andExpect(jsonPath("$.lastBooking.id").value(testItemInfoDto.getLastBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.id").value(testItemInfoDto.getNextBooking().getId()))
                .andExpect(jsonPath("$.requestId").value(testItemInfoDto.getRequestId()))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(itemService, times(1)).getById(anyLong(),
                anyLong());
    }

    @Test
    void testGetAll() throws Exception {
        when(itemService.getAll(1L, CustomPageRequest.of(0, 10)))
                .thenReturn(Arrays.asList(testItemInfoDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItemInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andDo(MockMvcResultHandlers.print());

        verify(itemService, times(1)).getAll(1L, CustomPageRequest.of(0, 10));
    }

    @Test
    void testDeleteItem() throws Exception {
        Long toDelete = 1L;
        mockMvc.perform(delete("/items/{itemId}", toDelete))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        verify(itemService, times(1)).deleteItem(anyLong());
    }

    @Test
    void testSearchItem() throws Exception {

        when(itemService.searchItem("ItemNameX",
                CustomPageRequest.of(0, 10))).thenReturn(
                Arrays.asList(testItemInfoDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "ItemNameX")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItemInfoDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testItemInfoDto.getId()))
                .andExpect(jsonPath("$[0].name").value(testItemInfoDto.getName()))
                .andExpect(jsonPath("$[0].description").value(testItemInfoDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(testItemInfoDto.getAvailable()))
                .andDo(MockMvcResultHandlers.print());

        verify(itemService, times(1)).searchItem("ItemNameX",
                CustomPageRequest.of(0, 10));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(testComment);

        mockMvc.perform(post("/items/{itemId}/comment", testItemInfoDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testComment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testComment.getId()))
                .andExpect(jsonPath("$.text").value(testComment.getText()))
                .andExpect(jsonPath("$.authorName").value(testComment.getAuthorName()))
                .andExpect(jsonPath("$.created").value((testComment.getCreated().toString())))
                .andDo(MockMvcResultHandlers.print());

        verify(itemService, times(1)).addComment(any(CommentDto.class), anyLong(), anyLong());
    }
}
