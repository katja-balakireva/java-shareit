package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.custom.CustomBadRequestException;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.custom.ItemNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.custom.ValidateOwnershipException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    @Autowired
    private CommentMapper commentMapper;

    private User testUser;
    private User testBooker;
    private Item testItem;
    private ItemDto testItemDto;
    private ItemInfoDto testItemInfoDto;
    private ItemRequest testRequest;
    private Booking testBooking;
    private Comment testComment;

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
        testBooking = bookingRepository.save(new Booking(1L, LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(4), testItem, testBooker, State.APPROVED));
        testComment = commentRepository.save(new Comment(1L, "TestText", testItem.getId(), testBooker,
                LocalDateTime.now()));
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
        assertEquals(testItemInfoDto.getRequestId(), result.getRequestId());

        assertThrows(UserNotFoundException.class, () -> itemService.addItem(99L, testItemDto));
    }

    @Test
    void testAddItemNoRequest() {
        Item itemNoRequest = itemRepository.save(new Item(2L, "TestItem_2", "TestDescription_2",
                true, testUser, null, null));
        ItemInfoDto result = itemService.addItem(testUser.getId(), itemMapper.toItemDto(itemNoRequest));

        assertNotNull(result);
        assertEquals(itemNoRequest.getId(), result.getId());
        assertEquals(itemNoRequest.getName(), result.getName());
        assertEquals(itemNoRequest.getDescription(), result.getDescription());
        assertEquals(itemNoRequest.getAvailable(), result.getAvailable());
        assertEquals(itemNoRequest.getOwner().getId(), result.getOwner().getId());
        assertNull(result.getRequestId());

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
        assertThrows(ValidateOwnershipException.class, () -> itemService.updateItem(testBooker.getId(),
                testItem.getId(), itemToUpdate));
    }

    @Test
    void testUpdateItemNoName() {
        ItemDto itemToUpdate = new ItemDto(2L, null, null, "TestDescription_Upd",
                false);

        ItemInfoDto result = itemService.updateItem(testUser.getId(), testItem.getId(), itemToUpdate);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getName(), result.getName());
    }

    @Test
    void testUpdateItemNoDescription() {
        ItemDto itemToUpdate = new ItemDto(2L, null, "Some_name", null,
                false);

        ItemInfoDto result = itemService.updateItem(testUser.getId(), testItem.getId(), itemToUpdate);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getDescription(), result.getDescription());
    }

    @Test
    void testUpdateItemNoAvailable() {
        ItemDto itemToUpdate = new ItemDto(2L, null, "Some_name", "TestDescription_Upd",
                null);

        ItemInfoDto result = itemService.updateItem(testUser.getId(), testItem.getId(), itemToUpdate);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getAvailable(), result.getAvailable());
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
    void testSearchItemEmpty() {
        String searchText = "";
        List<ItemInfoDto> result = itemService.searchItem(searchText, CustomPageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.isEmpty());
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
    void testGetByIdNotOwner() {
        ItemInfoDto resultItem = itemMapper.toItemInfoDtoNotOwner(testItem);
        ItemInfoDto result = itemService.getById(resultItem.getId(), testBooker.getId());

        assertNotNull(result);
        assertEquals(testItemInfoDto.getId(), result.getId());
        assertEquals(testItemInfoDto.getName(), result.getName());
        assertEquals(testItemInfoDto.getDescription(), result.getDescription());
        assertEquals(testItemInfoDto.getAvailable(), result.getAvailable());
        assertEquals(testItemInfoDto.getOwner().getId(), result.getOwner().getId());
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
    void testAddCommentError() {
        assertThrows(CustomBadRequestException.class, () -> itemService.addComment(commentMapper.toCommentDto(testComment),
                testUser.getId(), testItem.getId()));
        assertThrows(ItemNotFoundException.class, () -> itemService.addComment(commentMapper.toCommentDto(testComment),
                testUser.getId(), 99L));
        assertThrows(UserNotFoundException.class, () -> itemService.addComment(commentMapper.toCommentDto(testComment),
                99L, testItem.getId()));
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