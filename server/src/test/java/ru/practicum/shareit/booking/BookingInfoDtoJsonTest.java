package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingInfoDtoJsonTest {

    @Autowired
    private JacksonTester<BookingInfoDto> json;

    @Test
    void testBookingInfoDto() throws IOException {
        Item item = new Item();
        User booker = new User();
        BookingInfoDto bookingInfoDto = new BookingInfoDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1), item, booker, State.APPROVED);

        JsonContent<BookingInfoDto> result = json.write(bookingInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotEmpty();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotEmpty();

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isNull();
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isNull();

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
