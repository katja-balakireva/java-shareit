package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Component
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

//    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
//        Item item = itemRepository.findById(bookingDto.getItemId());
//        User user = userRepository.findById(bookerId);
//
//        return Booking.builder()
//                .start(bookingDto.getStart())
//                .end(bookingDto.getEnd())
//                .item(item)
//                .booker(user)
//                .build();
//    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {

        return BookingInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .state(booking.getState())
                .build();
    }

}
