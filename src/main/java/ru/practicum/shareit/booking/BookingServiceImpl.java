package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.custom.BookingNotFoundException;
import ru.practicum.shareit.custom.CustomBadRequestException;
import ru.practicum.shareit.custom.ItemNotFoundException;
import ru.practicum.shareit.custom.UserNotFoundException;
import ru.practicum.shareit.custom.ValidateBookingOwnershipException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Component("DefaultBookingService")
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    public List<BookingInfoDto> getAllByUserId(Long userId, String state, PageRequest pageRequest) {
        validateAndReturnUser(userId);

        Collection<Booking> result = sortByStateAndBookerId(state, userId, pageRequest);
        log.info("Получен список бронирований пользователя {}", userId);
        return result.stream().map(BookingMapper::toBookingInfoDto)
                .sorted(Comparator.comparingLong(BookingInfoDto::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<BookingInfoDto> getAllByOwnerId(Long ownerId, String state, PageRequest pageRequest) {
        validateItemByOwner(ownerId);

        Collection<Booking> result = sortByStateAndOwnerId(state, ownerId, pageRequest);
        log.info("Получен список бронирований владельца {}", ownerId);
        return result.stream().map(BookingMapper::toBookingInfoDto)
                .sorted(Comparator.comparingLong(BookingInfoDto::getId).reversed())
                .collect(Collectors.toList());
    }

    public BookingInfoDto getById(Long bookingId, Long userId) {
        Booking bookingInDB = validateAndReturnBooking(bookingId);

        if (!userId.equals(bookingInDB.getItem().getOwner().getId()) &&
                !userId.equals(bookingInDB.getBooker().getId())) {
            log.info("Бронирование с id {} для пользователя {} не найдено", bookingId, userId);
            throw new BookingNotFoundException("Бронирование не найдено");
        }

        BookingInfoDto result = BookingMapper.toBookingInfoDto(bookingInDB);
        log.info("Получено бронирование пользователя с id {}: {}", userId, result);
        return result;
    }

    public BookingInfoDto addBooking(Long userId, BookingDto bookingDto) {
        checkDatesRange(bookingDto);
        Item itemInDB = validateAndReturnItem(bookingDto.getItemId());
        User userInDB = validateAndReturnUser(userId);
        validateOwnership(userInDB, itemInDB);

        bookingDto.setStatus(State.WAITING);
        Booking booking = bookingMapper.toBooking(bookingDto, userId);
        Booking bookingToAdd = bookingRepository.save(booking);
        BookingInfoDto result = BookingMapper.toBookingInfoDto(bookingToAdd);
        log.info("Пользователь {} добавил новое бронирование: {}", userId, result);
        return result;
    }

    public BookingInfoDto updateBooking(Long userId, Long bookingId, boolean approved) {
        Booking bookingInDB = validateAndReturnBooking(bookingId);

        if (!userId.equals(bookingInDB.getItem().getOwner().getId())) {
            log.info("Бронирование с id {} для пользователя {} не найдено", bookingId, userId);
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        checkState(bookingInDB, approved);

        Booking bookingToUpdate = bookingRepository.save(bookingInDB);
        BookingInfoDto result = BookingMapper.toBookingInfoDto(bookingToUpdate);
        log.info("Бронирование c id {} обновлено: {}", bookingId, result);
        return result;
    }

    /* STATE METHODS */

    private Collection<Booking> sortByStateAndBookerId(String state, Long bookerId, PageRequest pageRequest) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.getAllByBookerId(bookerId, LocalDateTime.now(), pageRequest);
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageRequest);
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageRequest);
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageRequest);
            default:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, State.from(state),
                        pageRequest);
        }
    }

    private Collection<Booking> sortByStateAndOwnerId(String state, Long ownerId, PageRequest pageRequest) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.getCurrentBookingsByOwnerId(ownerId, LocalDateTime.now(), pageRequest);
            case "PAST":
                return bookingRepository.getPastBookingsByOwnerId(ownerId, LocalDateTime.now(), pageRequest);
            case "FUTURE":
                return bookingRepository.getFutureBookingsByOwnerId(ownerId, LocalDateTime.now(), pageRequest);
            case "ALL":
                return bookingRepository.findAllByOwnerId(ownerId, pageRequest);
            default:
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, State.from(state), pageRequest);
        }
    }

    private void checkState(Booking booking, Boolean approved) {
        if (booking.getStatus().equals(State.APPROVED) ||
                booking.getStatus().equals(State.REJECTED)) {
            log.warn("Невозможно поменять статус у бронирования: {}", booking);
            throw new CustomBadRequestException("Невозможно поменять статус бронирования");
        }
        if (approved) {
            booking.setStatus(State.APPROVED);
        } else booking.setStatus(State.REJECTED);
    }

    /* VALIDATION METHODS */

    private User validateAndReturnUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        } else {
            return user.get();
        }
    }

    private Item validateAndReturnItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            log.warn("Вещь с id {} не найдена", itemId);
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (!item.get().getAvailable()) {
            log.warn("Вещь с id {} не доступна", itemId);
            throw new CustomBadRequestException("Вещь недоступна");
        }
        return item.get();
    }

    private void validateOwnership(User user, Item item) {
        if (user.getId().equals(item.getOwner().getId())) {
            log.warn("Вещь с id {} не доступна для бронирования владельцем", item.getId());
            throw new ValidateBookingOwnershipException("Владелец не может забронировать свою вещь");
        }
    }

    private void validateItemByOwner(Long ownerId) {
        if (!itemRepository.existsByOwnerId(ownerId)) {
            log.warn("Вещь с id владельца {} не найдена", ownerId);
            throw new ItemNotFoundException("Вещь не найдена");
        }
    }

    private Booking validateAndReturnBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            log.warn("Бронирование с id {} не найдено", bookingId);
            throw new BookingNotFoundException("Бронирование не найдено");
        } else {
            return booking.get();
        }
    }

    private void checkDatesRange(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.warn("Ошибка: дата конца перед датой начала");
            throw new CustomBadRequestException("Ошибка: дата конца перед датой начала");
        }
    }
}