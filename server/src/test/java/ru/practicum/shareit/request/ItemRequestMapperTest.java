package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ItemRequestMapperTest {
    @Autowired
    private ItemMapper itemMapper;

    private User testUser;
    private User testRequester;
    private ItemRequest testRequest;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "UserName", "user@user.com");
        testRequester = new User(2L, "RequesterName", "requester@req.com");
        testRequest = new ItemRequest(1L, "TestDescription", LocalDateTime.now(), testRequester);
        testItem = new Item(1L, "ItemName", "ItemDescription",
                true, testUser, null, new ArrayList<>());
    }

    @Test
    void testToItemRequestInfoDto() {
        ItemRequestInfoDto result = ItemRequestMapper.toItemRequestInfoDto(testRequest, getTestItemList(testItem));

        assertEquals(testRequest.getId(), result.getId());
        assertEquals(testRequest.getDescription(), result.getDescription());
        assertNotNull(result.getCreated());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testToItemRequestDto() {
        ItemRequestDto result = ItemRequestMapper.toItemRequestDto(testRequest);
        assertEquals(testRequest.getDescription(), result.getDescription());
    }

    @Test
    void testToItemRequest() {
        ItemRequestDto testRequestDto = ItemRequestMapper.toItemRequestDto(testRequest);
        ItemRequest result = ItemRequestMapper.toItemRequest(testRequestDto, testRequester);

        assertEquals(testRequest.getDescription(), result.getDescription());
        assertNotNull(result.getCreated());
        assertEquals(testRequest.getUser().getId(), result.getUser().getId());
    }

    private List<ItemDto> getTestItemList(Item item) {
        return List.of(item).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
