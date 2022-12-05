package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface BookingService {

    List<BookingInfoDto> getAllByUserId(Long userId, String state, PageRequest pageRequest);

    List<BookingInfoDto> getAllByOwnerId(Long ownerId, String state, PageRequest pageRequest);

    BookingInfoDto getById(Long bookingId, Long userId);

    BookingInfoDto addBooking(Long userId, BookingDto bookingDto);

    BookingInfoDto updateBooking(Long userId, Long bookingId, boolean approved);
}
