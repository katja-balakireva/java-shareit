package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRequestMapper {

    private ItemMapper itemMapper;

    @Autowired
    public ItemRequestMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDto> itemsResult = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        ItemRequestInfoDto result =
                ItemRequestInfoDto.builder()
                        .id(itemRequest.getId())
                        .description(itemRequest.getDescription())
                        .created(itemRequest.getCreated())
                        .items(itemsResult)
                        .build();

        return result;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest result = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .user(user)
                .build();
        return result;
    }
}
