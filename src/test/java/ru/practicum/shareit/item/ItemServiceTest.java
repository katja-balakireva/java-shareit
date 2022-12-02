package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.custom.ItemNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    @Qualifier("DefaultItemService")
    private ItemServiceImpl itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private ItemMapper itemMapper;

    private User testUser;
    private User testBooker;
    private Item testItem;
    private ItemDto testItemDto;
    private ItemInfoDto testItemInfoDto;
    private ItemRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(1L, "name_1", "email_1@test.com"));
        testBooker = userRepository.save(new User(2L, "name_2", "email_2@test.com"));
        testRequest = requestRepository.save(new ItemRequest(1L, "test_description",
                LocalDateTime.now(), testBooker));
        testItem = itemRepository.save(new Item(1L, "TestItem", "TestDescription",
                true, testUser, testRequest.getId(), null));

        itemMapper = new ItemMapper(userRepository, bookingRepository, commentRepository, new CommentMapper());
        testItemDto = itemMapper.toItemDto(testItem);
        testItemInfoDto = itemMapper.toItemInfoDto(testItem);
    }

    @Test
    void testAddItem() {
        ItemInfoDto result = itemService.addItem(testUser.getId(), testItemDto);

        assertNotNull(result);
        assertEquals(testItemInfoDto.getId(), result.getId());
        assertEquals(testItemInfoDto.getName(), result.getName());
        assertEquals(testItemInfoDto.getDescription(), result.getDescription());
        assertEquals(testItemInfoDto.getAvailable(), result.getAvailable());
        assertEquals(testItemInfoDto.getOwner().getId(), result.getOwner().getId());

        assertThrows(UserNotFoundException.class, () -> itemService.addItem(99L, testItemDto));
    }

    @Test
    void testUpdateItem() {
        ItemDto itemToUpdate = new ItemDto(1L, null, "TestName_Upd", "TestDescription_Upd",
                false);

        Item fromStorage = itemRepository.findById(testItem.getId()).get();
        ItemInfoDto result = itemService.updateItem(testUser.getId(), testItem.getId(), itemToUpdate);

        assertNotNull(result);
        assertEquals(fromStorage.getId(), result.getId());
        assertEquals(fromStorage.getName(), result.getName());
        assertEquals(fromStorage.getDescription(), result.getDescription());
        assertEquals(fromStorage.getAvailable(), result.getAvailable());
        assertEquals(fromStorage.getOwner().getId(), result.getOwner().getId());

        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(99L, testItem.getId(),
                itemToUpdate));
        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(null, testItem.getId(),
                itemToUpdate));
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(testUser.getId(), 99L,
                itemToUpdate));
    }

    @Test
    void testSearchItem() {
        List<ItemInfoDto> result = itemService.searchItem("TestItem", CustomPageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(testItemInfoDto.getId(), result.get(0).getId());
        assertEquals(testItemInfoDto.getName(), result.get(0).getName());
        assertEquals(testItemInfoDto.getDescription(), result.get(0).getDescription());
        assertEquals(testItemInfoDto.getAvailable(), result.get(0).getAvailable());
        assertEquals(testItemInfoDto.getOwner().getId(), result.get(0).getOwner().getId());
    }

    @Test
    void testGetById() {
        ItemInfoDto result = itemService.getById(testItem.getId(), testUser.getId());

        assertNotNull(result);
        assertEquals(testItemInfoDto.getId(), result.getId());
        assertEquals(testItemInfoDto.getName(), result.getName());
        assertEquals(testItemInfoDto.getDescription(), result.getDescription());
        assertEquals(testItemInfoDto.getAvailable(), result.getAvailable());
        assertEquals(testItemInfoDto.getOwner().getId(), result.getOwner().getId());

        assertThrows(UserNotFoundException.class, () -> itemService.getById(testItem.getId(), 99L));
        assertThrows(UserNotFoundException.class, () -> itemService.getById(testItem.getId(), null));
        assertThrows(ItemNotFoundException.class, () -> itemService.getById(99L, testUser.getId()));

    }

    @Test
    void testGetAll() {
        List<ItemInfoDto> result = itemService.getAll(testUser.getId(), CustomPageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(testItemInfoDto.getId(), result.get(0).getId());
        assertEquals(testItemInfoDto.getName(), result.get(0).getName());
        assertEquals(testItemInfoDto.getDescription(), result.get(0).getDescription());
        assertEquals(testItemInfoDto.getAvailable(), result.get(0).getAvailable());
        assertEquals(testItemInfoDto.getOwner().getId(), result.get(0).getOwner().getId());

        assertThrows(UserNotFoundException.class, () -> itemService.getAll(99L, CustomPageRequest
                .of(0, 10)));
    }

    @Test
    void testDeleteItem() {
        Item itemToDelete = itemRepository.findById(testItem.getId()).get();
        itemService.deleteItem(itemToDelete.getId());
        Optional<Item> result = itemRepository.findById(itemToDelete.getId());

        assertTrue(result.isEmpty());
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(99L));
    }
}
