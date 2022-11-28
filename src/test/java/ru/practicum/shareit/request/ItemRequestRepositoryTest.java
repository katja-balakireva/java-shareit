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
    private ItemRequest otherRequest_1;
    private ItemRequest otherRequest_2;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        otherUser = createOtherUser();
        testRequest = createTestRequest();
        otherRequest_1 = createOtherRequest("text_1");
        otherRequest_2 = createOtherRequest("text_2");
    }

    @Test
    void testFindAllByUserId() {
        Collection<ItemRequest> result = requestRepository.findAllByUserId(testUser.getId(), REQ);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindAllOthersByUserId() {
        Collection<ItemRequest> result = requestRepository.findAllOthersByUserId(testUser.getId(), REQ);
        List<ItemRequest> resultList = new ArrayList<>(result);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(otherUser, resultList.get(0).getUser());
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
