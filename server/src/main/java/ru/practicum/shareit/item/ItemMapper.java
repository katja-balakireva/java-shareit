package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
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

    public Item toItem(ItemDto itemDto, Long userId, Optional<ItemRequest> itemRequest) {
        User owner = getOwner(userId);

        Item result = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();

        itemRequest.ifPresent(r -> result.setRequestId(itemRequest.get().getId()));
        return result;
    }

    public ItemInfoDto toItemInfoDto(Item item) {
        ItemInfoDto result = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();

        //bookings and comments to add
        Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());

        Optional<Booking> lastBooking = result.findLastBooking(bookings);
        lastBooking.ifPresent(booking -> result.setLastBooking(new ItemInfoDto.ItemBookingDto(booking.getId(),
                booking.getBooker().getId())));

        Optional<Booking> nextBooking = result.findNextBooking(bookings);
        nextBooking.ifPresent(booking -> result.setNextBooking(new ItemInfoDto.ItemBookingDto(booking.getId(),
                booking.getBooker().getId())));

        findAndSetComments(item, result);
        return result;
    }

    public ItemInfoDto toItemInfoDtoNotOwner(Item item) {
        ItemInfoDto result = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();

        //bookings and comments to add
        result.setLastBooking(null);
        result.setNextBooking(null);

        findAndSetComments(item, result);
        return result;
    }


    public ItemDto toItemDto(Item item) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getRequestId() != null) {
            result.setRequestId(item.getRequestId());
        }
        return result;

    }

    private void findAndSetComments(Item item, ItemInfoDto itemInfoDto) {
        List<CommentDto> comments =
                commentRepository.findAllByItemId(item.getId()).stream()
                        .map(commentMapper::toCommentDto)
                        .collect(Collectors.toList());

        if (comments.isEmpty()) {
            itemInfoDto.setComments(new ArrayList<>());
        } else {
            itemInfoDto.setComments(comments);
        }
    }

    private User getOwner(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            log.warn("Пользователь с id {} не найден", ownerId);
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            return owner.get();
        }
    }
}