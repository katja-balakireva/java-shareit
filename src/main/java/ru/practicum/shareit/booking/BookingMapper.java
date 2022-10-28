package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
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

    public BookingInfoDto toBookingInfoDto(Booking booking) {

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
            throw new UserNotFoundException("");
        }  else {
            return result.get();
        }
    }

    private Item getItem(Long itemId) {
        Optional<Item> result = itemRepository.findById(itemId);
        if (result.isEmpty()) {
            throw new ItemNotFoundException("");
        }  else {
            return result.get();
        }
    }
}