package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.custom.BookingNotFoundException;
import ru.practicum.shareit.custom.CustomPageRequest;
import ru.practicum.shareit.custom.UnsupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    private static final LocalDateTime START = LocalDateTime.of(2023,
            1, 24, 8, 30, 10);
    private static final LocalDateTime END = LocalDateTime.of(2023,
            5, 24, 8, 30, 10);

    private static User testOwner;
    private static User testBooker;
    private static Item testItem;
    private static Booking testBooking;

    @BeforeAll
    static void setUp() {
        testOwner = new User(1L, "name_1", "email_1@test.com");
        testBooker = new User(2L, "name_2", "email_2@test.com");
        testItem = new Item(1L, "TestItem", "TestDescription",
                true, testOwner, null, null);
        testBooking = new Booking(1L, START, END, testItem, testBooker, State.WAITING);
    }

    @Test
    void testAddBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testBooker));
        when(bookingRepository.save(any())).thenReturn(testBooking);

        BookingInfoDto bookingToAdd = BookingMapper.toBookingInfoDto(testBooking);
        BookingInfoDto result = bookingService.addBooking(testBooker.getId(), BookingMapper.toBookingDto(testBooking));

        assertNotNull(result);
        assertEquals(bookingToAdd.getId(), result.getId());
        assertEquals(bookingToAdd.getStart(), result.getStart());
        assertEquals(bookingToAdd.getEnd(), result.getEnd());
        assertEquals(bookingToAdd.getItem().getId(), result.getItem().getId());
        assertEquals(bookingToAdd.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingToAdd.getStatus().toString(), result.getStatus().toString());
    }

    @Test
    void testUpdateBooking() {
        Booking waitingBooking = new Booking(2L, START.plusMonths(1), END.plusMonths(2), testItem, testBooker, State.WAITING);
        Booking approvedBooking = new Booking(2L, START.plusMonths(1), END.plusMonths(2), testItem, testBooker, State.APPROVED);
        when(bookingRepository.save(any())).thenReturn(waitingBooking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(waitingBooking));
        when(bookingRepository.save(any())).thenReturn(approvedBooking);

        BookingInfoDto bookingToUpdate = BookingMapper.toBookingInfoDto(approvedBooking);
        BookingInfoDto result = bookingService.updateBooking(testOwner.getId(), approvedBooking.getId(), true);

        assertNotNull(result);
        assertEquals(bookingToUpdate.getId(), result.getId());
        assertEquals(bookingToUpdate.getStart(), result.getStart());
        assertEquals(bookingToUpdate.getEnd(), result.getEnd());
        assertEquals(bookingToUpdate.getItem().getId(), result.getItem().getId());
        assertEquals(bookingToUpdate.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingToUpdate.getStatus().toString(), result.getStatus().toString());
    }

    @Test
    void testGetById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        BookingInfoDto bookingToGet = BookingMapper.toBookingInfoDto(testBooking);
        BookingInfoDto result = bookingService.getById(testBooking.getId(), testBooker.getId());
        assertNotNull(result);
        assertEquals(bookingToGet.getId(), result.getId());
        assertEquals(bookingToGet.getStart(), result.getStart());
        assertEquals(bookingToGet.getEnd(), result.getEnd());
        assertEquals(bookingToGet.getItem().getId(), result.getItem().getId());
        assertEquals(bookingToGet.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingToGet.getStatus().toString(), result.getStatus().toString());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(testBooking.getId(), 99L));
    }

    @Test
    void testGetAllByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testBooker));
        Collection<BookingInfoDto> allBookings = List.of(BookingMapper.toBookingInfoDto(testBooking));

        when(bookingRepository.getAllByBookerId(anyLong(), any(), any())).thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultCurrent = bookingService.getAllByUserId(testOwner.getId(), "CURRENT",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultCurrent);
        assertEquals(allBookings, resultCurrent);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultAll = bookingService.getAllByUserId(testOwner.getId(), "ALL",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultAll);
        assertEquals(allBookings, resultAll);

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultFuture = bookingService.getAllByUserId(testOwner.getId(), "FUTURE",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultFuture);
        assertEquals(allBookings, resultFuture);

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultPast = bookingService.getAllByUserId(testOwner.getId(), "PAST",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultPast);
        assertEquals(allBookings, resultPast);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllByUserId(testOwner.getId(),
                "hello", CustomPageRequest.of(0, 10)));
    }

    @Test
    void testGetAllByOwnerId() {
        when(itemRepository.existsByOwnerId(testOwner.getId())).thenReturn(true);

        Collection<BookingInfoDto> allBookings = List.of(BookingMapper.toBookingInfoDto(testBooking));

        when(bookingRepository.getCurrentBookingsByOwnerId(anyLong(), any(), any())).thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultCurrent = bookingService.getAllByOwnerId(testOwner.getId(), "CURRENT",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultCurrent);
        assertEquals(allBookings, resultCurrent);

        when(bookingRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultAll = bookingService.getAllByOwnerId(testOwner.getId(), "ALL",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultAll);
        assertEquals(allBookings, resultAll);

        when(bookingRepository.getFutureBookingsByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultFuture = bookingService.getAllByOwnerId(testOwner.getId(), "FUTURE",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultFuture);
        assertEquals(allBookings, resultFuture);

        when(bookingRepository.getPastBookingsByOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(testBooking));
        Collection<BookingInfoDto> resultPast = bookingService.getAllByOwnerId(testOwner.getId(), "PAST",
                CustomPageRequest.of(0, 10));
        assertNotNull(resultPast);
        assertEquals(allBookings, resultPast);

        assertThrows(UnsupportedStateException.class, () -> bookingService.getAllByOwnerId(testOwner.getId(),
                "goodbye", CustomPageRequest.of(0, 10)));
    }
}
