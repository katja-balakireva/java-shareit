package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Optional;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Builder.Default private Long id = 0L;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    @Builder.Default private Optional<ItemRequest> request = Optional.empty();
}
