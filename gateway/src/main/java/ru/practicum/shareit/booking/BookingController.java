package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all")
                                                         String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                         Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                                         Integer size) {
        log.info("Get all bookings by user id: {}, state: {}", userId, stateParam);
        return bookingClient.getAllBookingsByUserId(userId, stateParam, from, size);

    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                          @RequestParam(name = "state", defaultValue = "all")
                                                          String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                          Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10")
                                                          Integer size) {
        log.info("Get all bookings by owner id: {}, state: {}", ownerId, stateParam);
        return bookingClient.getAllBookingsByOwnerId(ownerId, stateParam, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getAllBookingsById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable Long bookingId) {
        log.info("Get all bookings by booking id: {}", bookingId);
        return bookingClient.getAllBookingsById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(name = "X-Sharer-User-Id") long bookerId,
                                             @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        log.info("Add booking: {}, booker id: {}", bookingDto, bookerId);
        return bookingClient.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
                                                @RequestParam Boolean approved,
                                                @PathVariable Long bookingId) {
        log.info("Update booking with id: {}, owner id: {}", bookingId, ownerId);
        return bookingClient.updateBooking(ownerId, approved, bookingId);
    }
}