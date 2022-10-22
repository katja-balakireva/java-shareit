package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

//    @GetMapping
//    public List<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
//                                        @RequestParam(name = "state", defaultValue = "all") String stateParam) {
//            State state = State.from(stateParam);
//        if (state == null) {
//            throw new IllegalArgumentException("Unknown state: " + stateParam);
//        }
//
//
//    }
}
