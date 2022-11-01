package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    List<BookingInfoDto> getAllByUserId(Long userId, String state);

    List<BookingInfoDto> getAllByOwnerId(Long ownerId, String state);

    BookingInfoDto getById(Long bookingId, Long userId);

    BookingInfoDto addBooking(Long userId, BookingDto bookingDto);

    BookingInfoDto updateBooking(Long userId, Long bookingId, boolean approved);
}
