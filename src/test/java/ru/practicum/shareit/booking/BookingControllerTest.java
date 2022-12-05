package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    @Qualifier("DefaultBookingService")
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static final LocalDateTime START = LocalDateTime.of(2023,
            1, 24, 8, 30, 10);
    private static final LocalDateTime END = LocalDateTime.of(2023,
            5, 24, 8, 30, 10);

    private static User testBooker;
    private static User testOwner;
    private static Item testItem;
    private static BookingInfoDto testBooking;
    private static BookingDto testBookingDto;

    @BeforeAll
    static void setUp() {
        testBooker = new User(1L, "BookerName", "bookerEmail@test.com");
        testOwner = new User(2L, "OwnerName", "ownerEmail@test.com");
        testItem = new Item(1L, "TestItem", "TestDescription", true, testOwner, 1L,
                new ArrayList<>());
        testBooking = new BookingInfoDto(1L, START, END, testItem, testBooker, State.APPROVED);
        testBookingDto = new BookingDto(1L, START, END, testItem.getId(), testBooker.getId(), State.APPROVED);
    }

    @Test
    void testAddBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(testBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.start").value(testBooking.getStart().toString()))
                .andExpect(jsonPath("$.end").value(testBooking.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().toString()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookingService, times(1)).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void testUpdateBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(testBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", testBooking.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.start").value(testBooking.getStart().toString()))
                .andExpect(jsonPath("$.end").value(testBooking.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().toString()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookingService, times(1)).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void testGetAllBookingsByUserId() throws Exception {
        when(bookingService.getAllByUserId(anyLong(), anyString(), any())).thenReturn(Arrays.asList(testBooking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].start").value(testBooking.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(testBooking.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(testBooking.getStatus().toString()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookingService, times(1)).getAllByUserId(anyLong(), anyString(), any());
    }

    @Test
    void testGetAllBookingsByOwnerId() throws Exception {
        when(bookingService.getAllByOwnerId(anyLong(), anyString(), any())).thenReturn(Arrays.asList(testBooking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].start").value(testBooking.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(testBooking.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(testBooking.getStatus().toString()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookingService, times(1)).getAllByOwnerId(anyLong(), anyString(), any());
    }

    @Test
    void testGetAllBookingsById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(testBooking);

        mockMvc.perform(get("/bookings/{bookingId}", testBooking.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.start").value(testBooking.getStart().toString()))
                .andExpect(jsonPath("$.end").value(testBooking.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().toString()))
                .andDo(MockMvcResultHandlers.print());

        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }
}
