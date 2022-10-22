package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

@Component
public class ItemMapper {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemMapper(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


   private User getOwner(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
       if (owner.isEmpty()) {
           throw new UserNotFoundException("");
       }  else {
               return owner.get();
           }
    }

    public Item toItem(ItemDto itemDto, Long userId) {
    User owner = getOwner(userId);
    return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemInfoDto toItemInfoDto(Item item) {
        ItemInfoDto result = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();

        //bookings and comments to add

        return result;
    }
}
