package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAll(Long userId);

    ItemDto getById(Long itemId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long itemId);

    List<ItemDto> searchItem(String text);
}
