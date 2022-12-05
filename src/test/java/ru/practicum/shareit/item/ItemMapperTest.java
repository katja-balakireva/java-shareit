package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User testOwner;
    private Item testItem;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        testOwner = userRepository.save(new User(1L, "OwnerName", "owner@owner.com"));
        testItem = itemRepository.save(new Item(1L, "ItemName", "ItemDescription",
                true, testOwner, null,
                new ArrayList<>()));
        testItemDto = new ItemDto(1L, null, "ItemName", "ItemDescription", true);
    }

    @Test
    void testToItem() {
        Item result = itemMapper.toItem(testItemDto, testOwner.getId(), Optional.empty());

        assertEquals(testItemDto.getId(), result.getId());
        assertEquals(testItemDto.getName(), result.getName());
        assertEquals(testItemDto.getDescription(), result.getDescription());
        assertEquals(testItemDto.getAvailable(), result.getAvailable());
        assertEquals(testItemDto.getRequestId(), result.getRequestId());
        assertNotNull(result.getOwner());
        assertNull(result.getComments());
    }

    @Test
    void testToItemInfoDto() {
        ItemInfoDto result = itemMapper.toItemInfoDto(testItem);

        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getName(), result.getName());
        assertEquals(testItem.getDescription(), result.getDescription());
        assertEquals(testItem.getAvailable(), result.getAvailable());
        assertEquals(testItem.getOwner().getId(), result.getOwner().getId());
        assertEquals(testItem.getRequestId(), result.getRequestId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void testToItemDto() {
        ItemDto result = itemMapper.toItemDto(testItem);

        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getRequestId(), result.getRequestId());
        assertEquals(testItem.getName(), result.getName());
        assertEquals(testItem.getDescription(), result.getDescription());
        assertEquals(testItem.getAvailable(), result.getAvailable());
    }
}
