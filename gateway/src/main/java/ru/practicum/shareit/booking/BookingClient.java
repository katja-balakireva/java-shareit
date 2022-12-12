package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.custom.BookingDateException;
import ru.practicum.shareit.custom.UnsupportedStateException;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllBookingsByUserId(long userId,
                                                         String stateParam,
                                                         Integer from,
                                                         Integer size) {

        validateState(stateParam);

        Map<String, Object> params = Map.of("state", stateParam, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getAllBookingsByOwnerId(long ownerId,
                                                          String stateParam,
                                                          Integer from,
                                                          Integer size) {
        validateState(stateParam);
        Map<String, Object> params = Map.of("state", stateParam, "from", from, "size", size);
        return get("/owner?state={state}&from={from}&size={size}", ownerId, params);

    }

    public ResponseEntity<Object> getAllBookingsById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> addBooking(long bookerId, BookingDto bookingDto) {
        validateBookingDate(bookingDto);
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> updateBooking(long ownerId, Boolean approved, Long bookingId) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }

    /*VALIDATION METHODS*/

    private void validateState(String stateParam) {
        if (BookingState.from(stateParam).isEmpty()) throw new UnsupportedStateException(
                "Unknown state: " + stateParam);
    }

    private void validateBookingDate(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingDateException("Дата конца бронирования не может быть после даты начала");
        }
        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BookingDateException("Дата конца бронирования не может равна дате начала");
        }
    }
}
