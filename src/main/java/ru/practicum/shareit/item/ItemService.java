package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    List<ItemInfoDto> getAll(Long userId);

    ItemInfoDto getById(Long itemId, Long userId);

    ItemInfoDto addItem(Long userId, ItemDto itemDto);

    ItemInfoDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long itemId);

    List<ItemInfoDto> searchItem(String text);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);
}
