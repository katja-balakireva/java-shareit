package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.custom.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(@Qualifier("DefaultBookingService")
                             BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingInfoDto> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", defaultValue = "all")
                                                       String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                       Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10")
                                                       Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return bookingService.getAllByUserId(userId, stateParam, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                        @RequestParam(name = "state", defaultValue = "all")
                                                        String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                        Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10")
                                                        Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        return bookingService.getAllByOwnerId(ownerId, stateParam, pageRequest);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getAllBookingsById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                     @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);

    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBooking(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                        @RequestParam Boolean approved,
                                        @PathVariable Long bookingId) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }
}
