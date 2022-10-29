package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemMapper(UserRepository userRepository,
                      BookingRepository bookingRepository,
                      CommentRepository commentRepository,
                      CommentMapper commentMapper
                      ) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
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
        Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());

        Optional<Booking> lastBooking = result.findLastBooking(bookings);
        lastBooking.ifPresent(booking -> result.setLastBooking(new ItemInfoDto.ItemBookingDto(booking.getId(),
                booking.getBooker().getId())));

        Optional<Booking> nextBooking = result.findNextBooking(bookings);
        nextBooking.ifPresent(booking -> result.setNextBooking(new ItemInfoDto.ItemBookingDto(booking.getId(),
                booking.getBooker().getId())));

        List<CommentDto> comments =
                commentRepository.findAllByItemId(item.getId()).stream()
                        .map(commentMapper::toCommentDto)
                        .collect(Collectors.toList());
        if (comments.isEmpty()) {
            result.setComments(new ArrayList<>());
        } else {
            result.setComments(comments);
        }
        return result;
    }

    public ItemInfoDto toItemInfoDtoNotOwner(Item item) {
        ItemInfoDto result = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();

        //bookings and comments to add
        Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());
        result.setLastBooking(null);
        result.setNextBooking(null);


        List<CommentDto> comments =
                commentRepository.findAllByItemId(item.getId()).stream()
                        .map(commentMapper::toCommentDto)
                        .collect(Collectors.toList());
        if (comments.isEmpty()) {
            result.setComments(new ArrayList<>());
        } else {
            result.setComments(comments);
        }
        return result;
    }

    private User getOwner(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new UserNotFoundException("");
        }  else {
            return owner.get();
        }
    }
}