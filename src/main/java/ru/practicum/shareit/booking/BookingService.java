package ru.practicum.shareit.booking;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.CustomBadRequestException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidateOwnershipException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    public List<BookingInfoDto> getAllByUserId(Long userId, String state) {
        //   validateAndReturnUser(userId);
        //verify
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("не найден");
        }
        Collection<Booking> result = sortByStateAndBookerId(state, userId);
        return result.stream().map(bookingMapper::toBookingInfoDto)
                .sorted(Comparator.comparingLong(BookingInfoDto::getId).reversed())
                .collect(Collectors.toList());
    }

    public List<BookingInfoDto> getAllByOwnerId(Long ownerId, String state) {
        //   validateAndReturnUser(userId);
        //verify
        if (!itemRepository.existsByOwnerId(ownerId)) {
            throw new ItemNotFoundException("не найден");
        }

        Collection<Booking> result = sortByStateAndOwnerId(state, ownerId);
        return result.stream().map(bookingMapper::toBookingInfoDto)
                .sorted(Comparator.comparingLong(BookingInfoDto::getId).reversed())
                .collect(Collectors.toList());
    }

    public BookingInfoDto getById(Long bookingId, Long userId) {
        Booking bookingInDB = bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException("бронирование не найдено"));

        if (!userId.equals(bookingInDB.getItem().getOwner().getId()) &&

                !userId.equals(bookingInDB.getBooker().getId())) {
            throw new BookingNotFoundException("бронирование не найдено");
        }
        BookingInfoDto result = bookingMapper.toBookingInfoDto(bookingInDB);
        return result;
    }

    public BookingInfoDto addBooking(Long userId, BookingDto bookingDto) {

        checkDatesRange(bookingDto);
        Item itemInDB = itemRepository.findById
                (bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("не найдено"));

        if (!itemInDB.getAvailable()) {
            throw new CustomBadRequestException("400");
        }
        User userInDB = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("не найден"));

        if (userInDB.getId().equals(itemInDB.getOwner().getId())) {
            throw new BookingNotFoundException("владелец не может забронировать свою вещь");
        }
        bookingDto.setStatus(State.WAITING);
        Booking booking = bookingMapper.toBooking(bookingDto, userId);
        Booking bookingToAdd = bookingRepository.save(booking);
        BookingInfoDto result = bookingMapper.toBookingInfoDto(bookingToAdd);
        log.info("Пользователь {} добавил новое бронирование: {}", userId, result);
        return result;
    }

    public BookingInfoDto updateBooking(Long userId, Long bookingId, boolean approved) {
        Booking bookingInDB = bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException("бронирование не найдено"));

        if (!userId.equals(bookingInDB.getItem().getOwner().getId())) {

            throw new BookingNotFoundException("бронирование не найдено");
        }

        if (bookingInDB.getStatus().equals(State.APPROVED) ||
                bookingInDB.getStatus().equals(State.REJECTED)) {
            throw new CustomBadRequestException("нельзя поменять статус"); // 400, bad request
        }

        if (approved) {
            bookingInDB.setStatus(State.APPROVED);
        } else bookingInDB.setStatus(State.REJECTED);

        Booking bookingToUpdate = bookingRepository.save(bookingInDB);
        BookingInfoDto result = bookingMapper.toBookingInfoDto(bookingToUpdate);
        log.info("Бронирование обновлено: {}", result);
        return result;
    }

    /* STATE METHODS */

    private Collection<Booking> sortByStateAndBookerId(String state, Long bookerId) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.getAllByBookerId(bookerId, LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now());
            case "ALL":
                return bookingRepository.findAllByBookerId(bookerId);
            default:
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, State.from(state));
        }
    }

    private Collection<Booking> sortByStateAndOwnerId(String state, Long ownerId) {
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookingRepository.getCurrentBookingsByOwnerId(ownerId, LocalDateTime.now());
            case "PAST":
                return bookingRepository.getPastBookingsByOwnerId(ownerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.getFutureBookingsByOwnerId(ownerId, LocalDateTime.now());
            case "ALL":
                return bookingRepository.findAllByOwnerId(ownerId);
            default:
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, State.from(state));
        }
    }

    /* VALIDATION METHODS */

    private void checkDatesRange(BookingDto bookingDto) {

if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
    throw new CustomBadRequestException("дата конца перед датой начала");
}
    }
}