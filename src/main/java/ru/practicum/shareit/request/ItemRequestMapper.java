package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ItemRequestMapper {

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestInfoDto result =
                ItemRequestInfoDto.builder()
                        .id(itemRequest.getId())
                        .description(itemRequest.getDescription())
                        .created(itemRequest.getCreated())
                        .items(items)
                        .build();
        return result;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto result = ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .build();

        return result;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest result = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .user(user)
                .build();
        return result;
    }
}
