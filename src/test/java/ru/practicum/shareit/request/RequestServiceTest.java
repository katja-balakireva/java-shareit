package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        testRequestDto = ItemRequestMapper.toItemRequestDto(testRequest);
        testRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(testRequest, List.of(testItem));
    }


    @Test
    void testAddItemRequest() {
        ItemRequestInfoDto result = requestService.addItemRequest(testRequester.getId(), testRequestDto);

        assertNotNull(result);
        assertEquals(testRequestInfoDto.getDescription(), result.getDescription());
    }

    @Test
    void testGetByRequestId() {
        ItemRequestInfoDto result = requestService.getByRequestId(testRequester.getId(), testRequest.getId());

        assertNotNull(result);
        assertEquals(testRequestInfoDto.getId(), result.getId());
        assertEquals(testRequestInfoDto.getDescription(), result.getDescription());
        assertEquals(testRequestInfoDto.getCreated(), result.getCreated());
        assertEquals(testRequestInfoDto.getItems().size(), result.getItems().size());
    }

    @Test
    void testGetAllByUserId() {
        List<ItemRequestInfoDto> result = requestService.getAllByUserId(testRequester.getId(),
                CustomPageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(testRequestInfoDto.getId(), result.get(0).getId());
        assertEquals(testRequestInfoDto.getDescription(), result.get(0).getDescription());
        assertEquals(testRequestInfoDto.getCreated(), result.get(0).getCreated());
        assertEquals(testRequestInfoDto.getItems().size(), result.get(0).getItems().size());
    }

    @Test
    void testGetAllRequestsNotOwner() {
        //all requests except user's
        List<ItemRequestInfoDto> result = requestService.getAllRequestsNotOwner(testRequester.getId(),
                CustomPageRequest.of(0, 10));

        assertTrue(result.isEmpty());
        assertEquals(result.size(), 0);
    }
}