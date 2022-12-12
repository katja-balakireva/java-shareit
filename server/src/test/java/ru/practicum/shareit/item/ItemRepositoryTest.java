package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private static final CustomPageRequest REQ = CustomPageRequest.of(0, 10);

    private User testOwner;
    private User testRequester;
    private Item testItem;
    private ItemRequest testRequest;

    @BeforeEach
    void setUp() {
        testOwner = createTestOwner();
        testRequester = createTestRequester();
        testRequest = createTestRequest(testRequester);
        testItem = createTestItem(true, testOwner, testRequest.getId());
    }

    @Test
    public void testFindByIdAndOwner_Id() {
        Optional<Item> result = itemRepository.findByIdAndOwner_Id(testItem.getId(), testOwner.getId());

        assertTrue(result.isPresent());
        Item resultItem = result.get();
        assertEquals(testItem.getId(), resultItem.getId());
        assertEquals(testItem.getName(), resultItem.getName());
        assertEquals(testItem.getAvailable(), resultItem.getAvailable());
        assertEquals(testItem.getOwner().getId(), resultItem.getOwner().getId());
    }


    @Test
    public void testFindAllByRequestId() {
        List<Item> result = itemRepository.findAllByRequestId(testRequest.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItem.getRequestId(), result.get(0).getRequestId());
    }

    @Test
    public void testExistsByOwnerId() {
        assertTrue(itemRepository.existsByOwnerId(testOwner.getId()));
    }

    @Test
    public void testFindByNameContainsOrDescriptionContainsIgnoreCase() {
        Collection<Item> result =
                itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(
                        "est", "Descript", REQ);
        List<Item> resultList = new ArrayList<>(result);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(testItem.getName(), resultList.get(0).getName());
        assertEquals(testItem.getDescription(), resultList.get(0).getDescription());
    }

    @Test
    public void testFindByOwnerId() {
        Collection<Item> result = itemRepository.findByOwnerId(testOwner.getId(), REQ);
        List<Item> resultList = new ArrayList<>(result);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(testItem.getId(), resultList.get(0).getId());
        assertEquals(testItem.getName(), resultList.get(0).getName());
        assertEquals(testItem.getAvailable(), resultList.get(0).getAvailable());
        assertEquals(testItem.getOwner().getId(), resultList.get(0).getOwner().getId());
        assertEquals(testItem.getRequestId(), resultList.get(0).getRequestId());
    }


    private Item createTestItem(boolean available, User owner, Long requestId) {
        Item item = new Item();
        item.setName("TestName");
        item.setDescription("TestDescription");
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequestId(requestId);
        return em.persist(item);
    }

    private User createTestOwner() {
        User user = new User();
        user.setName("TestOwner");
        user.setEmail("testmail@mail.com");
        return em.persist(user);
    }

    private User createTestRequester() {
        User user = new User();
        user.setName("TestRequester");
        user.setEmail("testmailrrr@mail.com");
        return em.persist(user);
    }

    private ItemRequest createTestRequest(User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription("RequestDescription");
        request.setCreated(LocalDateTime.now());
        request.setUser(requester);
        return em.persist(request);
    }
}
