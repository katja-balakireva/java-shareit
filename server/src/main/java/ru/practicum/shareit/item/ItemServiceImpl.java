package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.custom.CustomBadRequestException;
import ru.practicum.shareit.custom.ItemNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.custom.ValidateOwnershipException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultItemService")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper,
                           ItemRequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.requestRepository = requestRepository;
    }

    public List<ItemInfoDto> getAll(Long userId, PageRequest pageRequest) {
        validateUser(userId);
        List<ItemInfoDto> result =
                itemRepository.findByOwnerId(userId, pageRequest).stream()
                        .map(itemMapper::toItemInfoDto)
                        .sorted(Comparator.comparingLong(ItemInfoDto::getId))
                        .collect(Collectors.toList());
        log.info("?????????????? ???????????? ???? {} ??????????: {}", result.size(), result);
        return result;
    }

    public ItemInfoDto getById(Long itemId, Long userId) {
        validateUserIdIsNull(userId);
        validateUser(userId);

        Item item = validateAndReturnItem(itemId);
        ItemInfoDto result;
        if (item.getOwner().getId().equals(userId)) {
            result = itemMapper.toItemInfoDto(item);
        } else result = itemMapper.toItemInfoDtoNotOwner(item);

        log.info("???????????????? ???????? ?? id {}: {}", itemId, result);
        return result;
    }

    public ItemInfoDto addItem(Long userId, ItemDto itemDto) {
        validateUser(userId);
        Optional<ItemRequest> itemRequest = Optional.empty();
        if (itemDto.getRequestId() != null) {
            itemRequest =
                    requestRepository.findById(itemDto.getRequestId());
        }

        Item item = itemMapper.toItem(itemDto, userId, itemRequest);
        Item itemToAdd = itemRepository.save(item);
        ItemInfoDto result = itemMapper.toItemInfoDto(itemToAdd);
        log.info("???????????????????????? {} ?????????????? ?????????? ????????: {}", userId, result);
        return result;
    }

    public ItemInfoDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        validateUserIdIsNull(userId);
        validateUser(userId);

        itemDto.setId(itemId);

        Item itemInDB = validateAndReturnItem(itemId);
        validateOwner(itemInDB.getOwner().getId(), userId);

        if (itemDto.getName() != null) {
            itemInDB.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemInDB.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemInDB.setAvailable(itemDto.getAvailable());
        }

        Item itemToUpdate = itemRepository.save(itemInDB);
        ItemInfoDto result = itemMapper.toItemInfoDto(itemToUpdate);
        log.info("???????????????????????? {} ?????????????? ???????? ?? id {}: {}", userId, itemId, result);
        return result;
    }

    public void deleteItem(Long itemId) {
        Item item = validateAndReturnItem(itemId);
        log.info("???????? ?? id {} ??????????????", itemId);
        itemRepository.delete(item);
    }

    public List<ItemInfoDto> searchItem(String text, PageRequest pageRequest) {
        List<ItemInfoDto> result;

        if (text.isBlank() || text.isEmpty()) {
            result = new ArrayList<>();
        } else {
            result = itemRepository.findByNameContainsOrDescriptionContainsIgnoreCase(text, text, pageRequest).stream()
                    .filter(Item::getAvailable)
                    .map(itemMapper::toItemInfoDto)
                    .collect(Collectors.toList());
            log.info("?????????????????? ???????? {}: ", result);
        }
        return result;
    }

    /* COMMENTS METHODS */

    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        validateAndReturnItem(itemId);
        validateUser(userId);
        User author = userRepository.findById(userId).get();

        validateBookings(userId, itemId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.toComment(commentDto, itemId, author);
        Comment commentToAdd = commentRepository.save(comment);
        CommentDto result = commentMapper.toCommentDto(commentToAdd);
        return result;
    }

    /* VALIDATION METHODS */

    private void validateOwner(Long ownerId, Long userId) {
        if (!userId.equals(ownerId)) {
            log.warn("???????????????????????? ?? id {} ???? ???????????????? ???????????????????? ????????. id ??????????????????: {}", userId, ownerId);
            throw new ValidateOwnershipException("?????????????????? ???????? ?????????? ???????????? ????????????????");
        }
    }

    private Item validateAndReturnItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.warn("???????? ?? id {} ???? ??????????????", itemId);
            throw new ItemNotFoundException("???????? ???? ??????????????");
        } else {
            return item.get();
        }
    }

    private void validateUserIdIsNull(Long userId) {
        if (userId == null) {
            log.warn("???? ???????????? ???????????????????????? ?? id null");
            throw new UserNotFoundException("id ???????????????????????? ???? ?????? ??????????????");
        }
    }

    private void validateUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("???????????????????????? ?? id {} ???? ????????????", userId);
            throw new UserNotFoundException("???????????????????????? ???? ????????????");
        }
    }

    private void validateBookings(Long userId, Long itemId) {

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndItemId(userId, itemId);
        Collection<Booking> bookingsToRemove = new ArrayList<>();

        bookings.stream()
                .filter(booking -> booking.getStatus().equals(State.REJECTED) ||
                        booking.getEnd().isAfter(LocalDateTime.now()))
                .forEach(bookingsToRemove::add);
        bookings.removeAll(bookingsToRemove);

        if (bookings.isEmpty()) {
            log.warn("???????????????????????? ???? ??????????????");
            throw new CustomBadRequestException("???????????????????????? ???? ??????????????");
        }
    }
}
