package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository requestRepository;
    private static final CustomPageRequest REQ = CustomPageRequest.of(0, 10);

    private User testUser;
    private User otherUser;
    private ItemRequest testRequest;
    private ItemRequest otherRequestFirst;
    private ItemRequest otherRequestSecond;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        otherUser = createOtherUser();
        testRequest = createTestRequest();
        otherRequestFirst = createOtherRequest("text_1");
        otherRequestSecond = createOtherRequest("text_2");
    }

    @Test
    void testFindAllByUserId() {
        Collection<ItemRequest> result = requestRepository.findAllByUserId(testUser.getId(), REQ);
        List<ItemRequest> resultList = new ArrayList<>(result);
        assertNotNull(result);
        assertEquals(1, resultList.size());
        assertEquals(testRequest.getId(), resultList.get(0).getId());
        assertEquals(testRequest.getDescription(), resultList.get(0).getDescription());
        assertEquals(testRequest.getUser(), resultList.get(0).getUser());
        assertEquals(testRequest.getCreated(), resultList.get(0).getCreated());
    }

    @Test
    void testFindAllOthersByUserId() {
        List<ItemRequest> resultList = new ArrayList<>(requestRepository.findAllOthersByUserId(testUser.getId(), REQ));
        ItemRequest first = resultList.get(0);
        ItemRequest second = resultList.get(1);

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(otherUser, first.getUser());
        assertEquals(otherUser, second.getUser());

        assertTrue(otherRequestFirst.getId().equals(first.getId()) || otherRequestFirst.getId().equals(
                second.getId()));
        assertTrue(otherRequestFirst.getDescription().equals(first.getDescription()) || otherRequestFirst
                .getDescription().equals(second.getDescription()));
        assertTrue(otherRequestFirst.getUser().equals(first.getUser()) || otherRequestFirst.getUser().equals(
                second.getUser()));
        assertTrue(otherRequestFirst.getCreated().equals(first.getCreated()) || otherRequestFirst.getCreated()
                .equals(second.getCreated()));
    }

    private User createTestUser() {
        User user = new User();
        user.setName("TestUserName");
        user.setEmail("TestUser@test.com");
        return em.persist(user);
    }

    private User createOtherUser() {
        User user = new User();
        user.setName("OtherUserName");
        user.setEmail("OtherUser@test.com");
        return em.persist(user);
    }

    private ItemRequest createTestRequest() {
        ItemRequest request = new ItemRequest();
        request.setDescription("TestDescription");
        request.setUser(testUser);
        request.setCreated(LocalDateTime.now());
        return em.persist(request);
    }

    private ItemRequest createOtherRequest(String description) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setUser(otherUser);
        request.setCreated(LocalDateTime.now());
        return em.persist(request);
    }
}
