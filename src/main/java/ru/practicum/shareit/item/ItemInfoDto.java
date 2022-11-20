package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId; //

    public Optional<Booking> findLastBooking(Collection<Booking> bookings) {
        List<Booking> result = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getEnd))
                .collect(Collectors.toList());

        return (result.isEmpty()) ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Booking> findNextBooking(Collection<Booking> bookings) {
        List<Booking> result = bookings.stream()
                .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());

        return (result.isEmpty()) ? Optional.empty() : Optional.of(result.get(0));
    }

    @AllArgsConstructor
    @Data
    public static class ItemBookingDto {
        Long id;
        Long bookerId;
    }
}


