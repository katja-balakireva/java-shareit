package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemInfoDtoJsonTest {
    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @Test
    void testItemInfoDto() throws IOException {
        User user = new User(1L, "UserName", "user@user.com");
        ItemInfoDto itemInfoDto = new ItemInfoDto(1L, "TestName", "TestDescription", true,
                user, null, null, new ArrayList<>(), 2L);

        JsonContent<ItemInfoDto> result = json.write(itemInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("TestName");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("TestDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();

        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("UserName");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("user@user.com");

        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isNullOrEmpty();
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isNullOrEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(Collections.emptyList());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }
}