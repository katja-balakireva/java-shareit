package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.custom.ItemNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

@Component
@Slf4j
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository,
                         UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = getItem(bookingDto.getItemId());
        User booker = getBooker(bookerId);

        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {

        return BookingInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    private User getBooker(Long bookerId) {
        Optional<User> result = userRepository.findById(bookerId);
        if (result.isEmpty()) {
            log.warn("Пользователь с id {} не найден", bookerId);
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            return result.get();
        }
    }

    private Item getItem(Long itemId) {
        Optional<Item> result = itemRepository.findById(itemId);
        if (result.isEmpty()) {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ItemNotFoundException("Вещь не найдена");
        } else {
            return result.get();
        }
    }
}