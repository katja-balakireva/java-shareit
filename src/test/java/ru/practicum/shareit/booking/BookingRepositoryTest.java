package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private static final LocalDateTime START = LocalDateTime.of(2023,
            1, 24, 8, 30, 10);
    private static final LocalDateTime END = LocalDateTime.of(2023,
            5, 24, 8, 30, 10);
    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2023,
            3, 24, 8, 30, 10);
    private static final CustomPageRequest REQ = CustomPageRequest.of(0, 10);

    private User testUser;
    private User testOwner;
    private Item testItem;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testOwner = createTestOwner();
        testItem = createTestItem(testOwner);
        lastBooking = createTestBooking(START, END, State.APPROVED);
        nextBooking = createTestBooking(START.plusMonths(3), END.plusMonths(3),State.WAITING);
    }

    @Test
    void testFindAllByOwnerId() {
        Collection<Booking> result =
                bookingRepository.findAllByOwnerId(testOwner.getId(), REQ);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindAllByOwnerIdAndStatus() {
        Collection<Booking> result = bookingRepository.findAllByOwnerIdAndStatus(testOwner.getId(), State.WAITING, REQ);
        List<Booking> resultList = new ArrayList<>(result);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(State.WAITING, resultList.get(0).getStatus());
    }

    @Test
    void testGetFutureBookingsByOwnerId() {
        Collection<Booking> result = bookingRepository.getFutureBookingsByOwnerId(testOwner.getId(),
                LocalDateTime.now(), REQ);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPastBookingsByOwnerId() {
        Collection<Booking> result = bookingRepository.getPastBookingsByOwnerId(testOwner.getId(),
                LocalDateTime.now(), REQ);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testGetCurrentBookingsByOwnerId() {
        Collection<Booking> result = bookingRepository.getCurrentBookingsByOwnerId(testOwner.getId(),
                LocalDateTime.now(), REQ);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllByBookerId() {
        Collection<Booking> result = bookingRepository.getAllByBookerId(testUser.getId(), TEST_TIME, REQ);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    private User createTestUser() {
        User user = new User();
        user.setName("TestUserName");
        user.setEmail("TestUser@test.com");
        return em.persist(user);
    }

    private User createTestOwner() {
        User user = new User();
        user.setName("TestOwnerName");
        user.setEmail("TestOwner@test.com");
        return em.persist(user);
    }

    private Item createTestItem(User owner) {
        Item item = new Item();
        item.setName("TestItemName");
        item.setDescription("TestItemDescription");
        item.setAvailable(true);
        item.setOwner(owner);
        return em.persist(item);
    }

    private Booking createTestBooking(LocalDateTime start, LocalDateTime end, State state) {
        Booking booking = new Booking();
        booking.setItem(testItem);
        booking.setBooker(testUser);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(state);
        return em.persist(booking);
    }

}
