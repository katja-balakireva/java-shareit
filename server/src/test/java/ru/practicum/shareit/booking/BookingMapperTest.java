package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
public class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private static final LocalDateTime START = LocalDateTime.of(2023,
            1, 24, 8, 30, 10);
    private static final LocalDateTime END = LocalDateTime.of(2023,
            5, 24, 8, 30, 10);

    private User testOwner;
    private User testBooker;
    private Item testItem;
    private Booking testBooking;
    private BookingDto testBookingDto;

    @BeforeEach
    void setUp() {
        testOwner = userRepository.save(new User(1L, "OwnerName", "owner@owner.com"));
        testBooker = userRepository.save(new User(2L, "BookerName", "booker@booker.com"));
        testItem = itemRepository.save(new Item(1L, "ItemName", "ItemDescription",
                true, testOwner, null,
                new ArrayList<>()));
        testBooking = new Booking(1L, START, END, testItem, testBooker, State.APPROVED);
        testBookingDto = new BookingDto(1L, START, END, testItem.getId(), testBooker.getId(), State.APPROVED);
    }

    @Test
    void testToBooking() {
        Booking result = bookingMapper.toBooking(testBookingDto, testBooker.getId());

        assertEquals(testBooking.getId(), result.getId());
        assertEquals(testBooking.getStart(), result.getStart());
        assertEquals(testBooking.getEnd(), result.getEnd());
        assertEquals(testBooking.getItem().getId(), result.getItem().getId());
        assertEquals(testBooking.getBooker().getId(), result.getBooker().getId());
        assertEquals(testBooking.getStatus(), result.getStatus());
    }


    @Test
    void testToBookingDto() {
        BookingDto result = BookingMapper.toBookingDto(testBooking);

        assertEquals(testBookingDto.getId(), result.getId());
        assertEquals(testBookingDto.getStart(), result.getStart());
        assertEquals(testBookingDto.getEnd(), result.getEnd());
        assertEquals(testBookingDto.getItemId(), result.getItemId());
        assertEquals(testBookingDto.getBookerId(), result.getBookerId());
        assertEquals(testBookingDto.getStatus(), result.getStatus());
    }

    @Test
    void testToBookingInfoDto() {
        BookingInfoDto result = BookingMapper.toBookingInfoDto(testBooking);

        assertEquals(testBooking.getId(), result.getId());
        assertEquals(testBooking.getStart(), result.getStart());
        assertEquals(testBooking.getEnd(), result.getEnd());
        assertEquals(testBooking.getItem().getId(), result.getItem().getId());
        assertEquals(testBooking.getBooker().getId(), result.getBooker().getId());
        assertEquals(testBooking.getStatus(), result.getStatus());
    }
}
