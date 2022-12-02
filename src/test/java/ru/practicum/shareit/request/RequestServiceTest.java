package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.custom.RequestNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class RequestServiceTest {

    @Autowired
    @Qualifier("DefaultRequestService")
    private RequestServiceImpl requestService;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemMapper itemMapper;

    private static final CustomPageRequest REQ = CustomPageRequest.of(0, 10);

    private User testOwner;
    private User testRequester;
    private Item testItem;
    private ItemRequest testRequest;
    private ItemRequestDto testRequestDto;
    private ItemRequestInfoDto testRequestInfoDto;

    @BeforeEach
    void setUp() {
        testOwner = userRepository.save(new User(1L, "name_1", "email_1@test.com"));
        testRequester = userRepository.save(new User(2L, "name_2", "email_2@test.com"));
        testRequest = itemRequestRepository.save(new ItemRequest(1L, "Test_Description",
                LocalDateTime.now(), testRequester));

        testItem = itemRepository.save(new Item(1L, "TestItem", "TestDescription",
                true, testOwner, testRequest.getId(), null));
        List<ItemDto> testResultList = Stream.of(testItem).map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        testRequestDto = ItemRequestMapper.toItemRequestDto(testRequest);
        testRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(testRequest, testResultList);
    }


    @Test
    void testAddItemRequest() {
        ItemRequestInfoDto result = requestService.addItemRequest(testRequester.getId(), testRequestDto);

        assertNotNull(result);
        assertEquals(testRequestInfoDto.getDescription(), result.getDescription());
        assertThrows(UserNotFoundException.class, () -> requestService.addItemRequest(-99L, testRequestDto));
        assertThrows(UserNotFoundException.class, () -> requestService.addItemRequest(99L, testRequestDto));
    }

    @Test
    void testGetByRequestId() {
        ItemRequestInfoDto result = requestService.getByRequestId(testRequester.getId(), testRequest.getId());

        assertNotNull(result);
        assertEquals(testRequestInfoDto.getId(), result.getId());
        assertEquals(testRequestInfoDto.getDescription(), result.getDescription());
        assertEquals(testRequestInfoDto.getCreated(), result.getCreated());
        assertEquals(testRequestInfoDto.getItems().size(), result.getItems().size());

        assertThrows(UserNotFoundException.class, () -> requestService.getByRequestId(-99L, testRequest.getId()));
        assertThrows(UserNotFoundException.class, () -> requestService.getByRequestId(99L, testRequest.getId()));
        assertThrows(RequestNotFoundException.class, () -> requestService.getByRequestId(testRequester.getId(),
                99L));
    }

    @Test
    void testGetAllByUserId() {
        List<ItemRequestInfoDto> result = requestService.getAllByUserId(testRequester.getId(), REQ);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(testRequestInfoDto.getId(), result.get(0).getId());
        assertEquals(testRequestInfoDto.getDescription(), result.get(0).getDescription());
        assertEquals(testRequestInfoDto.getCreated(), result.get(0).getCreated());
        assertEquals(testRequestInfoDto.getItems().size(), result.get(0).getItems().size());

        assertThrows(UserNotFoundException.class, () -> requestService.getAllByUserId(99L, REQ));
    }

    @Test
    void testGetAllRequestsNotOwner() {
        //all requests except user's
        List<ItemRequestInfoDto> result = requestService.getAllRequestsNotOwner(testRequester.getId(), REQ);

        assertTrue(result.isEmpty());
        assertEquals(result.size(), 0);

        assertThrows(UserNotFoundException.class, () -> requestService.getAllRequestsNotOwner(-99L, REQ));
        assertThrows(UserNotFoundException.class, () -> requestService.getAllRequestsNotOwner(99L, REQ));
    }
}
