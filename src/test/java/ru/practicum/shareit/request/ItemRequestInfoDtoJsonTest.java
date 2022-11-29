package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestInfoDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestInfoDto> json;

    @Test
    void testItemRequestInfoDto() throws IOException {
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(1L, "TestDescription",
                LocalDateTime.now(), new ArrayList<>());

        JsonContent<ItemRequestInfoDto> result = json.write(itemRequestInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("TestDescription");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(Collections.emptyList());
    }
}
